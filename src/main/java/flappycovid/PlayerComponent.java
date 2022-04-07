package flappycovid;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

import static com.almasb.fxgl.dsl.FXGL.*;

public class PlayerComponent extends Component {
    private Vec2 acceleration = new Vec2(10, 0); // default x speed is 5.

    @Override
    public void onUpdate(double tpf) {
        acceleration.x += tpf * 10; // x speed calculation, default speed is around 5.6-ish
        acceleration.y += tpf * 30;

        if (acceleration.y < -8)
        {
            acceleration.y = -8;
        }

        if (acceleration.y > 8)
        {
            acceleration.y = 8;
        }

        if(acceleration.x >= 10)
        {
            acceleration.x = acceleration.x - 1; // while the player is dashing, accel for x is increased by 18. so we will need to gradually recude it to original speed.
        }

        entity.translate(acceleration.x, acceleration.y);

        if (entity.getBottomY() > getAppHeight()) {
            FXGL.<FlappyCovidApp>getAppCast().kill(entity); // if the player hits the floor (the y of the player > the height of the app, kill the player)
        }
    }

    public void jump(int x, int y) {
        acceleration.addLocal(x, -15);
    }

    public void dash(int x) {
        acceleration.addLocal(x, 0);
        play("dash.mp3");
    }

}
