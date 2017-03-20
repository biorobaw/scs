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

mazePlot <- function(feedersData, wantedFeeder = -1){
	feeders <- apply(feedersData,1, function (feeder) {
				r <- .01
				x <- as.numeric(feeder[4])
				y <- as.numeric(feeder[5])
				id <- as.numeric(feeder[3])
				enabled <- feeder[6] == "true"
				dat <- circleFun(c(x,y),2*r,npoints = 100, 0, 2, TRUE)
				if (enabled){
					p <- geom_polygon(data=dat, aes(x,y), color="orange", fill="orange")
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
	p + geom_segment(data=pathSegs[c('x','y','nX','nY','random')], aes(x,y,xend=nX,yend=nY,color = random), cex=.05) + scale_color_manual(values=c(true="red", false="blue")) 
}

ratPathPointsPlot <- function(pathData, p){
	p + geom_point(data=pathData, aes(x,y),  col="green", bg="red",cex=.1)
}

ratStartPointPlot <- function (pathData, p){
	p + geom_point(data=head(pathData, n=1), aes(x,y), col="red", bg="red",cex=1)
}

ratEndPointPlot <- function (pathData, p){
	p + geom_point(data=tail(pathData, n=1),aes(x,y), col="green", bg="green", cex=1)
}

stopPointsPlot <- function (pathData) {
	interval <- 21
	pathDataNext <- pathData[c(interval:nrow(pathData),seq(1,interval-1)),]
	stopPoints <- pathData[pathData$x == pathDataNext$x & pathData$y == pathDataNext$y,]
	if (nrow(stopPoints)>0)
		geom_point(data=stopPoints, aes(x,y), col="green", bg="green", cex=1)
	else
		list()
}

atePointsPlot <- function (pathData) {
	pathData$ate <- as.logical(pathData$ate)
	atePoints <- pathData[pathData$ate,]
	if (nrow(atePoints)>0)
		atePlot <- geom_point(data=atePoints, aes(x,y), col="green", bg="green", cex=1)
	else
		list()
}

triedToEatPointsPlot <- function (pathData) {
	pathData$ate <- as.logical(pathData$ate)
	pathData$triedToEat <- as.logical(pathData$triedToEat)
	triedToEatPoints <- pathData[pathData$triedToEat & !pathData$ate,]
	if (nrow(triedToEatPoints)>0)
		atePlot <- geom_point(data=triedToEatPoints, aes(x,y), col="red", bg="red", cex=1)
	else
		list()
}



wallPlot <- function(wallData,p){
	
	if(!is.null(wallData)){
		if (nrow(wallData) > 0){
			p <- p + geom_segment(data=wallData, aes(x,y,xend=xend,yend=yend),  col="black", cex=.2)
		}
	}
	p
}

plotPathOnMaze <- function (preName, name, pathData, wallData, maze){
	# Get the individual components of the plot
	p <- ggplot()
	p <- ratPathPlot(pathData, p)
	#  p <- ratPathPointsPlot(pathData, p)
	p <- ratStartPointPlot(pathData, p)
	p <- ratEndPointPlot(pathData, p)
	
	#p <- p + stopPointsPlot(pathData)
	
	p <- p + atePointsPlot(pathData)
	p <- p + triedToEatPointsPlot(pathData)
	
	p <- wallPlot(wallData, p)
	p <- p + maze
	
	# Some aesthetic stuff
	p <- mazePlotTheme(p)
	#   list(p, paste("plots/path",name,".jpg", sep=''))
	#   pdf(paste("plots/path",name,".pdf", sep=''))
	#   print(p)
	#   dev.off()
	# Save the plot to an image
	if (name == '')
		print(p)  
	else
		ggsave(plot=p,filename=paste("plots/path/path",preName,name,
						".pdf", sep=''), width=3, height=3)
	
	#   saveRDS(p, paste("plots/path/",name,".obj", sep=''))
}

pathFile <- 'subjposition.RData'
wallsFile <- 'walls.RData'
feedersFile <- 'feeders.RData'

invisible(dir.create("plots",showWarnings = FALSE))
invisible(dir.create("plots/path/",showWarnings = FALSE))

load(pathFile)
pathData <- data
load(wallsFile)
wallData <- data
if (file.exists(feedersFile)) {
	load(feedersFile)
	feedersData <- data
	maze <- mazePlot(feedersData)
} else {
	maze <- list()
}



splitPath <- split(pathData, pathData[c('trial', 'group', 'subject', 'repetition')], drop=TRUE)
splitWalls <- split(wallData, wallData[c('trial', 'group', 'subject', 'repetition')], drop=TRUE)
invisible(llply(
				names(splitPath), function(x) plotPathOnMaze(
							"", x, splitPath[[x]], splitWalls[[x]], maze), .parallel = FALSE))