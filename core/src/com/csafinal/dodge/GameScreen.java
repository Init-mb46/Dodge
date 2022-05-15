package com.csafinal.dodge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.sun.org.apache.xpath.internal.operations.Mult;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class GameScreen implements Screen {
	final Dodge game;

	Texture dropImg;
	Texture fastDropImg;
	Texture multiDropImg;
	Texture bucketImg;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<RainDrop> rainDrops;
	Array<PointsText> pointUpdates;

	long lastDropTime;
	int dropsCollected;
	long gameStartTime;

	public GameScreen(final Dodge game) {
		this.game = game;

		dropImg = new Texture(Gdx.files.internal("drop.png"));
		fastDropImg = new Texture(Gdx.files.internal("fastdrop.png"));
		multiDropImg = new Texture(Gdx.files.internal("multidrop.png"));
		bucketImg = new Texture(Gdx.files.internal("bucket.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("dropsound.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rainmusic.mp3"));

		rainMusic.setLooping(true);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800,480);

		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		rainDrops = new Array<>();
		pointUpdates = new Array<>();
	}

	@Override
	public void render (float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "Drop Points: " + dropsCollected, 20, 460);
		int seconds = (int) (TimeUtils.millis() - gameStartTime) / 1000;
		String t = String.format("%02d:%02d", seconds / 60, seconds % 60);
		game.font.draw(game.batch, t ,20, 430);
		game.batch.draw(bucketImg, bucket.x, bucket.y);
		for (RainDrop drop : rainDrops) {
			if (drop.quantity > 1){
				MultiDrop dropp = (MultiDrop) drop;
				for (int i = 0; i < dropp.quantity; i++) {
					game.batch.draw(multiDropImg, (float) (drop.x + (dropp.offsets[i] * 35)), (float) (drop.y + (dropp.offsets[i] * 20)));
				}
			} else if (drop.speedMultiplier >1.02){
				game.batch.draw(fastDropImg, drop.x, drop.y);
			} else {
				game.batch.draw(dropImg, drop.x, drop.y);
			}
		}
		for (PointsText pt : pointUpdates) {
			game.font.draw(game.batch, pt.text, pt.x, pt.y);
		}
		game.batch.end();

		if (Gdx.input.isTouched()) {
			Vector3 pos = new Vector3();
			pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(pos);
			bucket.x = (int) (pos.x - 64 / 2);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 400 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 400 * Gdx.graphics.getDeltaTime();
		clampRectToScreen(bucket);

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			int deltaG = (int) ((TimeUtils.millis() - gameStartTime) / 1000);
			int numDrops = MathUtils.clamp((int) MathUtils.log(MathUtils.E,deltaG * .2f), 1, 7);
			for (int i = 0; i < numDrops; i++) spawnRaindrop(deltaG);
		}

		for (Iterator<RainDrop> iter = rainDrops.iterator(); iter.hasNext();) {
			RainDrop drop = iter.next();
			drop.y -= 200 * drop.speedMultiplier * Gdx.graphics.getDeltaTime();
			if (drop.y + 64 < 0) {
				iter.remove();
			}
			if (drop.overlaps(bucket) && drop.y > bucket.y + bucket.height - 35) {
				dropSound.play();
				iter.remove();
				dropsCollected+= drop.value;
				PointsText pt = new PointsText("+" + drop.value, (int) drop.x, (int) drop.y, TimeUtils.millis());
				pointUpdates.add(pt);
			}
		}

		for (Iterator<PointsText> iter = pointUpdates.iterator(); iter.hasNext();) {
			PointsText pt = iter.next();
			pt.y += 50 * delta;
			if (TimeUtils.millis() - pt.timeCreated > PointsText.lifetime) {
				iter.remove();
			}
		}
	}

	@Override
	public void show() {
		rainMusic.play();
		gameStartTime = TimeUtils.millis();
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose () {
		dropImg.dispose();
		bucketImg.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		fastDropImg.dispose();
		multiDropImg.dispose();
	}

	public void spawnRaindrop(int deltaT) {
		double chance = .06 * ( 1 + MathUtils.log(MathUtils.E, deltaT * .2f));
		RainDrop drop;
		if (MathUtils.random() > chance || deltaT < 20 ) {
			drop = new RainDrop();
		} else if (deltaT >= 40) {
			if (MathUtils.random() < .3) {
				drop = new MultiDrop(MathUtils.random(2,4 * (1 + (int) (MathUtils.log(MathUtils.E, deltaT* .2f)))));
				MultiDrop x = (MultiDrop) drop;
				x.initOffsets((float) MathUtils.clamp(MathUtils.log(MathUtils.E, MathUtils.log(MathUtils.E, deltaT * .2f) * .2f), 0.1, 0.3));
			}
			else
				drop = new FastDrop(MathUtils.random(1.4f, (float) (2 + chance * 2)));
		} else {
			drop = new FastDrop(MathUtils.random(1, (float) (2 + chance * 2)));
		}
		drop.init(MathUtils.random(0, 800 - 64), MathUtils.random(480, 700));
		drop.width = drop.quantity > 1 ? 32 : 64;
		drop.height = drop.quantity > 1 ? 32 : 64;
		rainDrops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
	}
	public void clampRectToScreen(Rectangle obj) {
		obj.x = obj.x < 0 ? 0 : (int) (obj.x > 800 - obj.getWidth() ? 800 - obj.getWidth() : obj.x);
		obj.y = obj.y < 0 ? 0 : (int) (obj.y > 480 - obj.getHeight() ? 480 - obj.getHeight() : obj.y);
	}
}
