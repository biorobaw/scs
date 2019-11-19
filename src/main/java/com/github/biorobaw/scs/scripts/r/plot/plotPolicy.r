require(XML, quietly = TRUE)
require(shape, quietly = TRUE)
require(ggplot2, quietly = TRUE)
require(grid, quietly = TRUE)
require(plyr, quietly = TRUE)
require(parallel, quietly = TRUE)
require(doParallel, quietly = TRUE)
require(animation, quietly = TRUE)

mazePlotTheme <- function(p){
  p + theme(axis.line=element_blank(),axis.text.x=element_blank(),
            axis.text.y=element_blank(),axis.ticks=element_blank(),
            axis.title.x=element_blank(),
            axis.title.y=element_blank(),legend.position="none",
            panel.background=element_blank(),panel.border=element_blank(),panel.grid.major=element_blank(),
            panel.grid.minor=element_blank(),plot.background=element_blank())
}

# Define the circle; add a point at the center if the 'pie slice' if the shape is to be filled
# taken from stackoverflow http://stackoverflow.com/questions/12794596/how-fill-part-of-a-circle-using-ggplot2
circleFun <- function(center=c(0,0), diameter=1, npoints=100, start=0, end=2, filled=TRUE){
  tt <- seq(start*pi, end*pi, length.out=npoints)
  df <- data.frame(
    x = center[1] + diameter / 2 * cos(tt),
    y = center[2] + diameter / 2 * sin(tt)
  )
  if(filled==TRUE) { #add a point at the center so the whole 'pie slice' is filled
    df <- rbind(df, center)
  }
  return(df)
}

mazePlot <- function(mazeFile, wantedFeeder = -1){
  # Same as xmlParse()
  doc <- xmlParseDoc(mazeFile)
  root <- xmlRoot(doc)
  floor <-  xmlToList(getNodeSet(doc, "/world//floor")[[1]])
  r <- as.numeric(floor$r)
  x <- as.numeric(floor$x)
  y <- as.numeric(floor$y)
  
  ns <- getNodeSet(doc, "/world//feeder")
  feeders <- llply(ns, function (f) {
    feeder <- xmlToList(f)
    r <- as.numeric(feeder$r)
    x <- as.numeric(feeder$x)
    y <- as.numeric(feeder$y)
    dat <- circleFun(c(x,y),2*r,npoints = 100, 0, 2, TRUE)
    if (wantedFeeder == as.numeric(feeder$id) ){
      p <- geom_polygon(data=dat, aes(x,y), color="green", fill="green")
    } else {
      p <- geom_polygon(data=dat, aes(x,y), color="grey", fill="grey")
    } 
  })
  
  feeders
}

ratPathPlot <- function(pathData, p){
  pathSegs <- pathData[1:nrow(pathData)-1,]
  # Add two new columns with shifted data
  pathSegs[c('nX', 'nY')] <- pathData[-1,c('x','y')]
  p + geom_segment(data=pathSegs[c('x','y','nX','nY','random')], aes(x,y,xend=nX,yend=nY,color = random)) + scale_color_manual(values=c(true="red", false="blue")) 
}

ratPathPointsPlot <- function(pathData, p){
  p + geom_point(data=pathData, aes(x,y),  col="green", bg="red",cex=1)
}

ratStartPointPlot <- function (pathData, p){
  p + geom_point(data=head(pathData, n=1), aes(x,y), col="green", bg="green",cex=4)
}

ratEndPointPlot <- function (pathData, p){
  p + geom_point(data=tail(pathData, n=1),aes(x,y), col="blue", bg="blue", cex=4)
}

wallPlot <- function(wallData,p){
  
  if (!is.null(wallData)){
    
    p + geom_segment(data=wallData, aes(x,y,xend=xend,yend=yend),  col="black", cex=2)
  } else {
    p
  }
}

policyArrowsPlot <- function(policyData, p){
  # Compute deltax and y
  #policyDataNonNA <- policyData[!is.na(policyData['heading']),]
  
  # Only keep those greater than 1% of max
  policyDataSignificant <- policyData
  
  if (nrow(policyDataSignificant) > 0){
    segLen = .04
    policyDataSignificant[, 'deltax'] <- cos(policyDataSignificant['heading']) * segLen
    policyDataSignificant[, 'deltay'] <- sin(policyDataSignificant['heading']) * segLen
    p <- p + geom_segment(data=policyDataSignificant, aes(x = x, y = y, xend = x + deltax, yend = y + deltay, colour=log(val+1)), size=1.5, arrow = arrow(length = unit(0.3,"cm"), type="closed"))
    p <- p + scale_colour_gradient2(low = "white", mid = "blue", high="red", midpoint=log(10000)/2)
    #     p <- p + geom_segment(data=policyDataSignificant, aes(x = x, y = y, xend = x + deltax, yend = y + deltay))
  } else {
    p
  }
}

policyDotsPlot <- function(policyData, p){
  #policyDataNA <- policyData[is.na(policyData['heading']),]
  
  # Only keep those greater than 1% of max
  policyDataNonSignificant <- policyData[policyData$val <= max(policyData$val) / 100,]
  if (nrow(policyDataNonSignificant) > 0){
    p + geom_point(data=policyDataNonSignificant,aes(x,y), col="black", bg="black", cex=2)
  } else {
    p
  }
}

plotPolicyOnMaze <- function(name, policyData, wallData, maze){      
  p <- ggplot()
  p <- p + maze
  p <- wallPlot(wallData, p)
  #p <- ratPathPlot(pathData, p)
  #  p <- ratPathPointsPlot(pathData, p)
  #p <- ratStartPointPlot(pathData, p)
  #p <- ratEndPointPlot(pathData, p)
  p <- policyArrowsPlot(policyData, p)
  #p <- policyDotsPlot(policyData, p)
  
  # Some aesthetic stuff
  p <- mazePlotTheme(p)
  # Save the plot to an image
  
  
  ggsave(plot=p,filename=paste("plots/policy/",name,
                               ".pdf", sep=''), width=10, height=10)
  #   saveRDS(p, paste("plots/policy/",name,".obj", sep=''))
}

policyFile <- 'policy.RData'
wallsFile <- 'walls.RData'
mazeFile <- 'maze.xml'

invisible(dir.create("plots"))
invisible(dir.create("plots/policy/"))
maze <- mazePlot(mazeFile)
load(policyFile)
policyData <- data
load(wallsFile)
wallData <- data
splitWalls <- split(wallData, wallData[c('trial', 'group', 'subject', 'repetition')], drop=TRUE)
splitPol <- split(policyData, policyData[c('trial', 'group', 'subject', 'repetition')], drop=TRUE)
llply(names(splitPol), function(x){
  # Split data by layers and intention
  splitPolLayer <- split(splitPol[[x]], splitPol[[x]][c('intention')], drop=TRUE)
  # Plot different layers with same path data
  lapply(names(splitPolLayer), function (y) plotPolicyOnMaze(paste(x,y,sep='.'),
                                                             splitPolLayer[[y]],
                                                             splitWalls[[x]],
                                                             maze))
})
