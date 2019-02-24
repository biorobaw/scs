import numpy as np
import pandas as pd

def dataFrame(colname,values):
  return pd.DataFrame({colname:values,'key':0})
  
def one2one(df1,df2):
  return pd.concat([df1,df2[df2.columns.difference(df1.columns)]],axis=1)

def allXall(df1,df2):
  return df1.merge(df2,on='key')
  
def createConfigColumn(df):
  df['config']= 'c' + df.index.map(str)
   
  
def saveResult(df,fileName):
  df.drop(['key'],axis=1).to_csv(fileName,sep='\t',index=False)
  
numRatsPerConfig = 3
episodesPerStartingLocation = 200
runLevel = 1

outputFile = 'configSet1NoRats.csv'
outputFileNoRats = 'configSet1NoRats.csv'

runLevel = dataFrame('runLevel',[runLevel])
experiment = dataFrame('experiment',[ './multiscalemodel/src/edu/usf/ratsim/model/pablo/multiscale_memory/experiments/experiment.xml'])
group = dataFrame('group',['Control'])

ratIds = dataFrame('subName',range(numRatsPerConfig))

mazes = dataFrame('mazeFile',['multiscalemodel/src/edu/usf/ratsim/model/pablo/multiscale_memory/mazes/M0.xml',
            'multiscalemodel/src/edu/usf/ratsim/model/pablo/multiscale_memory/mazes/M1.xml'
            ])
numLocations = dataFrame('numStartingPositions',[2,2])

pcSizes = dataFrame('pcSizes',["0.12",
                              "0.04,0.08,0.12"
                              ])
                              
numPCx = dataFrame('numPCx',["9",
                             "25,13,9"])

##############################################################
#generate table
                             
#combine mazes and numLocations one2one
partial = one2one(mazes,numLocations)

#calculate num of episodes for each maze: locations*episodes per location
partial['numEpisodes'] = partial['numStartingPositions']*episodesPerStartingLocation


partial = allXall(group,partial)
partial = allXall(experiment,partial)
partial = allXall(runLevel,partial)

partial = allXall(partial,pcSizes)
partial = allXall(partial,numPCx)

createConfigColumn(partial)

saveResult(partial,outputFileNoRats)

final = allXall(partial,ratIds);

saveResult(final,outputFile)




            







