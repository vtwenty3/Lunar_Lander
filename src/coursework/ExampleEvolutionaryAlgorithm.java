package coursework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
		population = initialise();

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
			
			// Implemented in NN class. 
			outputStats();
			
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

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) {
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	private Individual select() {
		// Use Parameters.tournamentSize instead of hardcoding the value
		Individual best = null;

//		// Calculate the elite threshold using Parameters.elitePercentage
//		int eliteThreshold = (int) (Parameters.elitePercentage * Parameters.popSize);
//		List<Individual> eliteIndividuals = population.stream()
//				.sorted(Comparator.comparingDouble(individual -> -individual.fitness))
//				.limit(eliteThreshold)
//				.collect(Collectors.toList());
//
//		// If there are elite individuals, select a random elite individual and return its copy
//		if (!eliteIndividuals.isEmpty()) {
//			return eliteIndividuals.get(Parameters.random.nextInt(eliteIndividuals.size())).copy();
//		}

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
	private void mutate(ArrayList<Individual> individuals) {		

		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) {
				if (Parameters.random.nextDouble() < Parameters.mutateRate) {
					if (Parameters.random.nextBoolean()) {
						individual.chromosome[i] += (Parameters.mutateChange);
					} else {
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}
	}

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
			population.set(replaceIndex, individuals.get(i));
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

//	@Override
//	public double activationFunction(double x) {
//		if (x < -20.0) {
//			return -1.0;
//		} else if (x > 20.0) {
//			return 1.0;
//		}
//		return Math.tanh(x);
//	}

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