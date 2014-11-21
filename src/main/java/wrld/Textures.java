package wrld;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import javax.media.opengl.GL2;
import javax.media.opengl.GLException;
import java.io.IOException;

/**
 * Created by Kevin on 12/19/13.
 */
public class Textures {

    GL2 gl;

    public static Texture sky,grass,box, metal, dirt,
            bball, mball;

    Textures(GL2 gl) {
        this.gl =gl;

        //sky = loadTexture(sky,"/com/box/img/sky.jpg",".jpg");
        mball = loadTexture(mball, "/tex/ball.jpg",".jpg");
        bball = loadTexture(bball, "/tex/bball.jpg",".jpg");
        grass = loadTexture(grass, "/tex/ground.png",".png");
        sky = loadTexture(sky, "/tex/sky.jpg",".jpg");


    }//..

    public Texture loadTexture(Texture texture,String textureFileName, String textureFileType){
        // Load texture from image
        try {
            // Create a OpenGL Texture object from (URL, mipmap, file suffix)
            // Use URL so that can read from JAR and disk file.
            //BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(textureFileName));

            texture = TextureIO.newTexture( getClass().getResourceAsStream(textureFileName), true, textureFileType);

            // Use linear filter for texture if image is larger than the original texture
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
            // Use linear filter for texture if image is smaller than the original texture
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST_MIPMAP_LINEAR);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_BASE_LEVEL ,0);
            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAX_LEVEL , 10 );

            gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE, gl.GL_REPEAT);
            gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_REPEAT);
            gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_REPEAT);
            gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(gl.GL_TEXTURE_2D,gl.GL_TEXTURE_COMPARE_FAIL_VALUE_ARB,0.5f);

            // Texture image flips vertically. Shall use TextureCoords class to retrieve
            // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
           /* TextureCoords textureCoords = texture.getImageTexCoords();
            textureTop = textureCoords.top();
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right(); */
        } catch (GLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texture;
    }//..

}// end Class Textures
