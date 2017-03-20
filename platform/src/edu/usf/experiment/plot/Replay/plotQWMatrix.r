
source("plotUtilities.r")
source("LoadBinary.r")
source("plotQWUtilities.r")


########### FILES  ##########################################

args = commandArgs(trailingOnly=TRUE)


WmatrixFile = args[1]


labFile = 'walls.RData'
feedersFile = 'feeders.RData'
centerFile  = 'cellCenters.txt'

folderStruct = strsplit(getwd(),'/')[[1]]
lenStruct = length(folderStruct)

subjectId = folderStruct[lenStruct]
group = folderStruct[lenStruct-1]
episode = folderStruct[lenStruct-2]
trial = folderStruct[lenStruct-3]

saveWImage = paste('../../../../plots/', gsub(".bin","",WmatrixFile),'/',group,'/',subjectId,'/',trial,sep="")
dir.create(saveWImage,recursive = TRUE,showWarnings = FALSE)
saveWImage = paste(saveWImage,'/',episode,'.jpg',sep="")


########### LOAD W DATA #####################################


Wdata = readBinarySparseMatrix(WmatrixFile)
for(i in 1:nrow(Wdata)){ #make sure diagonal is zero
  Wdata[i,i]=0
}


centers = read.table(centerFile,sep=';')
centers = centers[,1:2]


load('walls.RData')
wallData <- data
splitWalls <- split(wallData, wallData[c('trial', 'group', 'subject', 'repetition')], drop=TRUE)



#save(maxQTable,maxWTable,nextCell,centers,lowestQ,highestQ,cells,file = processedFile)

########### PROCESS W DATA ###################################


maxWdata = apply(Wdata,1,max)

lowestW = min(maxWdata)
highestW = max(maxWdata)



########## CHOOSE PLOT PARAMS #############################

wMax = 15
wMin = 0
wThres = 0

WcolorWeights = maxWdata

#threshold the weights
WcolorWeights[WcolorWeights<wMin]=wMin
WcolorWeights[WcolorWeights>wMax]=wMax



########## PLOT ###########################################



Wplot = plotMaze(splitWalls[[1]])
Wplot = plotWArrows(Wplot,centers,Wdata,wThres)
Wplot = plotPCs(Wplot , centers,WcolorWeights ,wMin,wMax)
Wplot <- Wplot + theme(panel.background = element_blank(), axis.text = element_blank(),
                           axis.ticks = element_blank(), axis.title = element_blank())
Wplot

######## SAVE PLOT #######################################


ggsave(saveWImage,plot=Wplot,width=19.09,height=11.59)




###### DO THE SAME FOR EACH Q MATRIX #####################


for(val in args[-1]){	
	
  QVmatrixFile = val
  saveQImage = paste('../../../../plots/', gsub(".bin","",QVmatrixFile),'/',group,'/',subjectId,'/',trial,sep="")
  dir.create(saveQImage,recursive = TRUE,showWarnings = FALSE)
  saveQImage = paste(saveQImage,'/',episode,'.jpg',sep="")
  
  QVdata = readBinaryMatrix(QVmatrixFile);
  Qdata = QVdata[,-9]   #Q "ish" matrix
  Vdata = QVdata[,9]    #V matrix
  
  maxQdata = apply(Qdata,1,max)
  maxVdata = max(Vdata)
  
  lowestQ = min(maxQdata) #lowest of max per row
  highestQ = max(maxQdata)#highest of max per row
  
  qMin = 0#lowestQ
  qMax = 50#highestQ
  qThres = 1 
  
  QcolorWeights = maxQdata
  
  #threshold the weights
  QcolorWeights[QcolorWeights<qMin]=qMin
  QcolorWeights[QcolorWeights>qMax]=qMax
  
  
  Qplot = plotMaze(splitWalls[[1]])
  Qplot = plotWArrows(Qplot,centers,Wdata,qThres)
  Qplot = plotPCs(Qplot , centers,QcolorWeights ,qMin,qMax)
  Qplot <- Qplot + theme(panel.background = element_blank(), axis.text = element_blank(),
                         axis.ticks = element_blank(), axis.title = element_blank())
  Qplot
  
  ggsave(saveQImage,plot=Qplot,width=19.09,height=11.59)
  
}























