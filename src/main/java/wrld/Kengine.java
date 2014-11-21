package wrld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by kevin on 11/13/14.
 */
public class Kengine extends JFrame {

    static JFrame frame;
    Scene scene;

    Kengine(){
        frame=this;
        setTitle("wrld.NewWorld");
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



    public static void main(String[] args) {
        new Kengine();
    }//..

}// wrld.NewWorld
