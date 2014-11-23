package wrld;

import com.bulletphysics.dynamics.DynamicsWorld;

/**
 * Created by Kevin on 11/23/2014.
 */
public class CannonBall extends Ball {


    CannonBall( Point3d p, DynamicsWorld world) {
        super(p,Textures.cannonBall);


        p.size=0.166624f;
        p.mass=19.0509f;
        friction=1;
        linDamping=.1f;
        angDamping=.1f;
        angularFactor=1;
        restitution=.5f;
        initializePhysics(world);
    }//..



}// end Class CannonBall
