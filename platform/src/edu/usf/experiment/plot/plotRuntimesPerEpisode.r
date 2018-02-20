require(ggplot2, quietly = TRUE)
require(grid)
require(plyr, quietly = TRUE)
require(dunn.test, quietly = TRUE)
require(scales, quietly = TRUE)

maxColumns  <- 50

plotArrival <- function(pathData, plotName){
  pathData$group <- factor(pathData$group)
  
  # Subsample data
  numRepetitions <- max(pathData$repetition)
  columns <- min(numRepetitions, maxColumns)
  samples <- ceiling(0:(columns) * (numRepetitions / maxColumns))
  
  pathData <- pathData[pathData$repetition %in% samples,]
  p <- ggplot(pathData, aes(x = repetition, y = runtime, group = interaction(group, repetition), fill = group)) 
  p <- p + ylab("Completion time (num. actions)\n") + xlab("\nRepetition") 
  p <- p + theme_bw() + scale_fill_discrete(guide = guide_legend(title = "Group"))
  p <- p + scale_y_continuous() + scale_x_continuous(breaks= pretty_breaks())
  p <- p + theme(legend.text = element_text(size=20), legend.title = element_text(size=20), text = element_text(size=20)) 
  p <- p + theme(legend.key.height = unit(3,"line"), legend.key.width = unit(3,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  box <- p + geom_boxplot(position="dodge", notch = TRUE)# + geom_jitter()
  print(box)
  ggsave(plot=box,filename=paste(plotName, "box.", pathData[1,'trial'],".pdf", sep=''), width=30, height=10)

}

# Plot runtimes per episode
files <- list.files('.', 'summary.RData', recursive=T)
data <- list()
runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
library(data.table)
runtimes<-rbindlist(runtimeFrames)
save(runtimes, file="runtimes.RData")
load('runtimes.RData')
# runtimes <- runtimes[runtimes$runtime != 200000,]
ddply(runtimes, .(trial), function(x) plotArrival(x, plotName="runtimesEpisode"))

