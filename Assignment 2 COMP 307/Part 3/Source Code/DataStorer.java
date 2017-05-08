package Part3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


/**
 * Read the breast cancer file, and store all of the instances as well as the correct classifications:
 * @author berceadrag
 *
 */
public class DataStorer {

	// A list of all the 699 cancer patients:
	private List <BreastCancerInstance> patients = new ArrayList<BreastCancerInstance> ();

	/**
	 * A datastorer to read the data from the specified file name. It will be used once for 
	 * the test set and once for the training set
	 * @param fileName
	 */
	public DataStorer (String fileName) {
		readCancerfile(fileName);
	}

	/**
	 * Read the cancer file and load the patients list, from the provided file name
	 * 
	 * @param fileName
	 */
	public void readCancerfile (String fileName) {
		try {
			FileReader fileReader = new FileReader (new File(fileName));
			BufferedReader scanner = new BufferedReader(fileReader);


			String currentLine = scanner.readLine();

			while (currentLine != null) {
				// current line representing the cancer patient:
				String [] currentLineAsArray = currentLine.split(",");
				// create the features array, and obtain the class for the current patient
				ArrayList<Integer> features = new ArrayList <Integer> ();
				// iterate over the features, and skip the id number:
				for (int i = 1; i < currentLineAsArray.length - 1; i++) {
					if (currentLineAsArray[i].equals("?"))
						features.add(-1);
					else
						features.add(Integer.parseInt(currentLineAsArray[i]));
				}

				int type = Integer.parseInt(currentLineAsArray[currentLineAsArray.length-1]);

				BreastCancerInstance currentPatient = new BreastCancerInstance (features,type);

				this.patients.add(currentPatient);

				currentLine = scanner.readLine();
			}

			scanner.close();
		}

		catch (IOException e) {e.printStackTrace(); System.out.println("Error, the file wasn't found!");}
	}

	// GETTER:
	public List<BreastCancerInstance> getPatients() {
		return patients;
	}

}
