require(ggplot2, quietly = TRUE)

stepsPerSec <- 4

# Plot runtimes per episode
files <- list.files('DelayedCueObs/', 'atefeeders.RData', recursive=T)
errorFrames<-lapply(files,function(x) {
  x <- paste('./DelayedCueObs/', x, sep='')
  load(x);
  # Get error in boolean form
  data$error <- data$error == "true"
  # Add cumulative error count
  data[data$error,'cummErrors'] <- 1:nrow(data[data$error,])
  data[data$error,]
})
errors<-Reduce(function(x,y) merge (x,y, all=T), errorFrames)
save(errors, file="errors.RData")


p <- ggplot(data = errors, aes(x=cycle/stepsPerSec, y = cummErrors, color=group))
p <- p + stat_smooth() + geom_point()
p <- p + theme_bw() + scale_color_grey(end=.6)

p


