package reader;

import model.Implicant;
import model.Varieable;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		FileScanner fileScanner=new FileScanner("raw.txt");
		while (fileScanner.hasNext())
		{
			String line=fileScanner.next();
			Implicant implicant=FileScanner.readLine(line);

			//System.out.println("Negative : "+implicant.getNegativeVariable());
			System.out.println("Positive : "+implicant.getPositiveVariable());
			System.out.println("\n");
		}


	}

}
