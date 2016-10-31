require(gtools)
dist <- function(from, to){
  sqrt((from$x - to$x)^2 + (from$y - to$y)^2)
}

path_len <- function(firstPos, feeders){
  from <- firstPos
  length <- 0
  for (i in 1:nrow(feeders)){
    to <- feeders[i,]
    length <- length + dist(from, to)
    from <- to
  }
  
  length
}


feederFile <- "feeders.RData"
posFile <- "subjposition.RData"

load(feederFile)
feeders <- data
load(posFile)
pos <- data

firstPos <- pos[1,c("x","y")]


perms <- permutations(n = nrow(feeders), r = nrow(feeders))
feeders[perms[[1]],]
dists <- apply(perms, 1, function(x) path_len(firstPos, feeders[x,]))

scaledOpt <- min(dists) / .1

timeProRated <- nrow(pos) / scaledOpt
save(timeProRated, file="timeProRated.RData")
