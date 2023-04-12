setwd("C:/Users/Vincent/Desktop/SET10107Coursework2023/dataMining/1_random_75")
dataset <- read.csv("Random_75.csv")
library(ggplot2)

# Simple plots one by one easy to read

ggplot(dataset, aes(x = MaxGene, y = AverageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against MaxGene") + 
  xlab("MaxGene") + 
  ylab("AverageFitness")

ggplot(dataset, aes(x = PopSize, y = AverageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against PopSize") + 
  xlab("PopSize") + 
  ylab("AverageFitness")

ggplot(dataset, aes(x = TournamentSize, y = AverageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against TournamentSize") + 
  xlab("TournamentSize") + 
  ylab("AverageFitness")

ggplot(dataset, aes(x = PreserveElitePercentage, y = AverageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against PreserveElitePercentage") + 
  xlab("PreserveElitePercentage") + 
  ylab("AverageFitness")

ggplot(dataset, aes(x = InitialisationMethod, y = AverageFitness)) + 
  geom_boxplot() + 
  ggtitle("Average Fitness against InitialisationMethod") + 
  xlab("InitialisationMethod") + 
  ylab("AverageFitness")

ggplot(dataset, aes(x = MutateChangePopulation, y = AverageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against MutateChangePopulation") + 
  xlab("MutateChangePopulation") + 
  ylab("AverageFitness")

ggplot(dataset, aes(x = MaxMutationRate, y = AverageFitness)) + 
  geom_point() + 
  geom_smooth(method = "lm") + 
  ggtitle("Average Fitness against MaxMutationRate") + 
  xlab("MaxMutationRate") + 
  ylab("AverageFitness")



#3D plot, can be useful 
library(dplyr)
library(plotly)
dataset %>%
  plot_ly(x = ~MutateChangePopulation, y = ~MaxMutationRate, z = ~AverageFitness, 
          type = 'scatter3d', mode = 'markers', color = ~AverageFitness, 
          colors = colorRamp(c("red", "yellow", "green"))) %>%
  layout(scene = list(xaxis_title = "Mutate Change", 
                      yaxis_title = "Mutate Rate", 
                      zaxis_title = "Average Fitness"))







# All on one plot

# Load necessary libraries
library(ggplot2)
library(tidyr)
library(dplyr)

# Create a long format dataset for easier plotting
long_dataset <- dataset %>%
  gather(key = "Variable", value = "Value", -Iteration, -AverageFitness)

# Create a scatter plot matrix to explore relationships
scatter_plot_matrix <- ggplot(long_dataset, aes(x = Value, y = AverageFitness)) +
  geom_point(alpha = 0.5) +
  facet_wrap(~ Variable, scales = "free") +
  theme_bw() +
  labs(title = "Scatter Plot Matrix of AverageFitness vs. Other Variables",
       x = "Variable Value",
       y = "Average Fitness")
print(scatter_plot_matrix)

# Compute correlation coefficients
correlations <- cor(dataset[, -c(1, 7)])  # Exclude Iteration and InitialisationMethod (categorical)
avg_fitness_correlations <- correlations["AverageFitness", ]
avg_fitness_correlations <- sort(avg_fitness_correlations, decreasing = TRUE)
print(avg_fitness_correlations)



# Plots one by one








# Custom color palette
color_palette <- c("#128EFB", "#004D40", "orange")
filtered_dataset <- dataset[dataset$AverageFitness < 0.20,]

# Define custom theme with bigger text and visible plot background lines
my_theme <- theme_bw() + 
  theme(text = element_text(size = 14), 
        panel.grid.major = element_line(size = 0.5, linetype = "solid", color = "gray50"), 
        panel.grid.minor = element_line(size = 0.25, linetype = "solid", color = "gray50"))

# Create individual scatter plots with custom theme
plots <- lapply(colnames(dataset), function(variable) {
  plot <- ggplot(filtered_dataset, aes_string(y = variable, x = "AverageFitness", color = "InitialisationMethod", shape = "InitialisationMethod")) +
    geom_point(alpha = 1, size = 3) +
    geom_smooth(method = "lm", se = TRUE, linetype = "solid", level = 0.20, size = 1,
                aes(fill = InitialisationMethod), alpha = 0.00) +
    scale_color_manual(values = color_palette) +
    scale_fill_manual(values = color_palette) +
    my_theme +
    labs(title = paste("AverageFitness vs.", variable),
         y = variable,
         x = "Average Fitness")
  return(plot)
})

# Display plots one by one
for (plot in plots) {
  print(plot)
}
