package coursework;

import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
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
	//Parameters.parameter = value; to change from here
	public static void main(String[] args) {


		//change condition to false to run once
			//multi threading
//			double totalFitness = 0.0;
//			int numThreads = 10;
//			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
//			List<Future<Double>> futures = new ArrayList<>();
//			for (int i = 0; i < numThreads; i++) {
//				Future<Double> future = executor.submit(() -> {
//					Parameters.setDataSet(DataSet.Training);
//					// Train the Neural Network using the Evolutionary Algorithm
//					NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();
//					nn.run();
//					// Test the trained network on the test data set
//					Parameters.setDataSet(DataSet.Test);
//					return Fitness.evaluate(nn);
//				});
//				futures.add(future);
//			}
//			executor.shutdown();
//			for (Future<Double> future : futures) {
//				try {
//					totalFitness += future.get();
//				} catch (InterruptedException | ExecutionException e) {
//					e.printStackTrace();
//				}
//			}
//			double averageFitness = totalFitness / numThreads;
//			System.out.println("Average Fitness on Test: " + averageFitness);













//		List<Double> mutateRates = Arrays.asList(0.01, 0.05, 0.10);
//		List<Double> preserveElitePercentages = Arrays.asList(0.05, 0.10, 0.15);
//		List<String> activationFunctions = Arrays.asList("SELU", "TANH");
//
//		for (double mutateRate : mutateRates) {
//			for (double preserveElitePercentage : preserveElitePercentages) {
//				for (String activationFunction : activationFunctions) {
//					Parameters.mutateRate = mutateRate;
//					Parameters.preserveElitePercentage = preserveElitePercentage;
//					Parameters.activationFunction = activationFunction;
//
//					double totalFitness = 0.0;
//					int numThreads = 10;
//					ExecutorService executor = Executors.newFixedThreadPool(numThreads);
//					List<Future<Double>> futures = new ArrayList<>();
//					for (int i = 0; i < numThreads; i++) {
//						Future<Double> future = executor.submit(() -> {
//							Parameters.setDataSet(DataSet.Training);
//							// Train the Neural Network using the Evolutionary Algorithm
//							NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();
//							nn.run();
//							// Test the trained network on the test data set
//							Parameters.setDataSet(DataSet.Test);
//							return Fitness.evaluate(nn);
//						});
//						futures.add(future);
//					}
//					executor.shutdown();
//					for (Future<Double> future : futures) {
//						try {
//							totalFitness += future.get();
//						} catch (InterruptedException | ExecutionException e) {
//							e.printStackTrace();
//						}
//					}
//					double averageFitness = totalFitness / numThreads;
//					System.out.println("mutateRate: " + mutateRate + ", preserveElitePercentage: " + preserveElitePercentage + ", activationFunction: " + activationFunction + ", averageFitness: " + averageFitness);
//				}
//			}
//		}









	}
}
