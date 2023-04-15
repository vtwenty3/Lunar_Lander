package coursework;

import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
//		change condition to false to run once
//			multi threading

		if (true) {
			//Parameters.popSize = popSize;
			long startTime = System.currentTimeMillis();
			double totalFitnessTraining = 0.0;
			double totalFitnessTest = 0.0;
			int numThreads = 10;
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			List<Future<Double[]>> futures = new ArrayList<>();
			for (int i = 0; i < numThreads; i++) {
				Future<Double[]> future = executor.submit(() -> {
					Parameters.setDataSet(DataSet.Training);
					// Train the Neural Network using the Evolutionary Algorithm
					NeuralNetwork nn = new ExampleEvolutionaryAlgorithm();
					nn.run();
					double fitnessTraining = Fitness.evaluate(nn);
					// Test the trained network on the test data set
					Parameters.setDataSet(DataSet.Test);
					double fitnessTest = Fitness.evaluate(nn);
					return new Double[] {fitnessTraining, fitnessTest};
				});
				futures.add(future);
			}
			executor.shutdown();
			for (Future<Double[]> future : futures) {
				try {
					Double[] fitness = future.get();
					totalFitnessTraining += fitness[0];
					totalFitnessTest += fitness[1];
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
			double averageFitnessTraining = totalFitnessTraining / numThreads;
			double averageFitnessTest = totalFitnessTest / numThreads;
			System.out.println("Average Fitness on Training: " + averageFitnessTraining);
			System.out.println("Average Fitness on Test: " + averageFitnessTest);
			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime) / 1000;
			System.out.println("Time taken (in seconds): " + duration);

		} else {

			List<Integer> popSizes = Arrays.asList(50, 100, 200);
			List<Double> preserveElitePercentages = Arrays.asList(0.2, 0.4, 0.6);
			List<String> activationFunctions = Arrays.asList("SELU", "TANH");

			try (PrintWriter writer = new PrintWriter("results.txt")) {
				for (int popSize : popSizes) {
					for (double preserveElitePercentage : preserveElitePercentages) {
						for (String activationFunction : activationFunctions) {

							Parameters.popSize = popSize;
							Parameters.preserveElitePercentage = preserveElitePercentage;
							Parameters.activationFunction = activationFunction;

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
							String resultString = String.format("popSize: %d, preserveElitePercentage: %f, activationFunction: %s, averageFitness: %f",
									popSize, preserveElitePercentage, activationFunction, averageFitness);
							System.out.println(resultString);
							writer.println(resultString);
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}


	}






	}

