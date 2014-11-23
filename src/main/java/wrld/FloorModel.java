package wrld;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

import javax.media.opengl.GL2;
import javax.vecmath.Vector3f;
import java.util.Vector;

/**
 * Created by Kevin on 12/30/13.
 */
public class FloorModel extends Model {

    static int floorInt = 0;
    // Texture
    static protected Texture t;
    static protected float textureTop, textureBottom, textureLeft, textureRight;
    int fSec = 5,L = 400, W = 400,y=0;

    Vector<Wall> wall = new Vector<Wall>();

    FloorModel(Texture t, Point3d p, DynamicsWorld world) {
        super(p,1);

        mkStaticGroundPlane(world);

        if(floorInt == 0){
            this.gl = gl;

            this.t = t;

            TextureCoords textureCoords = t.getImageTexCoords();
            textureTop = textureCoords.top();
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right();
            floorInt = genCibeList(world);

        }
    }//..

    @Override
    public void draw(){

        for(Wall w:wall){
            w.draw();
        }

        gl.glPushMatrix();

        gl.glTranslatef(p.x,p.y,p.z);
        gl.glCallList(floorInt);

        gl.glPopMatrix();
    }//..

    protected int genCibeList(DynamicsWorld world){

        int wsz = 5;
        for(int z=-W;z<W;z+=wsz*2){
            wall.add(new Wall(new Point3d(L,wsz,z,wsz,0),world));
            wall.add(new Wall(new Point3d(-L,wsz,z,wsz,0),world));
        }
        for(int x=-L;x<L;x+=wsz*2){
            wall.add(new Wall(new Point3d(x,wsz,W,wsz,0),world));
            wall.add(new Wall(new Point3d(x,wsz,-W,wsz,0),world));
        }

        floorInt = gl.glGenLists(1);
        gl.glNewList(floorInt, gl.GL_COMPILE);

        gl.glDisable(gl.GL_CULL_FACE);
        gl.glBegin(gl.GL_QUADS);
        t.enable(gl);
        t.bind(gl);
        gl.glNormal3d(0, 1, 0);
        for(int x =-L;x<L;x+=fSec)
            for(int z=-W;z<W;z+=fSec){
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x + fSec, y, z);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x + fSec, y, z + fSec);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x, y, z + fSec);
            }
        gl.glEnd();
        gl.glEnable(gl.GL_CULL_FACE);
        gl.glEndList();
        return floorInt;

    }//..

    protected void mkStaticGroundPlane(DynamicsWorld world) {
        // floor
        float mass = 0;
        CollisionShape shape = new BoxShape(new Vector3f(L, 10, W));

        Transform t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(p.x,p.y-10,p.z));

        MotionState motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass, motionState, shape);
        RigidBody body = new RigidBody(info);
        body.setFriction(1f);
        body.setRestitution(.5f);
        world.addRigidBody(body);
    }//..

}// end Class FloorModel