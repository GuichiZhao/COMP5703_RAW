package display;

import javax.swing.*;

/**
 * Created by guichi on 10/05/2015.
 */
public class Application {
    public static void main(String[] args) {


//        Chooser chooser=new Chooser();
//
//        FilePublisher publisher=chooser.getPublisher();
//        chooser.pack();
//        chooser.setVisible(true);
//        chooser.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JFrame application = new GraphPanel();
        application.pack();
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //  application.setSize(800, 500);
        application.setVisible(true);
    }
}
