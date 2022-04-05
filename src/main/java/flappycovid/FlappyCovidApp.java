package flappycovid;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static flappycovid.EntityType.PLAYER1;
import static flappycovid.EntityType.PLAYER2;
import static flappycovid.EntityType.WALL;


public class FlappyCovidApp extends GameApplication {

    private PlayerComponent player_component1;
    private PlayerComponent player_component2;
    private boolean newGame = false;

    @Override
    protected void initSettings(GameSettings settings)
    {
        settings.setWidth(1280); // 720p resolution
        settings.setHeight(720);
        settings.setTitle("FlappyCovid"); // game name
        settings.setVersion("0.1"); // version
    }

    protected void initInput() {
        getInput().addAction(new UserAction("JumpPlayer1")
        {
            @Override
            protected void onActionBegin() {
                player_component1.jump(); // jump action, calls function in PlayerComponent
            }
        }, KeyCode.W); // maps to W key PLAYER 1

        getInput().addAction(new UserAction("JumpPlayer2")
        {
            @Override
            protected void onActionBegin() {
                player_component2.jump(); // jump action, calls function in PlayerComponent
            }
        }, KeyCode.UP); // maps to UP arrowkey PLAYER 2

        getInput().addAction(new UserAction("DashPlayer1")
        {
            @Override
            protected void onActionBegin() {
                player_component1.dash(); // dash action, calls function in PlayerComponent
            }
        }, KeyCode.D); // maps to D key PLAYER 1

        getInput().addAction(new UserAction("DashPlayer2")
        {
            @Override
            protected void onActionBegin() {
                player_component2.dash(); // dash action, calls function in PlayerComponent
            }
        }, KeyCode.RIGHT); // maps to D key PLAYER 2
    }

    @Override
    protected void initGameVars(Map<String, Object> vars)
    {
        vars.put("stageColor", Color.BLACK);
        vars.put("score", 0);
    }

    @Override
    protected void initGame()
    {
        initPlayers(); // initiates players
    }

    @Override
    protected void initPhysics()
    {
        onCollisionBegin(PLAYER1, WALL, (player, wall) ->
        {
            newGame(); // initiates the phyiscs / collisions for player 1
        });

        onCollisionBegin(PLAYER2, WALL, (player, wall) ->
        {
            newGame(); // initiates the phyiscs / collisions for player 2
        });
    }

    @Override
    protected void initUI()
    {
        Text uiScore = new Text("");
        uiScore.setFont(Font.font(72));
        uiScore.setTranslateX(getAppWidth() - 200);
        uiScore.setTranslateY(50);
        uiScore.fillProperty().bind(getop("stageColor"));
        uiScore.textProperty().bind(getip("score").asString());

        addUINode(uiScore);
    }

    @Override
    protected void onUpdate(double tpf)
    {
        inc("score", +1);

        if (geti("score") == 3000) {
            newGame();
        }

        if (newGame) {
            newGame = false;
            getGameController().startNewGame();
        }
    }

    private void initPlayers()
    {
        player_component1 = new PlayerComponent();
        player_component2 = new PlayerComponent();

        Entity player1 = entityBuilder()
                .at(100, 100) // inits player at x 100 y 100
                .type(PLAYER1) // type of player
                .bbox(new HitBox(BoundingShape.box(70, 60)))
                .view(texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.5)).loop())
                .collidable()
                .with(player_component1, new WallBuildingComponent())
                .build();

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getAppHeight());
        getGameScene().getViewport().bindToEntity(player1, getAppWidth() / 3, getAppHeight() / 2);

        spawnWithScale(player1, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());

        Entity player2 = entityBuilder()
                .at(125, 125) // inits player at x 100 y 100
                .type(PLAYER2) // type of player
                .bbox(new HitBox(BoundingShape.box(70, 60)))
                .view(texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.5)).loop())
                .collidable()
                .with(player_component2, new WallBuildingComponent())
                .build();

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getAppHeight());
        getGameScene().getViewport().bindToEntity(player2, getAppWidth() / 3, getAppHeight() / 2);

        spawnWithScale(player2, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());
    }

    public void newGame()
    {
        newGame = true; // set newgame boolean to true
    }

    public void gameOver()
    {
        // game over
    }

    public static void main(String[] args)
    {
        launch(args); // launch game
    }
}
