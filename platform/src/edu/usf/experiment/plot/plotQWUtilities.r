library(gridExtra)
library(grid)
library(ggplot2)
library(lattice)


plotMaze <- function(  wallData){
  p <- ggplot(data = wallData) + geom_segment( aes(x,y,xend=xend,yend=yend),  col="black", cex=.2)
  p
  
}

plotPCs <- function(p,centers,colors, lowest, highest){
  #p <- p+ggplot(data = centers, aes(x = V1, y = V2))
  centers$Color = colors
  p <- p + geom_point(data = centers,aes(x=V1, y=V2 , color=Color), size=2,alpha=0.5)
  p <- p + scale_color_gradient2(limits = c(lowest, highest), mid = "orange", high = "red")
  p
  
}

plotWArrows <- function(p,centers,Wdata, thres){
  
  nextCell = apply(Wdata,1,which.max)
  maxWTable = apply(Wdata,1,max)
  
  numPCs = nrow(centers);
  starts = (1:numPCs)[maxWTable>thres]
  ends = nextCell[maxWTable>thres]
  
  p1 = centers[starts,]
  p2 = centers[ends,]
  p1$V3 = p2$V1
  p1$V4 = p2$V2

  p <- p + geom_segment(data=p1,aes(x=V1,y=V2,xend=V3, yend=V4), arrow = arrow(length = unit(0.2,"cm")))
  p
  
}

