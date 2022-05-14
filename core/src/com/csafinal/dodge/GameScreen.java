package com.csafinal.dodge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.awt.*;
import java.util.Iterator;

public class GameScreen implements Screen {
	final Dodge game;

	Texture dropImg;
	Texture bucketImg;
	Sound dropSound;
	Music rainMusic;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Rectangle> rainDrops;
	long lastDropTime;
	int dropsCollected;
	long gameStartTime;

	public GameScreen(final Dodge game) {
		this.game = game;

		gameStartTime = TimeUtils.millis();
		dropImg = new Texture(Gdx.files.internal("drop.png"));
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
		spawnRaindrop();
	}

	@Override
	public void render (float delta) {
		ScreenUtils.clear(0, 0, 0.2f, 1);
		camera.update();

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "Drops Collected: " + dropsCollected, 20, 460);
		int seconds = (int) (TimeUtils.millis() - gameStartTime) / 1000;
		String t = String.format("%02d:%02d", seconds / 60, seconds % 60);
		game.font.draw(game.batch, t ,20, 430);
		game.batch.draw(bucketImg, bucket.x, bucket.y);
		for (Rectangle drop : rainDrops) {
			game.batch.draw(dropImg, drop.x, drop.y);
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
			int numDrops = (int) MathUtils.log(MathUtils.E,deltaG * .4f);
			for (int i = 0; i < numDrops; i++) spawnRaindrop();
			spawnRaindrop();
		}

		for (Iterator<Rectangle> iter = rainDrops.iterator(); iter.hasNext();) {
			Rectangle drop = iter.next();
			drop.y -= 200 * Gdx.graphics.getDeltaTime();
			if(drop.y + 64 < 0) iter.remove();

			if(drop.intersects(bucket)) {
				dropSound.play();
				iter.remove();
				dropsCollected++;
			}
		}
	}

	@Override
	public void show() {
		rainMusic.play();
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
	}

	public void spawnRaindrop() {
		Rectangle drop = new Rectangle();
		drop.x = MathUtils.random(0,800-64);
		drop.y = MathUtils.random(480,700);
		drop.width = 64;
		drop.height = 64;
		rainDrops.add(drop);
		lastDropTime = TimeUtils.nanoTime();
	}
	public void clampRectToScreen(Rectangle obj) {
		obj.x = obj.x < 0 ? 0 : (int) (obj.x > 800 - obj.getWidth() ? 800 - obj.getWidth() : obj.x);
		obj.y = obj.y < 0 ? 0 : (int) (obj.y > 480 - obj.getHeight() ? 480 - obj.getHeight() : obj.y);
	}

}
