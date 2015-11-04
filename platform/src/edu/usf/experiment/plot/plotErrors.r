require(ggplot2, quietly = TRUE)

stepsPerSec <- 4

# Plot runtimes per episode
files <- list.files('DelayedCueObs/', 'atefeeders.RData', recursive=T)
feedingFrames<-lapply(files,function(x) {
  x <- paste('./DelayedCueObs/', x, sep='')
  load(x);
  # Get error in boolean form
  data$error <- data$error == "true"
  data$ate <- data$ate == "true"
  data$correct <- data$correct == "true"
  # Add cumulative error count
  if(nrow(data[data$error,])>0)
    data[data$error,'cummErrors'] <- 1:nrow(data[data$error,])
  else
    data[,'cummErrors'] <- NA
  # Add eat interval
  shiftedEatTimes <- c(0, data[data$correct,'cycle'])
  shiftedEatTimes <- shiftedEatTimes[1:(length(shiftedEatTimes)-1)]
  data[data$correct, 'timeToReach'] <- data[data$correct, 'cycle'] - shiftedEatTimes
  data[data$correct, 'rewardNum'] <- 1:nrow(data[data$correct,])
  data
})
feedingData<-Reduce(function(x,y) merge (x,y, all=T), feedingFrames)
save(feedingData, file="feedingData.RData")
load ("feedingData.RData")


p <- ggplot(data = feedingData[feedingData$error,], aes(x=cycle/stepsPerSec, y = cummErrors, color=group))
p <- p + stat_smooth() #+ geom_point()
p <- p + theme_bw() + scale_color_discrete(guide = guide_legend(title = "Group"))

p <- p + ylab("Cummulative Errors") + xlab("Time (s)")
p <- p + theme(legend.text = element_text(size=20), legend.title = element_text(size=20), text = element_text(size=20)) 
p <- p + theme(legend.key.height = unit(2,"line"), legend.key.width = unit(2,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
p
ggsave(filename = "errors.pdf", plot = p,width=10, height=6)

p <- ggplot(data = feedingData[feedingData$correct,], aes(x=rewardNum, y = timeToReach/stepsPerSec, color=group))
p <- p + stat_smooth() #+ geom_point()
p <- p + theme_bw() + scale_color_discrete(guide = guide_legend(title = "Group"))

p <- p + ylab("Time to reach enabled feeder") + xlab("Reward Number")
p <- p + theme(legend.text = element_text(size=20), legend.title = element_text(size=20), text = element_text(size=20)) 
p <- p + theme(legend.key.height = unit(2,"line"), legend.key.width = unit(2,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
p
ggsave(filename = "timeToReach.pdf", plot = p,width=10, height=6)



