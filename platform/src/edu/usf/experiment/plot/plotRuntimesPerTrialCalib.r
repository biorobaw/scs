require(ggplot2, quietly = TRUE)
require(plyr, quietly = TRUE)

stepsPerSec <- 4

plotArrival <- function(pathData, plotName, xlabel){
  #pathData <- pathData[pathData$runtime < 10000,]
  pathData$runtime <- pathData$runtime / stepsPerSec
  summarizedRunTimes <- ddply(pathData, .(Group, repetition, experiment), summarise, sdRT = sd(runtime), mRT = mean(runtime))
  #   print(head(summarizedRunTimes))
  summarizedRunTimes <- ddply(summarizedRunTimes, .(Group, experiment), summarise, repetition=repetition, mRT=mRT, sdRT=sdRT, runmedian = runmed(mRT, 31))
  #   print(pathData[1,'trial'])
  p <- ggplot(pathData, aes(x=Group, y = runtime)) 
  #p <- p + geom_bar(data=summarizedRunTimes, mapping=aes(x=Group, y = mRT), stat='identity')
  p <- p + ylab("Completion Time (s)") + xlab(xlabel) 
  p <- p + theme(legend.text = element_text(size=16), legend.title = element_text(size=16), text = element_text(size=16))
  p <- p + theme(axis.text.x = element_blank(), axis.ticks.x = element_blank()) 
  p <- p + theme(legend.position = c(1, 1), legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  box <- p + geom_boxplot(aes(fill=Group),position=position_dodge(1), notch=TRUE)# + geom_jitter()
  box <- box + facet_grid(. ~ experiment)
  print(box)
  ggsave(plot=box,filename=paste(plotName, "box.", pathData[1,'trial'],".pdf", sep=''), width=10, height=6)
  
#   bar <- ggplot(summarizedRunTimes, aes(x=experiment, y = mRT)) 
#   bar <- bar + ylab("Completion Time (s)") + xlab("Mid. Layer Action Selection Contribution")  
#   bar <- bar + geom_bar(aes(y=mRT,fill=Group),position=position_dodge(1), stat="identity")
#   bar <- bar + geom_errorbar(aes(x=experiment, ymax=mRT+sdRT, ymin=mRT-sdRT, color=Group, width = 0.25))
#   bar <- bar + theme(legend.text = element_text(size=16), legend.title = element_text(size=16), text = element_text(size=16)) 
#   bar <- bar + theme(legend.position = c(1, 1), legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
#   ggsave(plot=bar,filename=paste(plotName, "bar.", pathData[1,'trial'],".pdf", sep=''), width=10, height=6)
}


dirs <- list.dirs (recursive=FALSE)
data <- ldply(dirs, function(dir){
  setwd(dir)
  # Plot runtimes per episode
  files <- list.files('.', 'summary.RData', recursive=T)
  runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
  library(data.table)
  runtimes<-rbindlist(runtimeFrames)
  names(runtimes)[2] <- "Group"
  #levels(runtimes$Group) <- c("Multi-Scale (3 Layers)", "Small Scale (1 Layer)", "Medium Scale (1 Layer)", "Large Scale (1 Layer)")
  #runtimes <- runtimes[runtimes$runtime != 200000,]
  runtimes$experiment <- dir
  setwd("..")
  runtimes
})
save(data,file="runtimes.RData")
# load("runtimes.RData")

xlabel <- commandArgs(TRUE)[1]

data$experiment <- as.numeric(apply(data, 1, function(x) { strsplit(x[6], "-")[[1]][2]}))
# names(data)[names(data) == 'experiment'] <- strsplit(dirs, "./|-")[[1]][2]
plotArrival(data[ data$trial == "DelayedCueObs",], plotName="runtimesDCObs", xlabel)
plotArrival(data[ data$trial == "DelayedCueNoObs",], plotName="runtimesDCNoObs", xlabel)
plotArrival(data[ data$trial == "Training",], plotName="runtimesTraining", xlabel)



