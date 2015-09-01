require(ggplot2, quietly = TRUE)
require(plyr, quietly = TRUE)

plotArrival <- function(pathData, plotName){
  p <- ggplot(data=pathData)
  p <- p + geom_bar(aes(x=group, fill=group))

  p <- p + ylab("Num. of Steps") + xlab("Group") 
  p <- p + theme(legend.text = element_text(size=16), legend.title = element_text(size=16), text = element_text(size=16)) 
  p <- p + theme(legend.position = c(1, 1), legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  ggsave(plot=p,filename=paste(plotName, ".", pathData[1,'trial'],".pdf", sep=''), width=10, height=10)
}


files <- list.files('.', 'summary.RData', recursive=T)
runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
runtimes<-Reduce(function(x,y) merge (x,y, all=T), runtimeFrames)

runtimes <- runtimes[runtimes$runtime == 200000,]
ddply(runtimes, .(trial), function(x) plotArrival(x, plotName="timeouts"))