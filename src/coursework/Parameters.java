package coursework;

import java.lang.reflect.Field;
import java.util.Random;
import model.LunarParameters;
import model.NeuralNetwork;
import model.LunarParameters.DataSet;

public class Parameters {

	/**
	 * These parameter values can be changed You may add other Parameters as
	 * required to this class
	 * 
	 */
	public static int numHidden = 20;
	private static int numGenes = calculateNumGenes();
	public static double minGene = -0.035;// specifies minimum and maximum weight values
	public static double maxGene = +0.035;
	public static int maxEvaluations = 20000;
	// Parameters for mutation
	// Rate = probability of changing a gene
	// Change = the maximum +/- adjustment to the gene value
	//New parameters
	public static int popSize = 900;
	public static  int tournamentSize = 380; // Tournament size
	public static final String crossoverMethod = "twoPoint"; // Crossover method: "onePoint" or "twoPoint"
	public static double preserveElitePercentage  = 0.7; // delta change for mutation operator
	public static  String activationFunction = "SELU"; // Crossover method: "SELU" or "TANH"

	//For sobol need to enable math3, check EvolutionaryAlgorithm.java
	public static  String initialistionMethod = "lhs"; // init, typo but.. Choices: "random" or "sobol" or "lhs"

	public static double mutateChangeBest = 0.55; // delta change for mutation operator
	//Change the following to influence the adaptive mutation rate
	public static double mutateChangePopulation = 0.42; // delta change for mutation operator
	public static double minMutationRate = 0.001; // delta change for mutation operator
	public static double maxMutationRate = 0.23; // delta change for mutation operator
	public static int maxDiversity = 300;
	//Used in normal mutation. Does not do anyting in current implementation
	public static double mutateRate = 0.01; // 0.01 mutation rate for mutation operator
	public static double mutateChange = 0.1; // delta change for mutation operator




	// Random number generator used throughout the application
	public static long seed = System.currentTimeMillis();
	public static Random random = new Random(seed);

	// set the NeuralNetwork class here to use your code from the GUI
	public static Class neuralNetworkClass = ExampleEvolutionaryAlgorithm.class;

	/**
	 * Do not change any methods that appear below here.
	 * 
	 */

	public static int getNumGenes() {
		return numGenes;
	}

	private static int calculateNumGenes() {
		int num = (NeuralNetwork.numInput * numHidden) + (numHidden * NeuralNetwork.numOutput) + numHidden
				+ NeuralNetwork.numOutput;
		return num;
	}

	public static int getNumHidden() {
		return numHidden;
	}

	public static void setHidden(int nHidden) {
		numHidden = nHidden;
		numGenes = calculateNumGenes();
	}

	public static String printParams() {
		String str = "";
		for (Field field : Parameters.class.getDeclaredFields()) {
			String name = field.getName();
			Object val = null;
			try {
				val = field.get(null);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			str += name + " \t" + val + "\r\n";

		}
		return str;
	}

	public static void setDataSet(DataSet dataSet) {
		LunarParameters.setDataSet(dataSet);
	}

	public static DataSet getDataSet() {
		return LunarParameters.getDataSet();
	}

	public static void main(String[] args) {
		printParams();
	}
}
