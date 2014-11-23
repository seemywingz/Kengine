package wrld;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;
import java.util.Vector;

/**
 * Created by kevin on 11/13/14.
 */
public class CollisionModel extends Model{

    CollisionShape shape;

    float
            friction=1f,
            linDamping=.1f,angDamping=.1f,
            angularFactor=1,
            restitution=.5f;

    CollisionModel(GL2 gl, Point3d p, int callist){
        super(gl, p, callist);
    }//..

    CollisionModel(GL2 gl, Point3d p,Vector<Integer> frames,int frameWait) {
        super(gl,p,frames,frameWait);
    }//..

    @Override
    public void draw(){
        t = body.getWorldTransform(t);
        t.getOpenGLMatrix(glMatrix);

        gl.glPushMatrix();
            gl.glMultMatrixf(Utils.mkFloatBuffer(glMatrix));
                gl.glScaled(p.size, p.size, p.size);
            drawFramesOrList();
        gl.glPopMatrix();
    }//..

    protected void drawFramesOrList(){
        if(frames != null) {
            gl.glCallList(frames.get(curFarame));
            if(frameCnt>=frameWait) {
                curFarame+=fd;
                if(curFarame == frames.size()-1){
                    fd=-fd;
                }else if(curFarame==0){
                    fd=-fd;
                }
                frameCnt=0;
            }
            frameCnt++;
        }else {
            gl.glCallList(callist);
        }
    }//..

    protected void initializePhysics(DynamicsWorld world){
        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(p.x,p.y,p.z));
        Vector3f inertia = new Vector3f(0,0,0);
        shape.calculateLocalInertia(p.mass,inertia);
        shape.setLocalScaling(new Vector3f(p.size, p.size, p.size));

        MotionState motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(p.mass,motionState,shape,inertia);
        body = new RigidBody(info);
        body.setFriction(friction);
        body.setDamping(linDamping, angDamping);
        body.setAngularFactor(angularFactor);
        body.setRestitution(restitution);
        Scene.world.addRigidBody(body);
    }//..

}// wrld.PhysicsModel
