package wrld;

import javax.media.opengl.GL2;

/**
 * Created by kevin on 11/13/14.
 */
public class Light {

    protected static float amb = .4f;
    protected static float[]
        L0_DIFFUSE  = new float[]{amb,amb,amb,1f},
        L0_SPECULAR = new float[]{0f,0f,0f,1f},

        L1_AMBIENT = new float[]{1f,1f,1f,1f},
        L1_DIFFUSE  = new float[]{1f,1f,1f,1f},
        L1_SPECULAR = new float[]{1f,1f,1f,1f},
        L1_POSITION = new float[]{-1500, 1000f, 0, 1};

    public static void sun(GL2 gl){
        gl.glPushMatrix();
        gl.glTranslatef(0, 0, 0);

        // Backlight - so shadows arent black
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_DIFFUSE, L0_DIFFUSE, 0);
        gl.glLightfv(gl.GL_LIGHT0, gl.GL_SPECULAR, L0_SPECULAR, 0);

        // Directional wrld.Light
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_AMBIENT, L1_AMBIENT, 0);
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_DIFFUSE, L1_DIFFUSE, 0);
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_SPECULAR, L1_SPECULAR, 0);
        gl.glLightfv(gl.GL_LIGHT1, gl.GL_POSITION, L1_POSITION,0);

        gl.glEnable(gl.GL_LIGHT0);
        gl.glEnable(gl.GL_LIGHT1);
        gl.glPopMatrix();
    }//..

}// wrld.Light
