import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


/**
 * This class will read the regression.txt file, and store arrays of the x and y coordinates.
 * These two arrays will be used by the GP Program.
 * @author Dragos
 *
 */
public class DataStorer {
	
	private Float [] x = new Float [20];
	private float [] y = new float [20];
	
	public DataStorer () {
		readRegressionFile();
	}
	
	/**
	 * Read the file and load the Float arrays, with the x and y coordinates
	 */
	public void readRegressionFile () {
		try {
			FileReader fileReader = new FileReader (new File("regression.txt"));
			BufferedReader scanner = new BufferedReader(fileReader);
			
			// the first two lines in the file are of no use:
			scanner.readLine();
			scanner.readLine();
			
			// the index position for the next insertion into the two x and y arrays: 
			int index = 0;
			String currentLine = scanner.readLine();
			
			while (currentLine != null) {
				// the current line directly from the regression.txt file:
				String [] currentLineAsArray = currentLine.split(" ");
				// the list that will contain the x and y coordinates:
				List<Float> xAndYCoordinatesAsString = new ArrayList<Float> ();
				// search through the line from the text file, and obtain the coordinates:
				for (String s: currentLineAsArray) {
					if (!s.equals(""))
						xAndYCoordinatesAsString.add(Float.parseFloat(s));
				}
				
				Float xCor = xAndYCoordinatesAsString.get(0);
				Float yCor = xAndYCoordinatesAsString.get(1);

				x[index] = xCor;
				y[index] = yCor;
				
				index++;
				currentLine = scanner.readLine();
			}
			
			scanner.close();
		} 
		
		catch (IOException e) {e.printStackTrace(); System.out.println("Error, the file wasn't found!");}
	}
	
	// GETTERS AND SETTERS:
	
	public Float[] getX() {
		return x;
	}

	public float[] getY() {
		return y;
	}
	
}
