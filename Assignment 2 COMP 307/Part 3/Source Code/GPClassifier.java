package Part3;

import java.util.List;
import java.util.Scanner;
import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.*;
import org.jgap.gp.terminal.*;


public class GPClassifier extends GPProblem {

	// create 9 variables, for each of the 9 features that shall be used:
	public static Variable [] varArray = new Variable [9];

	// the current list of cancer patients being used. This will be changed from training instances to test instances:
	private static List<BreastCancerInstance> patients;

	public GPClassifier(GPConfiguration a_conf) throws InvalidConfigurationException {
		super(a_conf);
	}

	/**
	 * This method is used for setting up the commands and terminals that can be
	 * used to solve the problem.
	 * @return GPGenotype
	 * @throws InvalidConfigurationException
	 */
	public GPGenotype create() throws InvalidConfigurationException {

		GPConfiguration conf = getGPConfiguration();

		Class[] types = {
				CommandGene.IntegerClass
		};

		Class[][] argTypes = {
				{}
		};

		// initialize all the variables from the varArray, these will be the features used by the gp classifier
		for (int i = 0; i < 9; i++)
			this.varArray[i] =  Variable.create(conf, "F"+i, CommandGene.IntegerClass);

		CommandGene[][] nodeSets = { {

			this.varArray[0],
			this.varArray[1],
			this.varArray[2],
			this.varArray[3],
			this.varArray[4],
			this.varArray[5],
			this.varArray[6],
			this.varArray[7],
			this.varArray[8],
			new Multiply(conf, CommandGene.IntegerClass),
			new Divide(conf, CommandGene.IntegerClass),
			new Subtract(conf, CommandGene.IntegerClass),
			new Sine(conf, CommandGene.FloatClass),
			new Exp(conf, CommandGene.FloatClass),
			new Pow(conf, CommandGene.FloatClass),
			new Add(conf, CommandGene.IntegerClass),
			new Terminal(conf, CommandGene.IntegerClass, -11, 11, true)}
		};

		// Create genotype with initial population. Here, we use the declarations
		// made above:
		return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets,20, true);
	}


	public static void main(String[] args) throws Exception {

		// Prompt the user for training and test set files:
		Scanner scan = new Scanner (System.in);

		System.out.println("Please enter the name of the training set file: ");
		String trainingSet = scan.next();

		System.out.println("Please enter the name of the test set file: ");
		String testSet = scan.next();


		System.out.println("--------Finding Formula From The Training Set Now--------");
		DataStorer storerForTraining = new DataStorer(trainingSet);
		patients = storerForTraining.getPatients();

		GPConfiguration config = new GPConfiguration();

		// use default fitness evaluator, because in this case the fitness is a measure of how many correct classifications were made
		// so the higher the fitness the better!
		config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
		config.setMaxInitDepth(4); // Maximum depth of 6 for the root node. Values that are too high may result in bad performance
		config.setPopulationSize(100);
		config.setMaxCrossoverDepth(8);

		config.setFitnessFunction(new GPClassifier.MyFormulaFitnessFunction());
		config.setStrictProgramCreation(true);
		GPProblem problem = new GPClassifier(config);

		GPGenotype gp = problem.create();
		gp.setVerboseOutput(true);

		// Evolve for 400 generations. 
		gp.evolve(400);
		// Create a graphical tree of the best solution's program and write it to a PNG file.
		problem.showTree(gp.getAllTimeBest(), "mathproblem_best.png");
		
		System.out.println("--------Performance on Training set--------");
		gp.outputSolution(gp.getAllTimeBest());

		// after GP has finished training, get the best formula that it finds from the training set
		// and use it on the test set:
		IGPProgram bestSolution = gp.getAllTimeBest();

		System.out.println("--------Finding Test Set Classification Accuracy--------");
		DataStorer storerForTesting = new DataStorer(testSet);
		patients = storerForTesting.getPatients();

		System.out.println("Classification accuracy on test set: " + 
				new GPClassifier.MyFormulaFitnessFunction().computeRawFitness(bestSolution) + "%");

		scan.close();
	}

	/**
	 * Fitness function for evaluating the produced fomulas, represented as GP
	 * programs. The fitness is computed by calculating the result (Y) of the
	 * function/formula for integer inputs 0 to 20 (X). The sum of the differences
	 * between expected Y and actual Y is the fitness, the lower the better (as
	 * it is a defect rate here).
	 */
	public static class MyFormulaFitnessFunction
	extends GPFitnessFunction {

		protected double evaluate(final IGPProgram a_subject) {
			return computeRawFitness(a_subject);
		}

		public double computeRawFitness(final IGPProgram ind) {

			double correctClassifications = 0;
			Object [] objArray = new Object [0];

			for (int i = 0; i < patients.size(); i++) {

				// set up the 9 features here:
				for (int j = 0; j < varArray.length;j++)
					varArray[j].set(patients.get(i).getFeatureAt(j));

				try {
					// get the GP Output from the current program:
					int output = ind.execute_int(0, objArray);

					// perform the classification:
					if (output >= 0)
						output = 2;
					else
						output = 4;

					// check if the program output was indeed correct:
					if (output == patients.get(i).getType())
						correctClassifications++;


				} catch (ArithmeticException ex) {
					System.out.println("x = " + patients.get(i));
					System.out.println(ind);
					throw ex;
				}
			}

			return (correctClassifications/(patients.size())*100);
		}
	}
}
