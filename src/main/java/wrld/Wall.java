package wrld;

import com.bulletphysics.collision.shapes.BoxShape;
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

/**
 * Created by Kevin on 1/8/14.
 */
public class Wall extends CollisionModel {


    static Texture texture;
    protected float textureTop, textureBottom, textureLeft, textureRight;
    static int obj = 0;

    Wall(GL2 gl, Point3d p, DynamicsWorld world) {
        super(gl, p, 1);

        if(obj == 0){
            try {
                Textures tex = new Textures(gl);
                texture = tex.loadTexture(texture, "/tex/wall.jpg", ".jpg");
                TextureCoords textureCoords = texture.getImageTexCoords();
                textureTop = textureCoords.top();
                textureBottom = textureCoords.bottom();
                textureLeft = textureCoords.left();
                textureRight = textureCoords.right();
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Error loading wall texture");
            }
            obj = genCube();
        }

        initializePhysics(world);
    }

    public void draw(){
        t = body.getWorldTransform(t);
        t.getOpenGLMatrix(glMatrix);

        gl.glPushMatrix();

        gl.glMultMatrixf(Utils.mkFloatBuffer(glMatrix));
        gl.glCallList(obj);

        gl.glPopMatrix();
    }//..

    @Override
    protected void initializePhysics(DynamicsWorld world) {

        Vector3f inertia = new Vector3f(0,0,0);

        float mass = p.mass;
        BoxShape boxShape;
        MotionState motionState;

        t = new Transform();
        t.setIdentity();
        t.origin.set(new Vector3f(p.x,p.y,p.z));
        boxShape = new BoxShape(new Vector3f(p.size,p.size,p.size));
        //boxShape = new BoxShape(new Vector3f(1,1,1));
        boxShape.calculateLocalInertia(mass,inertia);

        motionState = new DefaultMotionState(t);
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo(mass,motionState,boxShape,inertia);
        body = new RigidBody(info);
        body.setFriction(1);
        body.setRestitution(.5f);

        world.addRigidBody(body);
    }


    protected int genCube(){
        obj = gl.glGenLists(1);
        gl.glNewList(obj, gl.GL_COMPILE);

        texture.enable(gl);
        texture.bind(gl);
        gl.glBegin(gl.GL_QUADS);

        // Front Face
        gl.glNormal3f(0.0f, 0.0f, p.size);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-p.size, -p.size, p.size); // bottom-left of the texture and quad
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(p.size, -p.size, p.size);  // bottom-right of the texture and quad
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(p.size, p.size, p.size);   // top-right of the texture and quad
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-p.size, p.size, p.size);  // top-left of the texture and quad

        // Back Face
        gl.glNormal3f(0.0f, 0.0f, -p.size);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(-p.size, -p.size, -p.size);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(-p.size, p.size, -p.size);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(p.size, p.size, -p.size);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(p.size, -p.size, -p.size);

        // Top Face
        gl.glNormal3f(0.0f, p.size, 0.0f);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-p.size, p.size, -p.size);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-p.size, p.size, p.size);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(p.size, p.size, p.size);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(p.size, p.size, -p.size);

        // Bottom Face
        gl.glNormal3f(0.0f, -p.size, 0.0f);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(-p.size, -p.size, -p.size);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(p.size, -p.size, -p.size);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(p.size, -p.size, p.size);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(-p.size, -p.size, p.size);

        // Right face
        gl.glNormal3f(p.size, 0.0f, 0.0f);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(p.size, -p.size, -p.size);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(p.size, p.size, -p.size);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(p.size, p.size, p.size);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(p.size, -p.size, p.size);

        // Left Face
        gl.glNormal3f(-p.size, 0.0f, 0.0f);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-p.size, -p.size, -p.size);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(-p.size, -p.size, p.size);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(-p.size, p.size, p.size);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-p.size, p.size, -p.size);

        gl.glEnd();
        gl.glEndList();
        return obj;

    }//..
}// end Class Wall
