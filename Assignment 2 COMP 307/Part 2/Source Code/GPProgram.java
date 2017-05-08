
import org.jgap.*;
import org.jgap.gp.*;
import org.jgap.gp.function.*;
import org.jgap.gp.impl.*;
import org.jgap.gp.terminal.*;


public class GPProgram extends GPProblem {

	public static Variable vx;

	protected static Float[] x = new Float[20];

	protected static float[] y = new float[20];

	public GPProgram(GPConfiguration a_conf) throws InvalidConfigurationException {
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
				CommandGene.FloatClass
		};

		Class[][] argTypes = {
				{}
		};

		CommandGene[][] nodeSets = { {
			vx = Variable.create(conf, "X", CommandGene.FloatClass),
					new Multiply(conf, CommandGene.FloatClass),
					new Subtract(conf, CommandGene.FloatClass),
					new Pow(conf, CommandGene.FloatClass),
					new Add(conf, CommandGene.FloatClass),
					new Terminal(conf, CommandGene.FloatClass, 1.0d, 4.0d, true)}
		};


		// Use the Datastorer to load in the regression text file, and use the x and y coordinates from there.
		DataStorer dataStorer = new DataStorer();
		x = dataStorer.getX();
		y = dataStorer.getY();

		// Create genotype with initial population. Here, we use the declarations
		// made above:
		return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets,20, true);
	}


	public static void main(String[] args) throws Exception {

		GPConfiguration config = new GPConfiguration();

		config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator()); //DeltaGPFitnessEvaluator(): Lower fitness values => Higher rank
		config.setMaxInitDepth(4); // Maximum depth of 6 for the root node. Values that are too high may result in bad performance
		config.setPopulationSize(100);
		config.setMaxCrossoverDepth(8);

		config.setFitnessFunction(new GPProgram.MyFormulaFitnessFunction());
		config.setStrictProgramCreation(true);
		GPProblem problem = new GPProgram(config);

		GPGenotype gp = problem.create();
		gp.setVerboseOutput(true);

		// For the stoping criteria: Keep evolving the program, until either a fitness value of 0 is produced. 
		// Or 4,000 evolutions have been made.
		int evolutions = 0;
		while (true) {
			gp.evolve(1);
			if (gp.getAllTimeBest().getFitnessValue() == 0 || evolutions == 4000) 
				break;
			evolutions++;
		}

		// If the fitness value is 0, I do not want to print the fitness value, and the function twice. So only 
		// output the solution here, if gp has evolved 4,000 evolutions and the fitness value is still not 0
		if (evolutions == 4000) 
			gp.outputSolution(gp.getAllTimeBest());

		// Create a graphical tree of the best solution's program and write it to
		// a PNG file.
		problem.showTree(gp.getAllTimeBest(), "mathproblem_best.png");
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
			double error = 0.0f;
			Object[] noargs = new Object[0];

			for (int i = 0; i < 20; i++) {

				vx.set(x[i]);
				try {

					double result = ind.execute_float(0, noargs);

					error += Math.abs(result - y[i]);

					if (Double.isInfinite(error)) 
						return Double.MAX_VALUE;

				} catch (ArithmeticException ex) {
					System.out.println("x = " + x[i].floatValue());
					System.out.println(ind);
					throw ex;
				}
			}

			// If the error is small enough, then use it
			if (error < 0.001) 
				error = 0.0d;

			return error;
		}
	}
}
