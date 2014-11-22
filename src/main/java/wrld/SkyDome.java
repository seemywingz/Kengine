package wrld;

import com.jogamp.opengl.util.texture.Texture;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * Created by Kevin on 12/29/13.
 */
public class SkyDome {

     GL2 gl;
     GLU glu;
     GLUquadric sQuad;
     int callist = 0, radius;
     protected Texture texture;

    Point3d p;

    SkyDome(GL2 gl, Texture texture, Point3d p) {
        this.p = p;
        this.texture = texture;
        this.gl = gl;
        glu = new GLU();
        sQuad = glu.gluNewQuadric();
        radius = 8000;
        gegGLCalList();
    }//..

    public void draw(){

        gl.glPushMatrix();

        gl.glScalef(p.size,p.size,p.size);
        gl.glTranslatef(p.x, p.y, p.z);
        gl.glRotated(90, 1, 0, 0);
        gl.glCallList(callist);

        gl.glPopMatrix();
    }//..

    public void setPos(Point3d pos){p.x=pos.x;p.y=pos.y;}//..



    protected void gegGLCalList(){
        callist = gl.glGenLists(1);
        gl.glNewList(callist, gl.GL_COMPILE);
        texture.enable(gl);
        texture.bind(gl);
        glu.gluQuadricTexture(sQuad, true);
        glu.gluQuadricOrientation(sQuad, glu.GLU_INSIDE);
        glu.gluSphere(sQuad, radius, 36, 72);

        gl.glEndList();
    }//..

}// end Class Sky
