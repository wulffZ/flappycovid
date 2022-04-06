package flappycovid;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.texture;

public class ShooterFactory implements EntityFactory {

    @Spawns("Bullet")
    public Entity newBullet(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(ShooterType.BULLET)
                .viewWithBBox(new Rectangle(10, 10, Color.BLUE))
                .view(texture("player1.png"))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(25, 0), 800))
                .build();
    }
}
