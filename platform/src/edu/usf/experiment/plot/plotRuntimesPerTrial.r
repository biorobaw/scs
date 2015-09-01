require(ggplot2, quietly = TRUE)
require(plyr, quietly = TRUE)

plotArrival <- function(pathData, plotName){
  #pathData <- pathData[pathData$runtime < 10000,]
  summarizedRunTimes <- ddply(pathData, .(group, repetition), summarise, sdRT = sd(runtime)/sqrt(length(runtime)), mRT = mean(runtime))
  #   print(head(summarizedRunTimes))
  summarizedRunTimes <- ddply(summarizedRunTimes, .(group), summarise, repetition=repetition, mRT=mRT, sdRT=sdRT, runmedian = runmed(mRT, 31))
  #   print(pathData[1,'trial'])
  p <- ggplot(pathData, aes(x=group, y = runtime)) 
  #p <- p + geom_bar(data=summarizedRunTimes, mapping=aes(x=Group, y = mRT), stat='identity')
  p <- p + ylab("Num. of Steps") + xlab("Group") 
  p <- p + theme(legend.text = element_text(size=16), legend.title = element_text(size=16), text = element_text(size=16)) 
  p <- p + theme(legend.position = c(1, 1), legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  box <- p + geom_boxplot(aes(fill=group),position=position_dodge(1), notch=TRUE)# + geom_jitter()
  ggsave(plot=box,filename=paste(plotName, "box.", pathData[1,'trial'],".pdf", sep=''), width=10, height=10)
  
  bar <- ggplot(summarizedRunTimes, aes(x=group, y = mRT)) 
  bar <- bar + geom_bar(aes(y=mRT,fill=group),position=position_dodge(1), stat="identity")
  bar <- bar + geom_errorbar(aes(x=group, ymax=mRT+sdRT, ymin=mRT-sdRT, color=group, width = 0.25))
  ggsave(plot=bar,filename=paste(plotName, "bar.", pathData[1,'trial'],".pdf", sep=''), width=10, height=10)
}

# Plot runtimes per episode
files <- list.files('.', 'summary.RData', recursive=T)
runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
runtimes<-Reduce(function(x,y) merge (x,y, all=T), runtimeFrames)
# names(runtimes)[2] <- "Group"
#levels(runtimes$Group) <- c("Multi-Scale (3 Layers)", "Small Scale (1 Layer)", "Medium Scale (1 Layer)", "Large Scale (1 Layer)")
runtimes <- runtimes[runtimes$runtime != 200000,]
ddply(runtimes, .(trial), function(x) plotArrival(x, plotName="runtimes"))


