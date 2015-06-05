package display;

import javax.swing.*;

/**
 * Created by guichi on 10/05/2015.
 */
public class Application
{
    public static void main(String[] args)
    {
        JFrame application = new GraphPanel();
        //application.setSize(800, 500);
        application.pack();
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        application.setVisible(true);
    }


}
