package display;

import java.util.Observable;

/**
 * Created by guichi on 21/05/15.
 */
public  class FilePublisher extends Observable
{
    String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        setChanged();
        notifyObservers();
    }

    public FilePublisher() {


    }
}