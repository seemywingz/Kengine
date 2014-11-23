package wrld;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import sun.audio.AudioPlayer;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


interface Logic {
    public abstract void apply() throws Exception;
}// end interface Logic

public class Utils {

    protected static Clip mkClip(Class c,String soundFile){
        Clip clip = null;
        try{
            AudioInputStream ais = AudioSystem.getAudioInputStream(c.getClass().getResource(soundFile));
            DataLine.Info lineInfo = new DataLine.Info(Clip.class, ais.getFormat());
            clip = (Clip) AudioSystem.getLine(lineInfo);
            clip.open(ais);

        }catch (Exception e){
            e.printStackTrace();
        }
        return clip;
        //clip.loop(2);
        //Clip theme = AudioSystem.getClip();
    }//..

    public static Texture loadTexture(GL2 gl,String textureFileName){
        Texture texture;
        String delims = "[.]+";
        String file[] = textureFileName.split(delims);
        // Load texture from image
        try {
            // Create a OpenGL Texture object from (URL, mipmap, file suffix)
            // Use URL so that can read from JAR and disk file.
            //BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(textureFileName));

            texture = TextureIO.newTexture(Utils.class.getResourceAsStream(textureFileName), true, file[1]);

            // Use linear filter for texture if image is larger than the original texture
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

            // Use linear filter for texture if image is smaller than the original texture
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST_MIPMAP_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_BASE_LEVEL ,0);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAX_LEVEL , 20 );

            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE, GL2.GL_REPEAT);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
            gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

            // Texture image flips vertically. Shall use TextureCoords class to retrieve
            // the top, bottom, left and right coordinates, instead of using 0.0f and 1.0f.
           /* TextureCoords textureCoords = texture.getImageTexCoords();
            textureTop = textureCoords.top();
            textureBottom = textureCoords.bottom();
            textureLeft = textureCoords.left();
            textureRight = textureCoords.right(); */
        } catch (Exception e){
            texture = null;
        }

        return texture;
    }//..

    public static JLabel mkGraphic(Class c,String image,int x,int y,int w, int h){
        try {
            ImageIcon img;
            img = new ImageIcon(ImageIO.read(c.getResourceAsStream(image)));
            img = Utils.scaleImageIcon(img, w, h);
            JLabel graphic = new JLabel(img);
            graphic.setBounds(x, y, w, h);
            return graphic;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }//..

    public static ImageIcon scaleImageIcon(ImageIcon icon, int w, int h){
        Image img = icon.getImage() ;
        return new ImageIcon(  img.getScaledInstance( w, h,  Image.SCALE_SMOOTH )  );
    }//..


    protected static void startThread(final Logic logic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        logic.apply();
                    }catch (Exception e){e.printStackTrace();}
            }
        }).start();
    }//..

    protected static void startThreadLoop(final Logic logic){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        logic.apply();
                        Thread.sleep(20);
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        }).start();
    }//..

    protected static FloatBuffer mkFloatBuffer(float vertices[]){
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());    // use the device hardware's native byte order
        FloatBuffer fb = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
        fb.put(vertices);    // add the coordinates to the FloatBuffer
        fb.position(0);      // set the buffer to read the first coordinate
        return fb;
    }//..

    public static void wait(int mils){
        try {
            Thread.sleep(mils);
        }catch (Exception e){}
    }//..

    public static void playSound(Class c,String path){
        AudioPlayer.player.start(c.getResourceAsStream(path));
    }//..

    protected static float random(int max){
        double rand =  Math.random()*max;
        if((int)(Math.random()*100) < 50)
            rand = -rand;
        return (float) rand;
    }//..

}// end Class wrld.Utils
