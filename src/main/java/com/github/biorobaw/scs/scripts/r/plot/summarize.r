require(plyr, quietly = TRUE)

saveArrivalTime <- function(pathData){
  runTimes <- ddply(pathData, .(trial, group, subject, repetition), summarise, runTime = length(x))
  summarizedRunTimes <- ddply(runTimes, .(trial, group, repetition), summarise, runtime = mean(runTime))
  save(summarizedRunTimes, file='summary.RData')
  print (summarizedRunTimes)
}

load("subjposition.RData")
saveArrivalTime(data)


