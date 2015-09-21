require(XML, quietly = TRUE)
require(shape, quietly = TRUE)
require(ggplot2, quietly = TRUE)
require(grid, quietly = TRUE)
require(plyr, quietly = TRUE)
require(akima)

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

plotValueOnMaze <- function (preName, name, valData, wallData, maze){
  # Get the individual components of the plot
  p <- ggplot()

  resolution <- 0.005 # you can increase the resolution by decreasing this number (warning: the resulting dataframe size increase very quickly)
  xo <- seq(min(valData$x),max(valData$x),by=resolution)
  yo <- seq(min(valData$y),max(valData$y),by=resolution)
  valDataInterp <- interp(x=valData$x, y=valData$y, z=valData$val, 
                          xo=xo, 
                          yo=yo, duplicate="mean")
  valDataInterpDf <- expand.grid(x=xo, y=yo)
  dim(valDataInterp$z) <- c(length(xo)*length(yo), 1)
  valDataInterpDf$val <- valDataInterp$z
  p <- p + geom_raster(data = valDataInterpDf, aes(x=x, y=y, fill=val)) #+ scale_fill_gradient(low="white", high="red")
  p <- p + scale_fill_gradient2(limits=c(-100,1000))
  p <- p + maze
  
  p <- wallPlot(wallData, p)
  
  segLen <- .1
  angle <- valData[1,'angle']
  arrowDF <- segLen * data.frame(x=-cos(angle), y=-sin(angle), xend=cos(angle), yend=sin(angle))
  p <- p + geom_segment(data=arrowDF, aes(x, y, xend=xend, yend=yend), size=1.5, arrow = arrow(length = unit(2,"cm"), type="closed"))

  # Some aesthetic stuff
  p <- mazePlotTheme(p)
  # Save the plot to an image
  if (name == '')
    print(p)  
  else
    ggsave(plot=p,filename=paste("plots/value/value",preName,name,
                                 ".png", sep=''), width=10, height=10)
  
}

valueFile <- 'value.RData'
wallsFile <- 'walls.RData'
mazeFile <- 'maze.xml'

invisible(dir.create("plots"))
invisible(dir.create("plots/value/"))
maze <- mazePlot(mazeFile)
load(valueFile)
valData <- data
load(wallsFile)
wallData <- data
splitValIntention <- split(valData, valData[c('trial', 'group', 'subject', 'repetition', 'intention')], drop=TRUE)

invisible(llply(
  names(splitValIntention), function(x){
    if (min(splitValIntention[[x]]$val) != 0 || max (splitValIntention[[x]]$val) != 0){
      splitValIntentionAngle <- split(splitValIntention[[x]], splitValIntention[[x]][c('trial', 'group', 'subject', 'repetition', 'intention', 'angle')], drop=TRUE)
      llply(names(splitValIntentionAngle), function(y){
        plotValueOnMaze(
          "", y, splitValIntentionAngle[[y]], wallData, maze)}, .parallel = FALSE)
      } 
    }, .parallel = FALSE)
) 