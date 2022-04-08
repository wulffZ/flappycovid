package flappycovid;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.ArrayList;
import static com.almasb.fxgl.dsl.FXGL.getGameController;

public class MainMenu extends FXGLMenu {
    private final ArrayList<Button> menuButtons = new ArrayList<>();

    public MainMenu(MenuType type) {
        super(type);
        renderMenuUI();
    }

    private Button createButton(String buttonName) {
        return FXGL.getUIFactoryService().newButton(buttonName);
    }

    private void setMenuButtons() {
        menuButtons.add(createButton("Play Game"));
        menuButtons.add(createButton("Quit Game"));

        menuButtons.get(0).setOnAction(actionEvent -> this.start());
        menuButtons.get(1).setOnAction(actionEvent -> this.fireExit());
    }

    private void createButtonContainer(BorderPane pane) {
        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        menuButtons.forEach(btn -> box.getChildren().add(btn));
        pane.setCenter(box);
        box.setAlignment(Pos.CENTER);
    }

    private void createBackground() {
        Background background = new Background(
                new BackgroundImage(
                        new Image("assets/textures/background.png", FXGL.getAppWidth(), FXGL.getAppHeight(),
                                false, true),
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT));

        getContentRoot().setBackground(background);
    }

    private void start() {
        getGameController().startNewGame();
    }

    private void renderMenuUI() {
        BorderPane pane = new BorderPane();
        pane.setMinWidth(FXGL.getAppWidth());
        pane.setMinHeight(FXGL.getAppHeight());

        this.setMenuButtons();
        createButtonContainer(pane);
        createBackground();

        getContentRoot().getChildren().add(pane);
    }
}