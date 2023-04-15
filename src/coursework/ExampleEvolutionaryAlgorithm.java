package coursework;


import java.util.*;
import java.util.stream.Collectors;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {
	

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights

		if (Parameters.initialistionMethod.equalsIgnoreCase("sobol")) {
//			population = initialiseSobol();
		}else if (Parameters.initialistionMethod.equalsIgnoreCase("lhs")){
		population = initialiseLHS();
		}
		else{ //random
			population = initialiseRandom();
		}

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			Individual parent1 = select(); 
			Individual parent2 = select();


			// Generate a child by crossover. Not Implemented			
			ArrayList<Individual> children = reproduce(parent1, parent2);			
			
			//mutate the offspring
			mutate(children);
			
			// Evaluate the children
			evaluateIndividuals(children);			

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();

			Individual candidate = mutateBest(best);
			if(candidate.fitness < best.fitness) {
				best = candidate;
				updateBestInPopulation(candidate);
			}


//
//			for(int i = 0; i < 5; i++) {
//				Individual candidate = mutateBest(best);
//				if(candidate.fitness < best.fitness) {
//					best = candidate;
//					updateBestInPopulation(candidate);
//				}
//			}


			// Implemented in NN class. 
			outputStats();
			//double diversity = calculatePopulationDiversity();
			//System.out.println("Population Diversity: " + diversity);
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}



	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) {
			if (best == null) {
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	private void updateBestInPopulation(Individual candidate) {
		int bestIndex = -1;
		double bestFitness = Double.MAX_VALUE;

		for (int i = 0; i < population.size(); i++) {
			if (population.get(i).fitness < bestFitness) {
				bestFitness = population.get(i).fitness;
				bestIndex = i;
			}
		}

		if (bestIndex >= 0) {
			population.set(bestIndex, candidate);
		}
	}


	private Individual mutateBest(Individual best) {
		Individual candidate = best.copy();
		double currentMutationRate = adaptiveMutationRate();
		for (int i = 0; i < candidate.chromosome.length; i++) {
			if (Parameters.random.nextDouble() < currentMutationRate) {
				if (Parameters.random.nextBoolean()) {
					candidate.chromosome[i] += (Parameters.mutateChangeBest);
				} else {
					candidate.chromosome[i] -= (Parameters.mutateChangeBest);
				}
			}
		}
		Fitness.evaluate(candidate, this);
		return candidate;
	}
	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialiseRandom() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	private ArrayList<Individual> initialiseLHS() {
		population = new ArrayList<>();
		int numIntervals = Parameters.popSize;
		double intervalSize = 1.0 / numIntervals;
		double minGeneValue = Parameters.minGene; // Set the minimum gene value for your problem
		double maxGeneValue = Parameters.maxGene;// Set the maximum gene value for your problem
		double geneRange = maxGeneValue - minGeneValue;

		double[][] lhsMatrix = new double[Parameters.getNumGenes()][Parameters.popSize];

		// Generate LHS matrix
		for (int i = 0; i < Parameters.getNumGenes(); i++) {
			for (int j = 0; j < Parameters.popSize; j++) {
				double randomValue = Math.random() * intervalSize;
				lhsMatrix[i][j] = minGeneValue + (j * intervalSize) + randomValue;
			}
			// Shuffle each row of the LHS matrix
			Collections.shuffle(Arrays.asList(lhsMatrix[i]));
		}

		// Create the initial population using the LHS matrix
		for (int i = 0; i < Parameters.popSize; i++) {
			Individual individual = new Individual();
			for (int j = 0; j < Parameters.getNumGenes(); j++) {
				individual.chromosome[j] = lhsMatrix[j][i];
			}
			population.add(individual);
		}

		evaluateIndividuals(population);
		return population;
	}
//
//	private ArrayList<Individual> initialiseSobol() {
//		population = new ArrayList<>();
//		SobolSequenceGenerator sobol = new SobolSequenceGenerator(Parameters.getNumGenes());
//		double geneRange = Parameters.maxGene - Parameters.minGene;
//
//		for (int i = 0; i < Parameters.popSize; ++i) {
//			Individual individual = new Individual();
//			double[] point = sobol.nextVector();
//			for (int j = 0; j < Parameters.getNumGenes(); ++j) {
//				individual.chromosome[j] = Parameters.minGene + (point[j] * geneRange);
//			}
//			population.add(individual);
//		}
//		evaluateIndividuals(population);
//		return population;
//	}




	private Individual select() {
		// Use Parameters.tournamentSize instead of hardcoding the value
		Individual best = null;
		// perform tournament selection
		for (int i = 0; i < Parameters.tournamentSize; i++) {
			Individual individual = population.get(Parameters.random.nextInt(Parameters.popSize));
			if (best == null || individual.fitness < best.fitness) {
				best = individual;
			}
		}

		// Return a copy of the best-selected individual
		return best.copy();
	}

	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();

		// Perform crossover based on the selected method
		if (Parameters.crossoverMethod.equalsIgnoreCase("onePoint")) {
			// One-point crossover
			int crossoverPoint = Parameters.random.nextInt(parent1.chromosome.length);

			// Copy genes from parents to children
			for (int i = 0; i < parent1.chromosome.length; i++) {
				if (i < crossoverPoint) {
					child1.chromosome[i] = parent1.chromosome[i];
					child2.chromosome[i] = parent2.chromosome[i];
				} else {
					child1.chromosome[i] = parent2.chromosome[i];
					child2.chromosome[i] = parent1.chromosome[i];
				}
			}
		} else if (Parameters.crossoverMethod.equalsIgnoreCase("twoPoint")) {
			// Two-point crossover
			int crossoverPoint1 = Parameters.random.nextInt(parent1.chromosome.length);
			int crossoverPoint2 = Parameters.random.nextInt(parent1.chromosome.length);

			// Ensure crossoverPoint1 <= crossoverPoint2
			if (crossoverPoint1 > crossoverPoint2) {
				int temp = crossoverPoint1;
				crossoverPoint1 = crossoverPoint2;
				crossoverPoint2 = temp;
			}

			// Copy genes from parents to children
			for (int i = 0; i < parent1.chromosome.length; i++) {
				if (i < crossoverPoint1 || i >= crossoverPoint2) {
					child1.chromosome[i] = parent1.chromosome[i];
					child2.chromosome[i] = parent2.chromosome[i];
				} else {
					child1.chromosome[i] = parent2.chromosome[i];
					child2.chromosome[i] = parent1.chromosome[i];
				}
			}
		}

		// Add the children to the list
		children.add(child1);
		children.add(child2);

		// Return the list of children
		return children;
	}
	/**
	 * Mutation
	 * 
	 * 
	 */

	private double calculatePopulationDiversity() {
		// Calculate the average fitness of the population
		double averageFitness = population.stream().mapToDouble(individual -> individual.fitness).average().orElse(0.0);
		double diversity = 0.0;
		// Calculate the diversity of the population
		for (Individual individual : population) {
			double distance = 0.0;
			for (int i = 0; i < individual.chromosome.length; i++) {
				distance += Math.abs(individual.chromosome[i] - averageFitness);
			}
			diversity += distance;
		}

		// Return the average diversity of the population
		return diversity / population.size();
	}


	private double adaptiveMutationRate() {
		double diversity = calculatePopulationDiversity();
		// Calculate the adaptive mutation rate based on the diversity
		return Parameters.minMutationRate + (Parameters.maxMutationRate - Parameters.minMutationRate) * (diversity / Parameters.maxDiversity);
	}

	private void mutate(ArrayList<Individual> individuals) {
		// Calculate the adaptive mutation rate
		double currentMutationRate = adaptiveMutationRate();

		for (Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < currentMutationRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChangePopulation);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChangePopulation);
					}
				}
			}
		}
	}



	//old mutation
//	private void mutate(ArrayList<Individual> individuals) {
//
//		for(Individual individual : individuals) {
//			for (int i = 0; i < individual.chromosome.length; i++) {
//				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
//					if (Parameters.random.nextBoolean()) {
//						individual.chromosome[i] += (Parameters.mutateChange);
//					} else {
//						individual.chromosome[i] -= (Parameters.mutateChange);
//					}
//				}
//			}
//		}
//	}



	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		int eliteCount = (int) (Parameters.popSize * Parameters.preserveElitePercentage);
		Collections.sort(population, (ind1, ind2) -> Double.compare(ind1.fitness, ind2.fitness));
		// Preserve the top eliteCount
		for (int i = 0; i < eliteCount; i++) {
			individuals.add(population.get(i));
		}
		// Replace the non-elite individuals with the new children
		for (int i = 0; i < individuals.size(); i++) {
			int replaceIndex = eliteCount + i;
			if (replaceIndex < population.size()) { // Ensure replaceIndex is within bounds
				population.set(replaceIndex, individuals.get(i));
			} else {
				break; // Exit the loop if replaceIndex is out of bounds
			}
		}
	}
	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}





//SELU
public double activationFunction(double x) {

	if (Parameters.activationFunction.equalsIgnoreCase("SELU")) {

		double alpha = 1.67326;
		double scale = 1.0507;
		if (x <= 0) {
			return scale * alpha * (Math.exp(x) - 1);
		} else {
			return scale * x;
		}
	} else if (Parameters.activationFunction.equalsIgnoreCase("TANH")) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	} else {
		return 0;
	}
}
}