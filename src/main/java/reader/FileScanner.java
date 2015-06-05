package reader;

import model.Implicant;
import model.Varieable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FileScanner implements Iterator<String> {

    BufferedReader reader = null;

    public FileScanner(String filename) throws FileNotFoundException {

        //use buffering, reading one line at a time
        reader = new BufferedReader(new FileReader(filename));
    }

    @Override
    public String next()
    {
        String res = null;
        if (hasNext()) {
            try {
                res = reader.readLine();
            } catch (IOException e) {
                handleException(e);
            }
        }
        return res;
    }
    @Override
    public boolean hasNext() {
        boolean res = false;
        try {
            res = reader.ready();
        } catch (IOException e) {
            handleException(e);
        }
        return res;
    }
    @Override
    protected void finalize() throws Throwable {
        try {
            if (reader != null) {
                reader.close();  // close opened file
                reader = null;
            }
        } finally {
            super.finalize();
        }
    }
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    private void handleException(Exception ex) {
        System.err.println("Problem reading file: " + ex);
    }
    public static Implicant readLine(String line) {
        Implicant implicant = new Implicant();
        String tmp = "";
        char[] l = line.toCharArray();
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < l.length - 1; i++) {
            Character c = l[i];
            if (Character.isDigit(c)) {
                Character cc = l[i + 1];
                if (!Character.isDigit(cc)) {
                    offsets.add(i);
                }
            }
        }
        if (offsets.size() != 0) {
            tmp = line.substring(0, offsets.get(0) + 1);

            addVarieabelToImplicant(implicant, tmp);

            for (int i = 0; i < offsets.size(); i++) {
                if (i < offsets.size() - 1) {
                    addVarieabelToImplicant(implicant, line.substring(offsets.get(i) + 1, offsets.get(i + 1) + 1));
                }

            }

            addVarieabelToImplicant(implicant, line.substring(offsets.get(offsets.size() - 1) + 1));
        } else {
            addVarieabelToImplicant(implicant, line);
        }

        return implicant;
    }
    private static void addVarieabelToImplicant(Implicant implicant, String varieable) {
        if (varieable.substring(0, 1).equals("~")) {
            implicant.add(new Varieable(false, varieable.substring(1)));
        } else {
            implicant.add(new Varieable(true, varieable));
        }
    }
}
