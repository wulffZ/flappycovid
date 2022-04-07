package flappycovid;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.texture;
import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGLForKtKt.getAssetLoader;


public class ShooterFactory implements EntityFactory {

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().density(0.05f));
        physics.setBodyType(BodyType.DYNAMIC);

        physics.setOnPhysicsInitialized(() -> {
            Point2D mousePosition = FXGL.getInput().getMousePositionWorld();

            physics.setLinearVelocity(mousePosition.subtract(data.getX(), data.getY()).normalize().multiply(800));
        });

        return FXGL.entityBuilder(data)
                .type(EntityType.BULLET)
                .viewWithBBox(FXGL.getAssetLoader().loadTexture("bullet.png", 40, 40))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(25, 0), 900))
                //.with(physics)
                //.with(new ExpireCleanComponent(Duration.seconds(4)))
                .build();

    }
}
