require(ggplot2, quietly = TRUE)
require(grid)
require(plyr, quietly = TRUE)
require(dunn.test, quietly = TRUE)
require(scales, quietly = TRUE)

#stepsPerSec <- 4

plotArrival <- function(pathData, plotName){
  #pathData <- pathData[pathData$runtime < 10000,]
  summarizedRunTimes <- ddply(pathData, .(group, repetition), summarise, sdRT = sd(runtime)/sqrt(length(runtime)), mRT = mean(runtime))
  #   print(head(summarizedRunTimes))
  summarizedRunTimes <- ddply(summarizedRunTimes, .(group), summarise, repetition=repetition, mRT=mRT, sdRT=sdRT, runmedian = runmed(mRT, 31))
  #   print(pathData[1,'trial'])
  p <- ggplot(pathData, aes(x = repetition, y = runtime, group = interaction(group, repetition), fill = group)) 
  #p <- p + geom_bar(data=summarizedRunTimes, mapping=aes(x=Group, y = mRT), stat='identity')
  p <- p + ylab("Completion time (num. actions)\n") + xlab("\nRepetition") 
  # p <- p + scale_fill_grey(name="Group",start = .4)
  p <- p + theme_bw() + scale_fill_discrete(guide = guide_legend(title = "Group"))
  p <- p + scale_y_continuous() + scale_x_continuous(breaks= pretty_breaks())
  p <- p + theme(legend.text = element_text(size=20), legend.title = element_text(size=20), text = element_text(size=20)) 
  p <- p + theme(legend.key.height = unit(3,"line"), legend.key.width = unit(3,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  box <- p + geom_boxplot(position="dodge",notch = true)# + geom_jitter()
  #box <- p + geom_point(aes(color=group), size = 5)
  print(box)
  ggsave(plot=box,filename=paste(plotName, "box.", pathData[1,'trial'],".pdf", sep=''), width=30, height=10)
  
  p <- ggplot(summarizedRunTimes, aes(x=group, y = mRT)) 
  p <- p + ylab("Completion time (s)") + xlab("Group") 
  p <- p + theme(legend.text = element_text(size=20), legend.title = element_text(size=20), text = element_text(size=20)) 
  p <- p + theme(legend.key.height = unit(3,"line"), legend.key.width = unit(3,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  p <- p + geom_bar(aes(y=mRT,fill=group),position=position_dodge(1), stat="identity")
  p <- p + geom_errorbar(aes(x=group, ymax=mRT+sdRT, ymin=mRT-sdRT, color=group, width = 0.25))
  p <- p + scale_fill_grey(name="Group")
  p <- p + scale_color_grey(name="Group")
  ggsave(plot=p,filename=paste(plotName, "bar.", pathData[1,'trial'],".pdf", sep=''), width=10, height=10)
}

# Plot runtimes per episode
files <- list.files('.', 'summary.RData', recursive=T)
runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
runtimes<-Reduce(function(x,y) merge (x,y, all=T), runtimeFrames)
save(runtimes, file="runtimes.RData")
load('runtimes.RData')
# runtimes <- runtimes[runtimes$runtime != 200000,]
ddply(runtimes, .(trial), function(x) plotArrival(x, plotName="runtimesEpisode"))
