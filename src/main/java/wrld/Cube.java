package wrld;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
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
 * Created by Kevin on 12/28/13.
 */


public class Cube extends CollisionModel {


    Texture texture;
    protected float textureTop, textureBottom, textureLeft, textureRight;


    Cube(GL2 gl, Point3d p, DynamicsWorld world, Texture texture) {
        super(gl,p,1);
        this.texture =texture;

        scale=false;
        angDamping=.1f;
        linDamping=.1f;
        friction=1f;
        shape = new BoxShape(new Vector3f(p.size,p.size,p.size));


        callist = genCube();
        initializePhysics(world);
    }//..

    protected int genCube(){
        int obj;
        TextureCoords textureCoords = texture.getImageTexCoords();
        textureTop = textureCoords.top();
        textureBottom = textureCoords.bottom();
        textureLeft = textureCoords.left();
        textureRight = textureCoords.right();

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


}// end Class Cube
