package wrld;


import com.bulletphysics.collision.shapes.SphereShape;
import com.jogamp.opengl.util.texture.Texture;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Created by kevin on 11/18/14.
 */


public class Ball extends CollisionModel {

    protected Texture texture;

    Ball(GL2 gl, Point3d p, Texture texture) {
        super(gl, p, 0);
        this.texture=texture;
        callist=mkGLCallList();
        shape = new SphereShape(1);
    }//..

    protected int mkGLCallList(){
        GLU glu = new GLU();
        int callList;

        GLUquadric quad = glu.gluNewQuadric();
        // material properties
        float[] no_mat = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] mat_ambient = {1f, 1f, 1f, 1.0f};
        float[] mat_diffuse = {1, 1, 1, 1.0f};
        float[] mat_specular = {1.0f, 1.0f, 1.0f, 1.0f};
        float no_shininess = 0.0f;
        float low_shininess = 5.0f;
        float high_shininess = 100.0f;
        float[] mat_emission = {0.3f, 0.2f, 0.2f, 0.0f};

        callList = gl.glGenLists(1);
        gl.glNewList(callList, gl.GL_COMPILE);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_AMBIENT, Utils.mkFloatBuffer(no_mat));
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_DIFFUSE, Utils.mkFloatBuffer(mat_diffuse));
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_SPECULAR, Utils.mkFloatBuffer(mat_specular));
        gl.glMaterialf(gl.GL_FRONT, gl.GL_SHININESS, high_shininess);
        gl.glMaterialfv(gl.GL_FRONT, gl.GL_EMISSION, Utils.mkFloatBuffer(no_mat));
        glu.gluQuadricTexture(quad, true);
        glu.gluQuadricOrientation(quad, glu.GLU_OUTSIDE);
        texture.enable(gl);
        texture.bind(gl);
        glu.gluSphere(quad, 1, 36, 72);
        texture.disable(gl);
        gl.glEndList();
        return callList;
    }//..
}// Ball
