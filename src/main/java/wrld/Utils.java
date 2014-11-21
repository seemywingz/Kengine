package wrld;

import sun.audio.AudioPlayer;
import javax.imageio.ImageIO;
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
}// end interface wrld.Logic

public class Utils {

    //Class c = getClass();

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
                while(true){
                    try {
                        logic.apply();
                        Thread.sleep(20);
                    }catch (Exception e){}
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
