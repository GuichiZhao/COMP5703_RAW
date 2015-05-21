package display;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by guichi on 21/05/15.
 */
public class Chooser extends JFrame{
    FilePublisher publisher;
    String fileName;

    public FilePublisher getPublisher() {
        return publisher;
    }

    public Chooser() {
        publisher=new FilePublisher();
        JButton addButton = new JButton("Select a file");
        JFileChooser fc = new JFileChooser();

        addButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {


                        int returnVal = fc.showOpenDialog(null);

                        File file = fc.getSelectedFile();
                        publisher.setFileName(file.toString());
                        fileName = file.toString();
//                        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
//
//                            String sCurrentLine;
//
//                            while ((sCurrentLine = br.readLine()) != null) {
//                                System.out.println(sCurrentLine);
//                            }
//
//                        } catch (IOException a) {
//                            a.printStackTrace();
//                        }


                        //This is where a real application would open the file.


                    }
                }
        );
        add(addButton);
    }



}
