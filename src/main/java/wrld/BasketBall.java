package wrld;

import com.bulletphysics.dynamics.DynamicsWorld;

/**
 * Created by kevin on 11/19/14.
 */
public class BasketBall extends Ball {

    BasketBall(Point3d p, DynamicsWorld world) {
        super(p,Textures.basketBall);

        p.size=0.127f;
        p.mass=0.62369f;

        //scale = false;
        friction=1;
        linDamping=.1f;
        angDamping=.1f;
        angularFactor=1;
        restitution=1.2f;
        initializePhysics(world);
    }//..

}// BasketBall
