package wrld;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DynamicsWorld;
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

        angDamping=.1f;
        linDamping=.1f;
        friction=1f;
        shape = new BoxShape(new Vector3f(1,1,1));

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
        gl.glNormal3f(0.0f, 0.0f, 1);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-1, -1, 1); // bottom-left of the texture and quad
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(1, -1, 1);  // bottom-right of the texture and quad
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(1, 1, 1);   // top-right of the texture and quad
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-1, 1, 1);  // top-left of the texture and quad

        // Back Face
        gl.glNormal3f(0.0f, 0.0f, -1);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(-1, -1, -1);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(-1, 1, -1);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(1, 1, -1);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(1, -1, -1);

        // Top Face
        gl.glNormal3f(0.0f, 1, 0.0f);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-1, 1, -1);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-1, 1, 1);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(1, 1, 1);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(1, 1, -1);

        // Bottom Face
        gl.glNormal3f(0.0f, -1, 0.0f);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(-1, -1, -1);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(1, -1, -1);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(1, -1, 1);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(-1, -1, 1);

        // Right face
        gl.glNormal3f(1, 0.0f, 0.0f);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(1, -1, -1);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(1, 1, -1);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(1, 1, 1);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(1, -1, 1);

        // Left Face
        gl.glNormal3f(-1, 0.0f, 0.0f);
        gl.glTexCoord2f(textureLeft, textureBottom);
        gl.glVertex3f(-1, -1, -1);
        gl.glTexCoord2f(textureRight, textureBottom);
        gl.glVertex3f(-1, -1, 1);
        gl.glTexCoord2f(textureRight, textureTop);
        gl.glVertex3f(-1, 1, 1);
        gl.glTexCoord2f(textureLeft, textureTop);
        gl.glVertex3f(-1, 1, -1);

        gl.glEnd();
        gl.glEndList();
        return obj;

    }//..


}// end Class Cube
