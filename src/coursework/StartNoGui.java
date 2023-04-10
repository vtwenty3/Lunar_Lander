package coursework;

import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Example of how to to run the {@link ExampleEvolutionaryAlgorithm} without the need for the GUI
 * This allows you to conduct multiple runs programmatically 
 * The code runs faster when not required to update a user interface
 *
 */
public class StartNoGui {

	public static void main(String[] args) {
		//change condition to false to run once
		if (true){
			//multi threading
			double totalFitness = 0.0;
			int numThreads = 10;
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			List<Future<Double>> futures = new ArrayList<>();
			for (int i = 0; i < numThreads; i++) {
				Future<Double> future = executor.submit(() -> {
					Parameters.setDataSet(DataSet.Training);
					// Train the Neural Network using the Evolutionary Algorithm
					NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();
					nn.run();
					// Test the trained network on the test data set
					Parameters.setDataSet(DataSet.Test);
					return Fitness.evaluate(nn);
				});
				futures.add(future);
			}
			executor.shutdown();
			for (Future<Double> future : futures) {
				try {
					totalFitness += future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			double averageFitness = totalFitness / numThreads;
			System.out.println("Average Fitness on Test: " + averageFitness);
		} else {
		//Provided implementation of NoGui
		//Set the data set for training 
		Parameters.setDataSet(DataSet.Training);
		//Create a new Neural Network Trainer Using the above parameters 
		NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();
		//train the neural net (Go and make a coffee) 
		nn.run();
		/* Print out the best weights found
		 * (these will have been saved to disk in the project default directory) 
		 */
		System.out.println(nn.best);
		/**
		 * We now need to test the trained network on the unseen test Set
		 */
		Parameters.setDataSet(DataSet.Test);
		double fitness = Fitness.evaluate(nn);
		System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness);
		/**
		 * Or We can reload the NN from the file generated during training and test it on a data set 
		 * We can supply a filename or null to open a file dialog 
		 * Note that files must be in the project root and must be named *-n.txt
		 * where "n" is the number of hidden nodes
		 * ie  1518461386696-5.txt was saved at timestamp 1518461386696 and has 5 hidden nodes
		 * Files are saved automatically at the end of training
		 *  
		 *  Uncomment the following code and replace the name of the saved file to test a previously trained network 
		 */
//		NeuralNetwork nn2 = NeuralNetwork.loadNeuralNetwork("1234567890123-5.txt");
//		Parameters.setDataSet(DataSet.Random);
//		double fitness2 = Fitness.evaluate(nn2);
//		System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness2);
		}
	}
}
