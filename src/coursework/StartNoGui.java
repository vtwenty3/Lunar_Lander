package coursework;
import model.Fitness;
import model.Individual;
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


		String control = "multi";
		//String control = "original";
//		String control = "file";


		if (control.equals("multi")) { //change to false to run once, ture runs on 10 threads
			long startTime = System.currentTimeMillis();
			double totalFitnessTraining = 0.0;
			double totalFitnessTest = 0.0;
			int numThreads = 12;
			int number = 0;

// Train numThreads neural networks using ExecutorService
			ExecutorService executor = Executors.newFixedThreadPool(numThreads);
			List<Future<NeuralNetwork>> trainingFutures = new ArrayList<>();
			for (int i = 0; i < numThreads; i++) {
				Future<NeuralNetwork> future = executor.submit(() -> {
					Parameters.setDataSet(DataSet.Training);
					NeuralNetwork nnTraining = new ExampleEvolutionaryAlgorithm();
					nnTraining.run();
					return nnTraining;
				});

				trainingFutures.add(future);
			}

			List<NeuralNetwork> trainedNetworks = new ArrayList<>();
			for (Future<NeuralNetwork> future : trainingFutures) {
				try {
					trainedNetworks.add(future.get());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			executor.shutdown();

			long endTime = System.currentTimeMillis();
			long duration = (endTime - startTime) / 1000;
			try {
				System.out.println("\n*** Training Complete! Time taken: " + duration + "s *** \n");
				Thread.sleep(2000); // Sleep for 50 milliseconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println( "**** Training Results ****" );

// Test each trained neural network one by one
			for (NeuralNetwork nnTraining : trainedNetworks) {
				double fitnessTraining = Fitness.evaluate(nnTraining);
				totalFitnessTraining += fitnessTraining;
				System.out.println(++number + " Train:" + fitnessTraining);

			}


			try {
				System.out.println( "\n**** Testing Results  ****" );
				number = 0;
				Thread.sleep(200); // Sleep for 50 milliseconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (NeuralNetwork nnTraining : trainedNetworks) {

//				double fitnessTraining = Fitness.evaluate(nnTraining);
//				totalFitnessTraining += fitnessTraining;
//				System.out.println(++number + " Training:" + fitnessTraining);
//
//				try {
//					Thread.sleep(150); // Sleep for 50 milliseconds
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				NeuralNetwork nnTest = new ExampleEvolutionaryAlgorithm();
//				double[] weights = nnTraining.getWeights();
//				nnTest.setWeights(weights);
//				nnTest.best = new Individual();
//				nnTest.best.chromosome = weights;
				Parameters.setDataSet(DataSet.Test);
				double fitnessTest = Fitness.evaluate(nnTraining);
				totalFitnessTest += fitnessTest;
				System.out.println(++number + " Test:" + fitnessTest);
			}

			try {
				System.out.println( "\n**** Average Results  ****" );

				number = 0;
				Thread.sleep(500); // Sleep for 50 milliseconds
			} catch (InterruptedException e) {
				e.printStackTrace();
			}


			double averageFitnessTraining = totalFitnessTraining / numThreads;
			double averageFitnessTest = totalFitnessTest / numThreads;
			System.out.println("Average Fitness on Training: " + averageFitnessTraining);
			System.out.println("Average Fitness on Test: " + averageFitnessTest);


		} else if (control.equals("original")) { // original NoGuiCode
			/*
			 * Set the parameters here or directly in the Parameters Class.
			 * Note you should use a maximum of 20,0000 evaluations for your experiments
			 */
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
		}
		else if (control.equals("file")){
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
			NeuralNetwork nn2 = NeuralNetwork.loadNeuralNetwork("1681569472546-20.txt");
			Parameters.setDataSet(DataSet.Test);
			double fitness2 = Fitness.evaluate(nn2);
			System.out.println("Fitness on " + Parameters.getDataSet() + " " + fitness2);
		}
	}





	}

