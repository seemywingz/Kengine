package wrld;

import com.jogamp.opengl.util.texture.Texture;

import javax.media.opengl.GL2;

/**
 * Created by Kevin on 12/19/13.
 * Stores created textures
 */
public class Textures {

    GL2 gl;

    public static Texture sky,grass, basketBall;

    Textures(GL2 gl) {
        this.gl =gl;

        //sky = loadTexture(sky,"/com/box/img/sky.jpg",".jpg");
        basketBall = Utils.loadTexture(getClass(),gl, "/tex/bball.jpg");
        grass = Utils.loadTexture(getClass(),gl, "/tex/ground.png");
        sky = Utils.loadTexture(getClass(),gl, "/tex/sky.jpg");


    }//..

}// end Class Textures
