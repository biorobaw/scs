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
  if(nrow(data[data$error,])>0)
    data[data$error,'cummErrors'] <- 1:nrow(data[data$error,])
  else
    data[,'cummErrors'] <- NA
  data[data$error,]
})
errors<-Reduce(function(x,y) merge (x,y, all=T), errorFrames)
save(errors, file="errors.RData")


p <- ggplot(data = errors, aes(x=cycle/stepsPerSec, y = cummErrors, color=group))
p <- p + stat_smooth() #+ geom_point()
p <- p + theme_bw() + scale_color_discrete(guide = guide_legend(title = "Group"))

p <- p + ylab("Cummulative Errors") + xlab("Time (s)")
p <- p + theme(legend.text = element_text(size=20), legend.title = element_text(size=20), text = element_text(size=20)) 
p <- p + theme(legend.key.height = unit(2,"line"), legend.key.width = unit(2,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
p
ggsave(filename = "errors.pdf", plot = p,width=10, height=6)


