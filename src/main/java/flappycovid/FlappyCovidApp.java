package flappycovid;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static flappycovid.EntityType.PLAYER1;
import static flappycovid.EntityType.PLAYER2;
import static flappycovid.EntityType.WALL;


public class FlappyCovidApp extends GameApplication {

    private double appWidth;
    private double appHeight;
    private PlayerComponent player_component1;
    private PlayerComponent player_component2;
    private Entity player1;
    private Entity player2;
    private boolean player1_alive = true;
    private boolean player2_alive = true;
    private double dashcooldown_player1 = 0;
    private double dashcooldown_player2 = 0;

    @Override
    protected void initSettings(GameSettings settings)
    {
        settings.setWidth(1280); // 720p resolution
        settings.setHeight(720);
        settings.setTitle("FlappyCovid"); // game name
        settings.setVersion("0.2"); // version
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
                if(dashcooldown_player1 > 0) {
                    // todo: implement visual feedback
                } else {
                    player_component1.dash(); // dash action, calls function in PlayerComponent
                    dashcooldown_player1 = 5;
                }
            }
        }, KeyCode.D); // maps to D key PLAYER 1

        getInput().addAction(new UserAction("DashPlayer2")
        {
            @Override
            protected void onActionBegin() {
                if(dashcooldown_player2 > 0) {
                   // todo: implement visual feedback
                } else {
                    player_component2.dash(); // dash action, calls function in PlayerComponent
                    dashcooldown_player2 = 5;
                }
            }
        }, KeyCode.RIGHT); // maps to D key PLAYER 2
    }

    @Override
    protected void initGameVars(Map<String, Object> vars)
    {
        vars.put("stageColor", Color.GREENYELLOW);
        vars.put("scorePlayer1", 0);
        vars.put("scorePlayer2", 0);
    }

    @Override
    protected void initGame()
    {
        initPlayers(); // initiates players
    }

    @Override
    protected void initPhysics()
    {
        onCollisionBegin(PLAYER1, WALL, (player1, wall) ->
        {
            kill(player1); // on collision with a entity player1 and wall, this player dies.
        });

        onCollisionBegin(PLAYER2, WALL, (player2, wall) ->
        {
            kill(player2); // on collision with a entity player2 and wall, this player dies.
        });
    }

    @Override
    protected void initUI()
    {
        Text player1Alive = new Text("P1");
        player1Alive.setFont(Font.font(62));
        player1Alive.setTranslateX(getAppWidth() - 420);
        player1Alive.setTranslateY(60);

        Text player2Alive = new Text("P2");
        player2Alive.setFont(Font.font(62));
        player2Alive.setTranslateX(getAppWidth() - 420);
        player2Alive.setTranslateY(120);

        Text scorePlayer1 = new Text("");
        scorePlayer1.setFont(Font.font(62));
        scorePlayer1.setTranslateX(getAppWidth() - 180);
        scorePlayer1.setTranslateY(120);
        scorePlayer1.textProperty().bind(getip("scorePlayer1").asString());

        Text scorePlayer2 = new Text("");
        scorePlayer2.setFont(Font.font(62));
        scorePlayer2.setTranslateX(getAppWidth() - 180);
        scorePlayer2.setTranslateY(60);
        scorePlayer2.textProperty().bind(getip("scorePlayer2").asString());

        addUINode(player1Alive);
        addUINode(player2Alive);

        addUINode(scorePlayer1);
        addUINode(scorePlayer2);
    }

    @Override
    protected void onUpdate(double tpf)
    {
        if(player1_alive) {
            inc("scorePlayer1", +0.1); // if their alive, increase theyre score by 0.1
        }

        if(player2_alive) {
            inc("scorePlayer2", +0.1); // if their alive, increase theyre score by 0.1
        }

        if(dashcooldown_player1 > 0) {
            dashcooldown_player1 += -0.05;
        } else {
            dashcooldown_player1 = 0;
        }

        if(dashcooldown_player2 > 0) {
            dashcooldown_player2 += -0.05;
        } else {
            dashcooldown_player2 = 0;
        }


        if (!player1_alive && !player2_alive) { // reset game, both players area dead.
            player1_alive = true;
            player2_alive = true;
            getGameController().startNewGame();
        }
    }

    private void initPlayers()
    {
        player_component1 = new PlayerComponent();
        player_component2 = new PlayerComponent();

        appWidth = getAppWidth() / 3;
        appHeight = getAppHeight() / 2;

        player1 = entityBuilder()
                .at(100, 100) // inits player at x 100 y 100
                .type(PLAYER1) // type of player
                .bbox(new HitBox(BoundingShape.box(5, 5)))
                .view(texture("player1.png"))
                .collidable()
                .with(player_component1, new WallBuildingComponent())
                .build();

        player2 = entityBuilder()
                .at(125, 125) // inits player at x 100 y 100
                .type(PLAYER2) // type of player
                .bbox(new HitBox(BoundingShape.box(32, 32)))
                .view(texture("player2.png"))
                .collidable()
                .with(player_component2, new WallBuildingComponent())
                .build();

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getAppHeight());
        getGameScene().getViewport().bindToEntity(player1, appWidth, appHeight); // by default, viewport is bound to player 1
        spawnWithScale(player1, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());
        spawnWithScale(player2, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());
    }

    public void kill(Entity entity)
    {
        EntityType entity_type = (EntityType) entity.getType();

        if(entity_type == PLAYER1) {
            player1_alive = false;
            getGameScene().getViewport().bindToEntity(player2, appWidth, appHeight); // now make viewport follow remaining player 2
            player1.setUpdateEnabled(false); // stop drawing entity player 1, leaving him in the wall
        }

        if(entity_type == PLAYER2) {
            player2_alive = false;
            getGameScene().getViewport().bindToEntity(player1, appWidth, appHeight); // now make viewport follow remaining player 1
            player2.setUpdateEnabled(false); // stop drawing entity player 2, leaving him in the wall
        }
    }

    public void gameOver()
    {
        showMessage("game over my dude");
    }

    public static void main(String[] args)
    {
        launch(args); // launch game
    }
}
