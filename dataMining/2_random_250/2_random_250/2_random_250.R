# Load necessary libraries
library(ggplot2)
library(dplyr)

#install.packages("reshape2")

# Read dataset
dataset <- read.csv("random_search_250.csv")

# Filter out outliers, if necessary (e.g., very high fitness values)
# dataset <- dataset %>% filter(averageFitness < threshold)

# Create scatter plots
# AverageFitness vs. PopSize
pop_size_plot <- ggplot(dataset, aes(x = PopSize, y = averageFitness, color = InitialisationMethod)) +
  geom_point(aes(shape = activationFunction), alpha = 0.8, size = 3) +
  theme_minimal() +
  labs(title = "Average Fitness vs. Population Size",
       x = "Population Size",
       y = "Average Fitness")
print(pop_size_plot)

# AverageFitness vs. MaxMutationRate
mutation_rate_plot <- ggplot(dataset, aes(x = MaxMutationRate, y = averageFitness, color = InitialisationMethod)) +
  geom_point(aes(shape = activationFunction), alpha = 0.8, size = 3) +
  theme_minimal() +
  labs(title = "Average Fitness vs. Max Mutation Rate",
       x = "Max Mutation Rate",
       y = "Average Fitness")
print(mutation_rate_plot)

# AverageFitness vs. maxDiversity
max_diversity_plot <- ggplot(dataset, aes(x = maxDiversity, y = averageFitness, color = InitialisationMethod)) +
  geom_point(aes(shape = activationFunction), alpha = 0.8, size = 3) +
  theme_minimal() +
  labs(title = "Average Fitness vs. Max Diversity",
       x = "Max Diversity",
       y = "Average Fitness")
print(max_diversity_plot)


ggplot(dataset, aes(x = MaxGene, y = averageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against MaxGene") + 
  xlab("MaxGene") + 
  ylab("AverageFitness")


# Custom color palette
color_palette <- c("#128EFB", "#004D40", "#D81B60")
filtered_dataset <- dataset[dataset$averageFitness < 0.15,]

# Create individual scatter plots
plots <- lapply(colnames(filtered_dataset), function(variable) {
  plot <- ggplot(filtered_dataset, aes_string(y = variable, x = "averageFitness", color ="InitialisationMethod", shape = "InitialisationMethod")) +
    geom_point(alpha = 1, size = 5.3) +
    geom_smooth(method = "lm", se = TRUE, linetype = "solid", level = 0.20, size = 1,
                aes(fill = InitialisationMethod), alpha = 0.05) +
    scale_color_manual(values = color_palette) +
    scale_fill_manual(values = color_palette) +
    theme_bw() +
    labs(title = paste("AverageFitness vs.", variable),
         y = variable,
         x = "Average Fitness")
  facet_grid(. ~ activationFunction)  # Add facet_grid here

  return(plot)
})

# Display plots one by one
for (plot in plots) {
  print(plot)
}


# Load necessary libraries
library(ggplot2)


# Custom color palette
color_palette <- c("#128EFB", "#004D40", "#D81B60")

# Filter dataset to show only average fitness below 0.15
filtered_dataset <- dataset[dataset$averageFitness < 0.15,]

# Select relevant columns
relevant_columns <- c("MaxGene", "PopSize", "TournamentSize", "PreserveElitePercentage", "MutateChangePopulation", "MaxMutationRate")

# Melt the dataset for ggplot2
library(reshape2)
melted_dataset <- melt(filtered_dataset, id.vars = c("Iteration", "InitialisationMethod", "activationFunction", "averageFitness"), measure.vars = relevant_columns)

# Create the plot
plot <- ggplot(melted_dataset, aes(x = averageFitness, y = value, color = InitialisationMethod, shape = InitialisationMethod)) +
  geom_point(alpha = 1, size = 5.3) +
  geom_smooth(method = "lm", se = TRUE, linetype = "solid", level = 0.20, size = 1,
              aes(fill = InitialisationMethod), alpha = 0.05) +
  scale_color_manual(values = color_palette) +
  scale_fill_manual(values = color_palette) +
  theme_bw() +
  labs(x = "Average Fitness", y = "Value") +
  facet_grid(variable ~ activationFunction)  # Facet grid by activation function

# Display the plot
print(plot)







# Load necessary libraries
library(ggplot2)

# Read dataset

# Custom color palette
color_palette <- c("#128EFB", "orange", "#D81B60")
my_theme <- theme_bw() + 
  theme(text = element_text(size = 14), 
        panel.grid.major = element_line(size = 0.5, linetype = "dashed", color = "gray60"), 
        panel.grid.minor = element_line(size = 0.25, linetype = "dashed", color = "gray60"))
# Filter dataset to show only average fitness below 0.15
filtered_dataset <- dataset[dataset$averageFitness < 0.20,]

# Select relevant columns
relevant_columns <- c("MaxGene", "PopSize", "TournamentSize", "PreserveElitePercentage", "MutateChangePopulation", "MaxMutationRate")

# Melt the dataset for ggplot2
library(reshape2)
melted_dataset <- melt(filtered_dataset, id.vars = c("Iteration", "InitialisationMethod", "activationFunction", "averageFitness"), measure.vars = relevant_columns)

# Create a list of plots
plots <- lapply(relevant_columns, function(variable) {
  plot <- ggplot(melted_dataset[melted_dataset$variable == variable,], aes(x = averageFitness, y = value, color = activationFunction, shape = activationFunction)) +
    geom_point(alpha = 1, size = 3.3) +
    geom_smooth(method = "lm", se = TRUE, linetype = "solid", level = 0.20, size = 1,
                aes(fill = InitialisationMethod), alpha = 0.05) +
    scale_color_manual(values = color_palette) +
    scale_fill_manual(values = color_palette) +
    my_theme +
    labs(title = paste("Average Fitness vs.", variable),
         x = "Average Fitness", y = "Value") +
    facet_grid(rows = vars(InitialisationMethod))  # Facet grid by activation function
  return(plot)
})

# Display plots one by one
for (plot in plots) {
  print(plot)
}
