package wrld;

import com.bulletphysics.collision.shapes.CapsuleShape;
import com.bulletphysics.dynamics.DynamicsWorld;

import javax.media.opengl.GL2;
import java.util.Vector;

/**
 * Created by kevin on 11/21/14.
 */
public class Skeleton extends CollisionModel{

    float radius;

    Skeleton(Point3d p,DynamicsWorld world,Vector<Integer> frames){
        super(p, frames, 0);

        //scaleGL=false;
        radius=.25f;
        angularFactor=0;
        shape = new CapsuleShape(radius,p.size);

        initializePhysics(world);

    }//..

}// Skeleton
