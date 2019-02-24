import pandas as pd
import os
import sys

#get console input
baseFolder = sys.argv[1]
file = sys.argv[2]

#load the configs and get the names of the configs
configs =pd.read_csv(file,sep='\t')
configFolders = pd.unique(configs['config'])

#create function for creating folder paths:
def makedirs(dir):
  try:  
      os.makedirs(dir)
  except OSError:  
      return False
  else:  
      return True

#create base folder and the rest of the logfolders
makedirs(baseFolder)
for folder in  configFolders:
  makedirs(baseFolder +"/" + folder)

#copy config folder to base folder
import shutil
shutil.copy2(file,baseFolder + '/configs.csv')