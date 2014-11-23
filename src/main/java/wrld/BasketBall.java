package wrld;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.jogamp.opengl.util.texture.Texture;

import javax.media.opengl.GL2;

/**
 * Created by kevin on 11/19/14.
 */
public class BasketBall extends Ball {

    BasketBall(GL2 gl, Point3d p, DynamicsWorld world, Texture texture) {
        super(gl, p, world, texture);

        p.size=0.127f;
        p.mass=0.62369f;
        callist=mkGLCallList();

        //scale = false;
        friction=1;
        linDamping=.1f;
        angDamping=.1f;
        angularFactor=1;
        restitution=1.2f;
        shape = new SphereShape(1);
        initializePhysics(world);
    }//..

}// BasketBall
