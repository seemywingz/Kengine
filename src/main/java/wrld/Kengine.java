package wrld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Created by kevin on 11/13/14.
 */
public class Kengine extends JFrame {

    static JFrame frame;
    Scene scene;
    boolean openJDK;

    Kengine(){
        openJDK=isOpenJDK();
        frame=this;
        setTitle("Kengine");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        setBounds(0, 0, gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
        setUndecorated(true);
        setLocationRelativeTo(null);
        //setAlwaysOnTop(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        scene = new Scene(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Utils.wait(4);
                frame.dispose();
                System.exit(0);
            }
        });
        setVisible(true);
    }//..

    public boolean isOpenJDK(){
        //System.out.println(System.getProperty("java.vm.name"));
        String delims = "[ ]+";
        String jvmName[] = System.getProperty("java.vm.name").split(delims);
        if(jvmName[0].toLowerCase().equals("openjdk"))
            return true;
        return false;
    }//..


    public static void main(String[] args) {
        new Kengine();
    }//..

}// wrld.NewWorld
