package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private int estadoDoJogo = 0;
	private BitmapFont font;
	private BitmapFont mensagem;
	private int pontuacao;
	private boolean marcouPonto = false;
	private Circle passaroCirculo;
	private Rectangle retangoloCanoTopo;
	private Rectangle retangoloCanoBaixo;
	//private ShapeRenderer shapeRenderer;

	private float larguraDoDispositivo;
	private float alturaDoDispositivo;
	private float velocidadeQueda=0;
	private float posicaoIncialVertical;
	private Texture canoBaixo;
	private Texture gameOver;
	private Texture playButton;
	private Texture canoTopo;
	private float posicaoMovimentoCanoHorizonatal;
	private float espacoCanos;
	private float deltaTime;
	private Random random;
	private float alturaEntreCanosRandomica;
	private float variacao=0;

	//camera
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 768;
	private final float VIRTUAL_HEIGHT = 1024;


	
	@Override
	public void create () {

		batch = new SpriteBatch();
		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");
		fundo = new Texture("fundo.png");
		gameOver = new Texture("game_over.png");
		playButton = new Texture("play.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(4);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(2);

		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");

		random = new Random();
		passaroCirculo = new Circle();
		//retangoloCanoTopo = new Rectangle();
		//retangoloCanoBaixo = new Rectangle();
		//shapeRenderer = new ShapeRenderer();

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
		viewport= new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);



		larguraDoDispositivo=  VIRTUAL_WIDTH;
		alturaDoDispositivo = VIRTUAL_HEIGHT;
		posicaoIncialVertical= alturaDoDispositivo/2;
		posicaoMovimentoCanoHorizonatal = larguraDoDispositivo -100;
		espacoCanos = 280;


	}

	@Override
	public void render () {
		camera.update();
		//limpar frames anteriores
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		if (variacao > 2)
			variacao = 0;

		if (estadoDoJogo == 0) {
			if (Gdx.input.justTouched()) {
				estadoDoJogo = 1;
			}
		} else {
			velocidadeQueda++;
			if (posicaoIncialVertical > 0 || velocidadeQueda < 0) {
				posicaoIncialVertical = posicaoIncialVertical - velocidadeQueda;
			}


			if (estadoDoJogo == 1) {

				posicaoMovimentoCanoHorizonatal -= deltaTime * 200;


				if (Gdx.input.justTouched()) {
					velocidadeQueda = -15;

				}

				//Verifica se o cano saiu da tela
				if (posicaoMovimentoCanoHorizonatal < -canoTopo.getWidth()) {
					posicaoMovimentoCanoHorizonatal = larguraDoDispositivo;
					alturaEntreCanosRandomica = random.nextInt(400) - 200;
					marcouPonto = false;
				}

				if (posicaoMovimentoCanoHorizonatal < 120) {
					if (!marcouPonto) {
						pontuacao++;
						marcouPonto = true;
					}
				}
			}else{
				if(Gdx.input.justTouched()){
					estadoDoJogo=0;
					pontuacao=0;
					velocidadeQueda=0;
					posicaoIncialVertical = alturaDoDispositivo/2;
					posicaoMovimentoCanoHorizonatal = larguraDoDispositivo;
				}

			}
		}


		//configurar camera

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(fundo,0,0,larguraDoDispositivo, alturaDoDispositivo);
		batch.draw(canoTopo,posicaoMovimentoCanoHorizonatal,alturaDoDispositivo/2 + espacoCanos/2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo,posicaoMovimentoCanoHorizonatal,alturaDoDispositivo/2 - canoBaixo.getHeight() - espacoCanos/2 + alturaEntreCanosRandomica);
		batch.draw(passaros[(int)variacao], 120, posicaoIncialVertical);
		font.draw(batch,String.valueOf(pontuacao),larguraDoDispositivo/2,alturaDoDispositivo -50);

		if(estadoDoJogo==2){
			batch.draw(gameOver,larguraDoDispositivo/4 - gameOver.getWidth()/7 ,alturaDoDispositivo/4);
			batch.draw(playButton,larguraDoDispositivo/7,alturaDoDispositivo/4 - playButton.getHeight()/6);
		}


		batch.end();

		passaroCirculo.set(120 + passaros[0].getWidth() / 2,posicaoIncialVertical + passaros[0].getHeight() / 2,passaros[0].getHeight());
		retangoloCanoBaixo = new Rectangle(
				posicaoMovimentoCanoHorizonatal, alturaDoDispositivo/2 - canoBaixo.getHeight() - espacoCanos/2 + alturaEntreCanosRandomica,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);
		retangoloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizonatal,alturaDoDispositivo/2 + espacoCanos/2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(),canoTopo.getHeight()
		);

		//desenhar forma
		/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(retangoloCanoBaixo.x,retangoloCanoBaixo.y,retangoloCanoBaixo.width,retangoloCanoBaixo.height);
		shapeRenderer.rect(retangoloCanoTopo.x,retangoloCanoTopo.y,retangoloCanoTopo.width,retangoloCanoTopo.height);
		shapeRenderer.end();*/

		//teste de colisão
		if(Intersector.overlaps(passaroCirculo,retangoloCanoBaixo)|| Intersector.overlaps(passaroCirculo,retangoloCanoTopo)
		|| posicaoIncialVertical <=0 || posicaoIncialVertical >=alturaDoDispositivo){
			estadoDoJogo = 2;

		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}
}
