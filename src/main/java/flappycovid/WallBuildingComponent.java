package flappycovid;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;

import static com.almasb.fxgl.dsl.FXGL.*;

public class WallBuildingComponent extends Component {
    private double previous_wall = 1000; // this where the default wall is first place, as to not insta-kill the players

    @Override
    public void onUpdate(double tpf) {
        if (previous_wall - entity.getX() < FXGL.getAppWidth()) {
            buildWalls();
        }
    }

    private void buildWalls() {
        double height = FXGL.getAppHeight(); // gets height of the game window.
        double distance = height / 2; // halfs height of game window

        for (int i = 0; i < 10; i++) {
            double topHeight = Math.random() * (height - distance);

            entityBuilder()
                    .at(previous_wall + i * 500, 0 - 25) // in relation to the last wall, use the i from the for loop, times 500 to determine x placement. this is the top wall.
                    .type(EntityType.WALL)
                    .viewWithBBox(FXGL.getAssetLoader().loadTexture("needle_down.png", 190, topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach();
            entityBuilder()
                    .at(previous_wall + i * 500, 0 + topHeight + distance + 40) // in relation to the last wall, use the i from the for loop, times 500 to determine x placement. this is the bottom wall.
                    .type(EntityType.WALL)
                    .viewWithBBox(FXGL.getAssetLoader().loadTexture("needle_up.png",190, height - distance - topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach();
        }

        previous_wall += 10 * 500;
    }
}
