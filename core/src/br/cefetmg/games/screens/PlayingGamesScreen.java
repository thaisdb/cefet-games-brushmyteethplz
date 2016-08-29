package br.cefetmg.games.screens;

import br.cefetmg.games.logic.chooser.GameSequencer;
import br.cefetmg.games.minigames.MiniGame;
import br.cefetmg.games.minigames.factories.ShooTheTartarusFactory;
import br.cefetmg.games.minigames.factories.ShootTheCariesFactory;
import br.cefetmg.games.minigames.util.MiniGameFactory;
import br.cefetmg.games.minigames.util.MiniGameState;
import br.cefetmg.games.minigames.util.StateChangeObserver;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author fegemo <coutinho@decom.cefetmg.br>
 */
public class PlayingGamesScreen extends BaseScreen
        implements StateChangeObserver {

    private MiniGame currentGame;
    private final GameSequencer sequencer;
    private PlayScreenState state;

    public PlayingGamesScreen(Game game) {
        super(game);
        super.assets.load("images/countdown.png", Texture.class);
        super.assets.load("images/gray-mask.png", Texture.class);
        super.assets.load("shoot-the-caries/caries.png", Texture.class);
        super.assets.load("shoot-the-caries/target.png", Texture.class);
        super.assets.load("shoo-the-tartarus/toothbrush-spritesheet.png",
                Texture.class);
        super.assets.load("shoo-the-tartarus/tartarus-spritesheet.png",
                Texture.class);
        super.assets.load("shoo-the-tartarus/tooth.png", Texture.class);
        super.assets.load("debug-rectangle.png", Texture.class);

        this.state = PlayScreenState.PLAYING;
        this.sequencer = new GameSequencer(5, new HashSet<MiniGameFactory>(
                Arrays.asList(
                        new ShootTheCariesFactory(),
                        new ShooTheTartarusFactory())
        ), this, this);
    }

    @Override
    public void show() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void handleInput() {
        if (this.currentGame != null) {
            this.currentGame.handleInput();
        }
        
        if (this.state != PlayScreenState.PLAYING) {
            if (Gdx.input.justTouched()) {
                // volta para o menu principal
                super.game.setScreen(new MenuScreen(super.game));
            }
        }
    }

    @Override
    public void update(float dt) {
        if (super.assets.update()) {
            if (this.currentGame == null) {
                advance();
            }
            this.currentGame.update(dt);
        }
    }

    @Override
    public void draw() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.batch.setProjectionMatrix(super.camera.combined);
        super.batch.begin();
        if (this.currentGame != null) {
            this.currentGame.draw();
        }
        if (this.state != PlayScreenState.PLAYING) {
            drawEndGame();
        }
        super.batch.end();
    }

    private void advance() {
        if (sequencer.hasNextGame()) {
            this.currentGame = sequencer.nextGame();
        } else {
            // mostra mensagem de fim
            this.state = PlayScreenState.FINISHED_GAME_OVER;
        }
    }

    @Override
    public void onStateChanged(MiniGameState state) {
        switch (state) {
            case WON:
            case FAILED:
                Timer.instance().scheduleTask(new Task() {
                    @Override
                    public void run() {
                        advance();
                    }

                }, 1.5f);
                break;
        }
    }

    private void drawEndGame() {
        super.drawCenterAlignedText("Pressione qualquer tecla para voltar "
                + "ao Menu", 0.5f, super.bounds.height * 0.35f);
    }

    enum PlayScreenState {
        PLAYING,
        FINISHED_GAME_OVER,
        FINISHED_WON
    }

}
