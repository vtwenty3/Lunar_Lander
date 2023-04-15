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
public class StartNoGuiGrid {

    public static void main(String[] args) {
        String resultsFilename = "grid_search_results.csv";

        double[] maxMutationRates = {0.15, 0.25};
        double[] mutateChangePopulations = {0.3, 0.4};
        double[] preserveElitePercentages = {0.65, 0.45};
        int[] popSizes = {350, 900};
        double[] maxGenes = {0.1, 0.55, 0.7};


     /*   double[] maxMutationRates = {0.15};
        double[] mutateChangePopulations = {0.3};
        double[] preserveElitePercentages = {0.65};
        int[] popSizes = {350, 900};
        double[] maxGenes = {0.1, 0.7};*/

        // Prepare the results file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFilename))) {
            // Write the header
            writer.write("Iteration,MinGene,MaxGene,PopSize,TournamentSize,PreserveElitePercentage,MutateChangePopulation,MaxMutationRate,AverageFitness\n");

            int iter = 0;
            for (double maxMutationRate : maxMutationRates) {
                for (double mutateChangePopulation : mutateChangePopulations) {
                    for (double preserveElitePercentage : preserveElitePercentages) {
                        for (int popSize : popSizes) {
                            for (double maxGene : maxGenes) {
                                Parameters.maxGene = maxGene;
                                Parameters.minGene = -Parameters.maxGene;
                                Parameters.popSize = popSize;
                                Parameters.preserveElitePercentage = preserveElitePercentage;
                                Parameters.mutateChangePopulation = mutateChangePopulation;
                                Parameters.maxMutationRate = maxMutationRate;

                                int[] tournamentSizes = {(int) (Parameters.popSize / 1.7), (int) (Parameters.popSize / 2.44)};
                                for (int tournamentSize : tournamentSizes) {
                                    Parameters.tournamentSize = tournamentSize;
                                    iter++;
                                    writeResults(writer, iter);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeResults(BufferedWriter writer, int iter) throws IOException {
        double averageFitness = executeMainCode();
        writer.write(String.format("%d,%.4f,%.4f,%d,%d,%.4f,%.4f,%.4f,%.4f%n",
                iter,
                Parameters.minGene,
                Parameters.maxGene,
                Parameters.popSize,
                Parameters.tournamentSize,
                Parameters.preserveElitePercentage,
                Parameters.mutateChangePopulation,
                Parameters.maxMutationRate,
                averageFitness));
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
