package coursework;

import model.Fitness;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
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

public class StartNoGuiRandom {






// ...

    public static void main(String[] args) {
        Random rand = new Random();
        int iterations = 150;
        String resultsFilename = "random_finaleee.csv";

        // Prepare the results file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFilename))) {
            // Write the header
            writer.write("Iteration,MinGene,MaxGene,MutateChangePopulation,MaxMutationRate,AverageFitness\n");

            for (int iter = 0; iter < iterations; iter++) {
                Parameters.maxGene = rand.nextDouble() * (0.05 - 0.01) + 0.01;
                Parameters.minGene = -Parameters.maxGene;

                Parameters.mutateChangePopulation = rand.nextDouble() * (0.52 - 0.37) + 0.37;
                Parameters.maxMutationRate = rand.nextDouble() * (0.3 - 0.2) + 0.2;

                double averageFitness = executeMainCode();

                writer.write(String.format("%d,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                        iter + 1,
                        Parameters.minGene,
                        Parameters.maxGene,
                        Parameters.mutateChangePopulation,
                        Parameters.maxMutationRate,
                        averageFitness));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static double executeMainCode() {
        long startTime = System.currentTimeMillis();
        double totalFitness = 0.0;
        // Your existing code for executing the algorithm
        // ...
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
        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;
        System.out.println("Time taken (in seconds): " + duration);
        // Calculate average fitness
        return averageFitness;
    }
}

