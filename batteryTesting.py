#!/usr/bin/env python
import sys
import os

if len(sys.argv) == 1:
	print "ERROR: format should be:"
	print "\t./batteryTesting.py batfile1 ... batfilen"
	exit()



if __name__=='__main__':
	columnNames = []
	columnTypes = []
	columnValues = []
	combinationType = None
	
	obligatoryColumns = {'SOURCE_FOLDERS':-1,'MAIN_CLASS':-1,'EXPERIMENT_XML':-1,'LOG_FOLDER':-1,'GROUP':-1,'RAT_NUMBER':-1}
	
	optionalArguments = ['feederFile','nFoodStopCondition','feederOrder','startPosition','ratpath','enabledFeeders']
	possibleColumns = obligatoryColumns.keys() + optionalArguments
	
	readingColumn = None 
	
	def processFile(fileName):
		global columnNames,columnTypes,columnValues,combinationType,obligatoryColumns,optionalArguments,readingColumn
		f = open(fileName)
		i=0
		for l in f:
#			print 'line',l
#			print 'slit',l.split()
			line = l.split()
			i+=1
			if len(line)==0 or line[0][0]=='#':
				continue
			elif line[0] == 'constant' or line[0] == 'variable' or line[0] == 'calculated':
				if line[1] in columnNames:
					print "ERROR: Column redefinition in line ",i
					print " ",l
					exit()	
				columnNames += [line[1]]
				columnTypes += [line[0]]
				columnValues += [[]]
#				print obligatoryColumns.keys()
#				print line[1]
				if line[1] in obligatoryColumns.keys():
#					print 'is obligatory'
					obligatoryColumns[line[1]] = len(columnNames)-1
				readingColumn = len(columnNames)-1
			elif line[0] == 'combine':
				combinationType = line[1]
				readingColumn = None
			else:
				if readingColumn == None:
					print "ERROR in line ",i
					print " ",l
					print "No columns to insert the value"
					exit()
				if columnTypes[readingColumn] == "calculated":
					columnValues[readingColumn] = line
				else:
					if len(line)>1:
						print "ERROR in line ",i
						print " ",l
						print "Too many values or bad format"
						exit()
					columnValues[readingColumn] += line
			
			
		
		f.close()
		readingColumn = None
	
	totalEntries = 0 #counts number of rows in resulting table
	realColumns = 0  #counts number of columns in resulting table
	
	for i in range(1,len(sys.argv)):
		processFile(sys.argv[i])
		
		
	#do the joining and run each row
	
	#step one check all required columns are present:
	for k in obligatoryColumns.keys():
		if obligatoryColumns[k]==-1:
			print "ERROR: Missing obligatory column",k
			exit()
			
	#check if combinationType defined:
	if combinationType ==None:
		print "ERROR: combination type was not defined"
		exit()
		
	
			
	#check all constant and calculated columns have one value
	#check compatibility sizes of variable columns according to combinationType
	#calculate number of entries and columns in resulting table
	for i in range(0,len(columnNames)):
		if columnNames[i] in possibleColumns:
			realColumns+=1
		if columnTypes[i] == "constant" or columnTypes == "calculated":
			if len(columnValues[i])!= 1:
				print "ERROR: column ",columnNames[i]," should have exactly one value"
				print len(columnValues), " defined"
				print exit()
				
		if columnTypes[i] == "variable":
			if combinationType == "oneXone":
				if totalEntries == 0:
					totalEntries = len(columnValues[i])
				elif totalEntries!=len(columnValues[i]):
					print "ERROR: combination type oneXone defines but"
					print "columns have different amount of rows"
					print exit()
				
			elif combinationType == "allXall":
				if totalEntries == 0:
					totalEntries = len(columnValues[i])
				else:
					totalEntries *= len(columnValues[i])
			else:
				print "ERROR: the given combination type doesn't exist"
				exit()
			
	order = [obligatoryColumns['SOURCE_FOLDERS'],
		 obligatoryColumns['MAIN_CLASS'],
		 obligatoryColumns['EXPERIMENT_XML'],
		 obligatoryColumns['LOG_FOLDER'],
		 obligatoryColumns['GROUP'],
		 obligatoryColumns['RAT_NUMBER'],
		 ]
		 
	for i in range(0,len(columnNames)):
		if i not in order:
			order += [i]

	#create resulting table	
	print 'total entries:',totalEntries
	resultTable  = [['' for i in range(0,totalEntries)] for i in range(0,realColumns)]
	#first fill constant and variable columns:
	print 'o',order
	print 't',columnTypes
	print 'n',columnNames
	print 'resultTable columns: ',len(resultTable) #,len(resultTable[6])
	print 'r',realColumns
	cumulative = 1
	for col in order:
		if columnTypes[col] == "constant" or columnTypes[col] == 'variable':
			cantValues = len(columnValues[col])			
			for k in range(0,totalEntries):
				resultTable[col][k] = columnValues[col][(k / cumulative) % cantValues]
			cumulative = cumulative * cantValues
	
	def parseError(col):
		global columnNames
		print 'ERROR: could parse calculated column ',columnNames[col]
		exit()
	
	def createOperation(tokens,col):
		global columnNames,order
		nextToken = 0 #I leave it like this to allow future recurssion if needed
		if tokens[nextToken] == 'lenList':
			if len(tokens) < nextToken + 2 or tokens[nextToken+1] not in columnNames:
				parseError(col)
			colId = order[columnNames.index(tokens[nextToken+1])]
			def calculator(table,col,row):
				table[col][row] = len(resultTable[colId][row].split(','))
			nextToken +=2
		elif tokens[nextToken] == 'row':
			def calculator(table,col,row):
				table[col][row] = row
			nextToken +=1
		else:
			parseError(col)
		return calculator
			
				
	for col in order:
		if columnTypes[col] == 'calculated':
			calculator = createOperation(columnValues[col],col)
			for row in range(0,totalEntries):
				calculator(resultTable,col,row)
				
	#finally generate the table of execution commands and execute them
	commands = ['' for i in range(0,totalEntries)]
	for i in range(0,totalEntries):
		for j in range(0,len(obligatoryColumns)):
			if columnNames[order[j]] == 'LOG_FOLDER':
				commands[i] += 'logs/Experiment/bat'
			commands[i] += str(resultTable[order[j]][i]) + ' '
		for j in range(len(obligatoryColumns),len(order)):
			if columnNames[order[j]] in possibleColumns:
				commands[i] += columnNames[order[j]]+ ' '+ str(resultTable[order[j]][i]) + ' '
				
	executionCommand = "java -cp "
	for i in range(0,totalEntries):
		print 'Executing:'
		print executionCommand + commands[i]
		print 
		print
		os.system(executionCommand + commands[i])
		
					
	
	

