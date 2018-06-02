
#Travel/num/Control/1/WTable.txt

source('LoadBinary.r')

threshold = 1*0:10
nofood = 0
laps = 100

args = commandArgs(trailingOnly=TRUE)
if(length(args)>0){
  nofood=as.numeric(args[1])
}

folder = paste("MultipleTNoFood",nofood,"Laps100Replay0Thres1",sep="")
folderOriginal = folder
folder = paste(folder,"/",folder,sep="")
saveFileName = paste('PercentualReplay',nofood,'.jpg',sep="")

percentualDataFile = paste(folderOriginal,'/percentual.RData',sep="")

if(!file.exists(percentualDataFile)){
  
  cant = nofood+laps
  maxId = cant-1
  letters = c('A','B','C','D','E','F','G','H','I','J')
  mydata = readBinarySparseMatrix(paste(folder,"A/Travel/0/Control/1/WTable.bin",sep=""))
  columns = ncol(mydata)
  maxConnection = array(0,c(cant,columns))
  counts = array(0,c(length(threshold),cant))
  lastwd = getwd()
  
  
  for (l in letters){
    
  
    
    for(id in 0:maxId){
      cat(id)
      cat('\n')
      mydata = readBinarySparseMatrix(paste(folder,l,"/Travel/",id,"/Control/1/WTable.bin",sep=""))
      cat('done data\n')
      maxConnection[id+1,] = apply(mydata,1,max)
        
    }
    
  
    for(i in 1:length(threshold)){
      counts[i,] = counts[i,] + apply(maxConnection>threshold[i],1,sum)
      
    }
    
  }
  
  percentual = t(counts)/(length(letters)*columns)
  
  percentual = as.data.frame(percentual)
  percentual$x = (-nofood):99
  save(percentual,file=percentualDataFile)
} else {
  load(percentualDataFile)
  
}

library(ggplot2)
library(reshape2)

percentual.melted = melt(percentual,id="x")
p = ggplot(data=percentual.melted,aes(x=x,y=value,color=variable))
p = p + geom_point()

ggsave(saveFileName, plot=p)


