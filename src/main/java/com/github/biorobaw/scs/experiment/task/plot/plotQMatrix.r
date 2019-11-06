
#args = commandArgs(trailingOnly=TRUE)
#if(length(args)>0){
#  nofood=as.numeric(args[1])
#  laps=as.numeric(args[2])
#  replay=as.numeric(args[3])
#  letter=args[4]
#  iteration=as.numeric(args[5])
#}


source("plotUtilities.r")
source("LoadBinary.r")
source("plotQWUtilities.r")


########### FILES  ##########################################

suffix = "AfterReplay"
QVmatrixFile = paste('QTable',suffix,'.bin',sep="")
WmatrixFile = 'WTable.bin'


labFile = 'walls.RData'
feedersFile = 'feeders.RData'
centerFile  = 'cellCenters.txt'

saveQImage = gsub(".bin",".jpg",QVmatrixFile)
saveWImage = gsub(".bin",".jpg",WmatrixFile)



########### LOAD DATA #######################################


QVdata = readBinaryMatrix(QVmatrixFile);
Qdata = QVdata[,-9]   #Q "ish" matrix
Vdata = QVdata[,9]    #V matrix


Wdata = readBinarySparseMatrix(WmatrixFile)
for(i in 1:cells){ #make sure diagonal is zero
  Wdata[i,i]=0
}


centers = read.table(centerFile,sep=';')
centers = centers[,1:2]


load('walls.RData')
wallData <- data
splitWalls <- split(wallData, wallData[c('trial', 'group', 'subject', 'repetition')], drop=TRUE)


#save(maxQTable,maxWTable,nextCell,centers,lowestQ,highestQ,cells,file = processedFile)

########### PROCESS DATA ###################################

maxQdata = apply(Qdata,1,max)
maxVdata = max(Vdata)
maxWdata = apply(Wdata,1,max)


actions = ncol(Qdata)
cells = ncol(Wdata)



lowestQ = min(maxQdata) #lowest of max per row
highestQ = max(maxQdata)#highest of max per row

lowestW = min(maxWdata)
highestW = max(maxWdata)



########## CHOOSE PLOT PARAMS #############################

wMin = 0
wMax = 10
wThres = 0

qMin = lowestQ
qMax = highestQ
qThres = 0.01 

QcolorWeights = maxQdata
WcolorWeights = maxWdata


#threshold the weights
QcolorWeights[QcolorWeights<lowestQ]=lowestQ
QcolorWeights[QcolorWeights>highestQ]=highestQ

#threshold the weights
WcolorWeights[WcolorWeights<lowestW]=lowestW
WcolorWeights[WcolorWeights>highestW]=highestW



########## PLOT ###########################################

Wplot = plotMaze(splitWalls[[1]])
Wplot = plotPCs(Wplot , centers,WcolorWeights ,lowestW,highestW)
Wplot
Wplot = plotWArrows(Wplot,centers,Wdata,wThres)
Wplot

#Wplot <- Wplot + theme(panel.background = element_blank(), axis.text = element_blank(),
#                           axis.ticks = element_blank(), axis.title = element_blank())
Wplot


Qplot = plotMaze(splitWalls[[1]])
Qplot = plotPCs(Qplot , centers,QcolorWeights ,lowestQ,highestQ)
Qplot
Qplot = plotWArrows(Qplot,centers,Wdata,qThres)
Qplot

Qplot <- Qplot + theme(panel.background = element_blank(), axis.text = element_blank(),
                       axis.ticks = element_blank(), axis.title = element_blank())
Qplot






######## SAVE PLOT #######################################

ggsave(saveWImage,plot=Wplot,width=19.09,height=11.59)
ggsave(saveQImage,plot=Wplot,width=19.09,height=11.59)























