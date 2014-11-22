package wrld;

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import javax.media.opengl.GL2;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import java.util.Vector;

/**
 * Created by Kevin on 12/25/13.
 */
public class Model {

    protected Point3d p;
    protected GL2 gl;

    //JBullet Variables
    protected Transform t;
    protected float glMatrix[] = new float[16];
    protected int callist = -1;
    protected RigidBody body;

    protected int curFarame=0,frameWait,frameCnt,fd=1;
    protected Vector<Integer> frames = null;


    Model(GL2 gl, Point3d p,int callist) {
        this.gl = gl;
        this.p = p;
        this.callist = callist;
    }//..

    Model(GL2 gl, Point3d p,Vector<Integer>frames,int frameWait) {
        this.gl = gl;
        this.p = p;
        this.frames=frames;
        this.frameWait=frameWait;
    }//..

    protected void draw(){
        if(frames == null) {
            gl.glPushMatrix();
            gl.glTranslatef(p.x, p.y, p.z);
            gl.glScaled(p.size, p.size, p.size);
            gl.glCallList(callist);
            gl.glPopMatrix();
        }else {
            gl.glPushMatrix();
            gl.glTranslatef(p.x, p.y, p.z);
            gl.glScaled(p.size, p.size, p.size);
            gl.glCallList(frames.get(curFarame));
            gl.glPopMatrix();
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
        }
    }//..

    public void pullTo(Vector3f pull){
        float tx = t.origin.x,ty = t.origin.y, tz = t.origin.z;
        body.setActivationState(1);
        body.applyForce(new Vector3f(pull.x-tx, pull.y-ty, pull.z-tz), new Vector3f(pull.x,pull.y,pull.z));
    }//..

    public void setTransform(Vector3f n){
        body.getWorldTransform(t);
        t.origin.set(n.x,n.y,n.z);
    }//..

    public void setRotation(float[] rot){
        Quat4f quat = new Quat4f();
        quat.set(rot); //or quat.setEulerZYX depending on the ordering you want
        t.setRotation(quat);
        body.setCenterOfMassTransform(t);
    }//..

    public RigidBody getBody(){
        return body;
    }//..

    public void setVelocity(Vector3f velocity){
        body.setLinearVelocity(velocity);
    }//..

}// end Class Model





    /*Model(GL2 glToUse, Point3d p, Vector<Integer> frames) {
        this.glToUse = glToUse;
        this.p = p;
        callist =0;
        this.frames = frames;
        curFarame=0;

    protected int curFarame;
    protected Vector<Integer> frames;


        --draw---
            gl.glPushMatrix();
                gl.glTranslatef(p.x,p.y,p.z);
                gl.glScaled(p.size,p.size,p.size);
                gl.glCallList(frames.get(curFarame));
            gl.glPopMatrix();
            curFarame++;
            curFarame = curFarame == frames.size()?0:curFarame;
    }//..*/