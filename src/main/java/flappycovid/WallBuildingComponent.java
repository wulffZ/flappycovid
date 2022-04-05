package flappycovid;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

public class WallBuildingComponent extends Component {
    private double lastWall = 1000;

    @Override
    public void onUpdate(double tpf) {
        if (lastWall - entity.getX() < FXGL.getAppWidth()) {
            buildWalls();
        }
    }

    private Rectangle wallView(double width, double height) { // this funcion determines the wall style.
        Rectangle wall = new Rectangle(width, height);
        wall.fillProperty().bind(FXGL.getWorldProperties().objectProperty("stageColor")); // uses property in FlappyCovidApp to determine wall color
        return wall;
    }

    private void buildWalls() {
        double height = FXGL.getAppHeight(); // gets height of the game window.
        double distance = height / 2; // halfs height of game window

        for (int i = 0; i < 10; i++) { // loops 10 times
            double topHeight = Math.random() * (height - distance);

            entityBuilder()
                    .at(lastWall + i * 500, 0 - 25) // in relation to the last wall, use the i from the for loop, times 500 to determine x placement. this is the top wall.
                    .type(EntityType.WALL)
                    .viewWithBBox(wallView(50, topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach();
            entityBuilder()
                    .at(lastWall + i * 500, 0 + topHeight + distance + 25) // in relation to the last wall, use the i from the for loop, times 500 to determine x placement. this is the bottom wall.
                    .type(EntityType.WALL)
                    .viewWithBBox(wallView(50, height - distance - topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach();
        }

        lastWall += 10 * 500;
    }
}
