package br.cefetmg.games.screens;

import br.cefetmg.games.Config;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Uma tela de Menu Principal do jogo.
 *
 * @author Flávio Coutinho - fegemo <coutinho@decom.cefetmg.br>
 */
public class MenuScreen extends BaseScreen {

    private static final int NUMBER_OF_TILED_BACKGROUND_TEXTURE = 7;
    private TextureRegion background;
    private TransitionEffect transition;
    private int touched;
    private Sprite screenTransition;
    /**
     * Cria uma nova tela de menu.
     *
     * @param game o jogo dono desta tela.
     */
    public MenuScreen(Game game, BaseScreen previous) {
        super(game, previous);
    }

    /**
     * Configura parâmetros da tela e instancia objetos.
     */
    @Override
    public void show() {
        Gdx.gl.glClearColor(1, 1, 1, 1);

        // instancia a textura e a região de textura (usada para repetir)
        background = new TextureRegion(new Texture("menu-background.png"));
        // configura a textura para repetir caso ela ocupe menos espaço que o
        // espaço disponível
        background.getTexture().setWrap(
                Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // define a largura da região de desenho de forma que ela seja repetida
        // um número de vezes igual a NUMBER_OF_TILED_BACKGROUND_TEXTURE 
        background.setRegionWidth(
                background.getTexture().getWidth()
                * NUMBER_OF_TILED_BACKGROUND_TEXTURE);
        // idem para altura, porém será repetida um número de vezes igual a 
        // NUMBER_OF_TILED_BACKGROUND_TEXTURE * razãoDeAspecto
        background.setRegionHeight(
                (int) (background.getTexture().getHeight()
                * NUMBER_OF_TILED_BACKGROUND_TEXTURE
                / Config.DESIRED_ASPECT_RATIO));
        transition = new TransitionEffect();
        transition.setDelta(0.01f);
        touched = 0;
        super.assets.load("images/transicao.jpg", Texture.class);
        screenTransition = new Sprite(new Texture("images/transicao.jpg"),(int)viewport.getWorldWidth(), (int)viewport.getWorldHeight());
        screenTransition.setCenter(viewport.getWorldWidth()/2f, viewport.getWorldHeight()/2f);
    }

    /**
     * Recebe <em>input</em> do jogador.
     */
    @Override
    public void handleInput() {
        // se qualquer interação é feita (teclado, mouse pressionado, tecla
        // tocada), navega para a próxima tela (de jogo)
        if (Gdx.input.justTouched()) {
            touched = 2;
        }
        if(transition.isFinished()){
            if(touched == 0) {
                touched = 1;
                transition.setX(0.0f);
            }else {
                touched = 3;
                navigateToMicroGameScreen();
            }
        }
    }

    /**
     * Atualiza a lógica da tela.
     *
     * @param dt Tempo desde a última atualização.
     */
    @Override
    public void update(float dt) {
        float speed = dt * 0.25f;
        background.scroll(speed, -speed);
    }

    /**
     * Desenha o conteúdo da tela de Menu.
     */
    @Override
    public void draw() {
        if(touched == 2){
            batch.begin();
            transition.fadeOut(batch, screenTransition);
            batch.end();
        }
        
        if (touched == 0) {
            batch.begin();
            transition.fadeIn(batch, screenTransition);
            batch.end();
        }
        
        if(touched < 3){
            batch.begin();
            batch.draw(background, 0, 0,
                    viewport.getWorldWidth(),
                    viewport.getWorldHeight());
            drawCenterAlignedText("Pressione qualquer tecla para jogar",
                    1f, viewport.getWorldHeight() * 0.35f);
            batch.end();
        }
    }

    /**
     * Navega para a tela de jogo.
     */
    private void navigateToMicroGameScreen() {
        game.setScreen(new PlayingGamesScreen(game, this));
    }

}
