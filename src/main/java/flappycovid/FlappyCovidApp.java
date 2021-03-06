package flappycovid;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.MenuItem;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.ui.FontFactory;
import com.sun.tools.javac.Main;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static flappycovid.EntityType.PLAYER1;
import static flappycovid.EntityType.PLAYER2;
import static flappycovid.EntityType.WALL;


public class FlappyCovidApp extends GameApplication {

    private double time_alive = 0;

    private boolean logged_in = false;
    private double appWidth;
    private double appHeight;

    private PlayerComponent player_component1;
    private PlayerComponent player_component2;

    private Entity player1;
    private Entity player2;

    private double scorePlayer1 = 0;
    private double scorePlayer2 = 0;

    private boolean player1_alive = true;
    private boolean player2_alive = true;

    private String player1_name = "";
    private String player2_name = "";

    private double dashcooldown_player1 = 0;
    private double dashcooldown_player2 = 0;

    private int current_level = 1;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280); // 720p resolution
        settings.setHeight(720);
        settings.setTitle("FlappyCovid"); // game name
        settings.setVersion("0.3.1"); // version

        settings.setEnabledMenuItems(EnumSet.of(MenuItem.EXTRA));
        settings.getCredits().addAll(Arrays.asList(
                "Matthijs",
                "Bart",
                "Sanne",
                "Wytze"
        ));

        settings.setSceneFactory(new UISceneFactory());
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @NotNull
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu(MenuType.MAIN_MENU);
            }
        });
        settings.getCSSList().add("stylesheet.css");
        settings.setDeveloperMenuEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    protected void onPreInit() {
        loopBGM("theme_soundtrack.wav");
    }

    protected void initInput() {
        getInput().addAction(new UserAction("JumpPlayer1") {
            @Override
            protected void onActionBegin() {
                player_component1.jump(0, -15); // jump action, calls function in PlayerComponent
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("JumpPlayer2") {
            @Override
            protected void onActionBegin() {
                player_component2.jump(0, -15); // jump action, calls function in PlayerComponent
            }
        }, KeyCode.UP);

        getInput().addAction(new UserAction("DashPlayer1") {
            @Override
            protected void onActionBegin() {
                if (dashcooldown_player1 == 0) {
                    if(current_level == 1) {
                        dashcooldown_player1 = 3;
                        player_component1.dash(18); // long dash, low cooldown
                    }
                    else if(current_level == 2) {
                        player_component1.dash(15); // shorter dash, longer cooldown.
                        dashcooldown_player1 = 5;
                    }
                    else if(current_level == 3) {
                        player_component1.dash(8); // extremely short dash, 10s cooldown. (HARD)
                        dashcooldown_player1 = 10;
                    }
                }
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("DashPlayer2") {
            @Override
            protected void onActionBegin() {
                if (dashcooldown_player2 == 0) {
                    if(current_level == 1) {
                        dashcooldown_player2 = 3;
                        player_component2.dash(18); // long dash, low cooldown
                    }
                    else if(current_level == 2) {
                        player_component2.dash(15); // shorter dash, longer cooldown.
                        dashcooldown_player2 = 5;
                    }
                    else if(current_level == 3) {
                        player_component2.dash(8); // extremely short dash, 10s cooldown. (HARD)
                        dashcooldown_player2 = 10;
                    }
                }
            }
        }, KeyCode.RIGHT);


        onKey(KeyCode.S, () -> shootPlayer1());
        onKey(KeyCode.DOWN, () -> shootPlayer2());


        FXGL.onKey(KeyCode.TAB, () -> {
            getDialogService().showMessageBox(String.join("\n", getHighscores()));
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("stageColor", Color.GREENYELLOW);
        vars.put("scorePlayer1", 0);
        vars.put("scorePlayer2", 0);
        vars.put("current_level", 1);
    }

    @Override
    protected void initGame() {
        initPlayers(); // initiates players

        getGameWorld().addEntityFactory(new ShooterFactory());
    }

    public Font getFont() {
        FontFactory flappybirdy_font_factory = getAssetLoader().loadFont("FlappyBirdy.ttf");
        Font flappybirdy = flappybirdy_font_factory.newFont(100);

        return flappybirdy;
    }

    protected void setPlayer1Name(String name) {
        Text player1_text = new Text(name);
        player1_text.setFont(Font.font(62));
        player1_text.setTranslateX(getAppWidth() - 420);
        player1_text.setTranslateY(120);
        player1_text.setFont(getFont());

        addUINode(player1_text);

        player1_name = name;
    }

    protected void setPlayer2Name(String name) {
        Text player2_text = new Text(name);
        player2_text.setFont(Font.font(62));
        player2_text.setTranslateX(getAppWidth() - 420);
        player2_text.setTranslateY(60);
        player2_text.setFont(getFont());

        addUINode(player2_text);

        player2_name = name;
        System.out.println(player1_name);
    }

    public void initLogin() {
        getDialogService().showInputBox("Please input player 2 name.", name -> {
            setPlayer2Name(name);
            logged_in = true;
            initSession();
        });

        getDialogService().showInputBox("Please input player 1 name.", name -> {
            setPlayer1Name(name);
        });
    }

    @Override
    protected void initPhysics() {
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
    protected void initUI() {
        Text scorePlayer1 = new Text("");
        scorePlayer1.setFont(Font.font(62));
        scorePlayer1.setTranslateX(getAppWidth() - 180);
        scorePlayer1.setTranslateY(120);
        scorePlayer1.setFont(getFont());
        scorePlayer1.textProperty().bind(getip("scorePlayer1").asString());

        Text scorePlayer2 = new Text("");
        scorePlayer2.setFont(Font.font(62));
        scorePlayer2.setTranslateX(getAppWidth() - 180);
        scorePlayer2.setTranslateY(60);
        scorePlayer2.setFont(getFont());
        scorePlayer2.textProperty().bind(getip("scorePlayer2").asString());

        Text level_text = new Text("Level");
        level_text.setFont(Font.font(62));
        level_text.setTranslateX(60);
        level_text.setTranslateY(60);
        level_text.setFont(getFont());

        Text current_level = new Text("");
        current_level.setFont(Font.font(62));
        current_level.setTranslateX(240);
        current_level.setTranslateY(60);
        current_level.setFont(getFont());
        current_level.textProperty().bind(getip("current_level").asString());

        addUINode(level_text);
        addUINode(current_level);
        addUINode(scorePlayer1);
        addUINode(scorePlayer2);
    }


    public void initSession() {
        String player_names = player1_name + "," + player2_name;
        try {
            FileWriter fileWriter = new FileWriter("session.txt");
            PrintWriter printwriter = new PrintWriter(fileWriter);
            printwriter.print(player_names);
            printwriter.close();
        } catch (IOException err) {
            System.out.println(err);
        }
    }

    public boolean checkSession() {
        try {
            File session = new File("session.txt");
            if(session.exists()) {
                String result = String.valueOf(Files.readAllLines(Paths.get("session.txt")));
                String names[] = result.split(",");

                names[0] = names[0].replaceAll("[\\(\\)\\[\\]\\{\\}]","");
                names[1] = names[1].replaceAll("[\\(\\)\\[\\]\\{\\}]","");

                setPlayer1Name(names[0]);
                setPlayer2Name(names[1]);

                logged_in = true;
                return true;
            } else {
                return false;
            }
        } catch (IOException err) {
            System.out.println(err);
        }
        return false;
    }

    public void killSession() {
        File session = new File("session.txt");
        if (session.exists()) {
            session.delete(); // remove session file, thus resetting the game's player data.
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        time_alive += 0.02; // roughly every second, this counts up a whole.

        if (!logged_in) {
            if(!checkSession())
            {
                initLogin();
            }
        }

        if(time_alive > 20 && time_alive < 20.02) {
            current_level = 1;
            inc("current_level", +1);
        }

        if(time_alive > 40 && time_alive < 40.02) {
            current_level = 2;
            inc("current_level", +1);
        }

        if(time_alive > 60 && time_alive < 60.02) {
            current_level = 3;
            inc("current_level", +1);
        }

        if (player1_alive) {
            scorePlayer1++;
            inc("scorePlayer1", +1); // if their alive, increase theyre score by 0.1
        }

        if (player2_alive) {
            scorePlayer2++;
            inc("scorePlayer2", +1); // if their alive, increase theyre score by 0.1
        }

        if (dashcooldown_player1 > 0) {
            dashcooldown_player1 += -0.05;
        } else {
            dashcooldown_player1 = 0;
        }

        if (dashcooldown_player2 > 0) {
            dashcooldown_player2 += -0.05;
        } else {
            dashcooldown_player2 = 0;
        }


        if (!player1_alive && !player2_alive) { // reset game, both players area dead.
            getDialogService().showConfirmationBox("Do you want to save your highscores?", yes ->
            {
                if (yes) {
                    saveScore();
                } else {
                    promptRetry();
                }
            });

        }
    }

    private void promptRetry() {
        getDialogService().showConfirmationBox("Do you want to retry", save ->
        {
            if (save) {
                retry();
            } else {
                restart();
            }
        });
    }

    private void saveScore() {
        try {
            FileWriter fileWriter = new FileWriter("highscores.txt", true);
            PrintWriter printwriter = new PrintWriter(fileWriter);
            printwriter.println("Score: " + scorePlayer1 + " achieved by " + player1_name);
            printwriter.println("Score: " + scorePlayer2 + " achieved by " + player2_name);
            printwriter.close();

            promptRetry();
        } catch (IOException err) {
            System.out.println(err);
        }
    }

    private List getHighscores() {
        try {
            List<String> list = Files.readAllLines(Paths.get("highscores.txt"), StandardCharsets.UTF_8);
            String[] highscores = list.toArray(new String[list.size()]);

            return List.of(highscores);
        } catch (IOException err) {
            System.out.println(err);
        }
        return null;
    }

    private void restart() {
        logged_in = false;
        player1_alive = true;
        player2_alive = true;

        killSession();

        getGameController().startNewGame();
    }

    private void retry() {
        player1_alive = true;
        player2_alive = true;

        logged_in = false;
        current_level = 1;
        getGameController().startNewGame();
    }

    private void initPlayers() {
        player_component1 = new PlayerComponent();
        player_component2 = new PlayerComponent();

        appWidth = getAppWidth() / 3;
        appHeight = getAppHeight() / 2;

        player1 = entityBuilder()
                .at(100, 100) // inits player at x 100 y 100
                .type(PLAYER1) // type of player
                .bbox(new HitBox(BoundingShape.circle(2)))
                .view(texture("player1.png"))
                .collidable()
                .with(player_component1, new WallBuildingComponent())
                .build();

        player2 = entityBuilder()
                .at(125, 125) // inits player at x 100 y 100
                .type(PLAYER2) // type of player
                .bbox(new HitBox(BoundingShape.circle(2)))
                .view(texture("player2.png"))
                .collidable()
                .with(player_component2, new WallBuildingComponent())
                .build();

        getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, getAppHeight());
        getGameScene().getViewport().bindToEntity(player1, appWidth, appHeight); // by default, viewport is bound to player 1

        Node scrollview =  new ScrollingBackgroundView(texture("background.png",1200, 725));

        entityBuilder()
                .view(scrollview)
                .buildAndAttach();

        spawnWithScale(player1, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());
        spawnWithScale(player2, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());
    }

    private void shootPlayer1() {
        spawn("bullet", player1.getPosition().add(70, 90));
    }

    private void shootPlayer2() {
        spawn("bullet", player2.getPosition().add(70, 50));
    }

    public void kill(Entity entity) {
        EntityType entity_type = (EntityType) entity.getType();

        if (entity_type == PLAYER1) {
            player1_alive = false;
            getGameScene().getViewport().bindToEntity(player2, appWidth, appHeight); // now make viewport follow remaining player 2
            player1.setUpdateEnabled(false); // stop drawing entity player 1, leaving him in the wall
        }

        if (entity_type == PLAYER2) {
            player2_alive = false;
            getGameScene().getViewport().bindToEntity(player1, appWidth, appHeight); // now make viewport follow remaining player 1
            player2.setUpdateEnabled(false); // stop drawing entity player 2, leaving him in the wall
        }
    }

    public static void main(String[] args) {
        launch(args); // launch game
    }
}
