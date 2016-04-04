require(ggplot2, quietly = TRUE)
require(plyr, quietly = TRUE)

plotArrival <- function(pathData, plotName){
  summarizedRunTimes <- ddply(pathData, .(trial, repetition), summarise, sdRT = sd(runtime)/sqrt(length(runtime)), mRT = mean(runtime))
  summarizedRunTimes <- ddply(summarizedRunTimes, .(trial), summarise, repetition=repetition, mRT=mRT, sdRT=sdRT, runmedian = runmed(mRT, 31))
  p <- ggplot(summarizedRunTimes, aes(x=repetition, y = mRT)) 
  p <- p + geom_errorbar(aes(ymin = mRT + sdRT, ymax=mRT - sdRT))
  p <- p + geom_line(aes(x=repetition, y=runmedian))
  p <- p + ylab("Num. of Steps") + xlab("Group") 
  p <- p + theme(legend.text = element_text(size=16), legend.title = element_text(size=16), text = element_text(size=16)) 
  p <- p + theme(legend.position = c(1, 1), legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  ggsave(plot=p,filename=paste(plotName, ".", pathData[1,'group'],".pdf", sep=''), width=10, height=10)
}

# Plot runtimes per episode
files <- list.files('.', 'summary.RData', recursive=T)
runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
runtimes<-Reduce(function(x,y) merge (x,y, all=T), runtimeFrames)
# names(runtimes)[2] <- "Group"
#levels(runtimes$Group) <- c("Multi-Scale (3 Layers)", "Small Scale (1 Layer)", "Medium Scale (1 Layer)", "Large Scale (1 Layer)")
ddply(runtimes, .(group), function(x) plotArrival(x, plotName="runtimes"))

 

