require(ggplot2, quietly = TRUE)
require(grid)
require(plyr, quietly = TRUE)
require(reshape2)
#stepsPerSec <- 4

plotArrival <- function(pathData, plotName){
  #pathData <- pathData[pathData$runtime < 10000,]
  summarizedRunTimes <- ddply(pathData, .(group, repetition), summarise, sdRT = sd(runtime)/sqrt(length(runtime)), mRT = mean(runtime))
  #   print(head(summarizedRunTimes))
  summarizedRunTimes <- ddply(summarizedRunTimes, .(group), summarise, repetition=repetition, mRT=mRT, sdRT=sdRT, runmedian = runmed(mRT, 31))
  #   print(pathData[1,'trial'])
  pathData$repetition <- pathData$repetition + 1
  p <- ggplot(pathData, aes(x=factor(repetition), y = runtime)) 
  #p <- p + geom_bar(data=summarizedRunTimes, mapping=aes(x=Group, y = mRT), stat='identity')
  p <- p + ylab("Path Lenght (Ratio to Opt.)\n") + xlab("\nRepetition") 
  # p <- p + scale_fill_grey(name="Group",start = .4)
  p <- p + theme_bw() + scale_fill_discrete(h=c(250,250), guide=FALSE)
  p <- p + scale_y_continuous(limits = c(0,15), breaks = c(0,1,5,10,15))
  p <- p + scale_x_discrete(breaks = c(1,5,10,15,20))
  p <- p + theme(legend.text = element_blank(), legend.title = element_blank(), text = element_text(size=20)) 
  p <- p + theme(legend.key.height = unit(3,"line"), legend.key.width = unit(3,"line"), legend.position = "right", legend.justification = c(1, 1), legend.background = element_rect(colour = NA, fill = NA))
  box <- p + geom_hline(yintercept = 1, linetype="dashed")
  box <- box + geom_boxplot(aes(fill=group),position=position_dodge(.5), outlier.size = .1, )# + geom_jitter()
  box <- box
   #box <- p + geom_point(aes(color=group), size = 5)
  print(box)
  ggsave(plot=box,filename=paste(plotName, "box.", pathData[1,'trial'],".pdf", sep=''), width=10, height=6)
  
  p <- ggplot(pathData,aes(x=repetition, y=runtime, group=indiv, color = factor(indiv))) 
  p <- p + geom_line() + scale_colour_manual(values = rep("grey50", 64), guide = FALSE)
  print(p)
  ggsave(plot=p,filename=paste(plotName, "lines.", pathData[1,'trial'],".pdf", sep=''), width=10, height=10)
}

# Plot runtimes per episode
# files <- list.files('.', 'summary.RData', recursive=T)
# runtimeFrames<-lapply(files,function(x) {load(x); summarizedRunTimes['file'] <- x; summarizedRunTimes})
# runtimes<-Reduce(function(x,y) merge (x,y, all=T), runtimeFrames)
# indiv <- sapply(runtimes$file,function(x) {
#   sp <- strsplit(x, split = "/")
#   print(sp)[[1]][3]
# })
# runtimes$indiv <- as.numeric(indiv)  
# runtimes$repetition <- as.numeric(runtimes$repetition)
# save(runtimes, file="runtimes.RData")
load('runtimes.RData')
# runtimes <- runtimes[runtimes$runtime != 200000,]
ddply(runtimes, .(trial), function(x) plotArrival(x, plotName="runtimesEpisode"))

#runtimes$subject <- 
# runtimes <- runtimes[runtimes$indiv < 9,]
head(runtimes)
summary(lm(runtime ~ indiv * repetition, data = runtimes))

matrix <- acast(runtimes[,c('runtime', 'indiv','repetition')], indiv ~ repetition, value.var="runtime")

clusters <- kmeans(matrix[,15:20],2,iter.max=1000, nstart=100)
clusters$cluster

p <- ggplot(runtimes[runtimes$indiv %in% (which(clusters$cluster==1)-1),],aes(x=repetition, y=runtime, group=indiv, color = factor(indiv))) 
p <- p + geom_line() + scale_colour_manual(values = rep("grey50", 64), guide = FALSE)
print(p)

p <- ggplot(runtimes[runtimes$indiv %in% (which(clusters$cluster==2)-1),],aes(x=repetition, y=runtime, group=indiv, color = factor(indiv))) 
p <- p + geom_line() + scale_colour_manual(values = rep("grey50", 64), guide = FALSE)
print(p)

