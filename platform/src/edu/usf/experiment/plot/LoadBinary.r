
readBinaryMatrix = function(filename){
	
	binMatrix = file(filename,"rb")
	rowCols = readBin(binMatrix,"integer",2,endian = "big");
	
	data = readBin(binMatrix,"double",rowCols[1]*rowCols[2],size=4,endian = "big")
	data = as.data.frame(t(array(data,c(rowCols[2],rowCols[1]))))
	
	
	close(binMatrix)
	
	return(data)
	
}




readBinarySparseMatrix= function(filename){
	
	fsize = file.size(filename);
	itemsToRead = (fsize-8)/4
	matrixElements = itemsToRead/3
	
	
	
	binMatrix = file(filename,"rb")
	rowCols = readBin(binMatrix,"integer",2,endian = "big");
	ij<-readBin(binMatrix,"integer",itemsToRead,endian = "big")
	close(binMatrix)
	
	
	data = array(0,rowCols[1]*rowCols[2])
	
	binMatrix = file(filename,"rb")
	readBin(binMatrix,"integer",2,endian = "big"); #discard first 2
	content<-readBin(binMatrix,"double",itemsToRead,size=4,endian = "big")
	close(binMatrix)
	
	
	mij = (1:matrixElements)*3
	j = ij[mij-1]
	i = ij[mij-2]
	
	
	indexes = (i*rowCols[2]+j)+1 
	data[indexes] = content[mij]
	
	
	result = as.data.frame(matrix(data,nrow=rowCols[1],byrow = TRUE))
	
	return(result)
	
}


readBinarySparseMatrixOLD= function(filename){
	
	binMatrix = file(filename,"rb")
	rowCols = readBin(binMatrix,"integer",2,endian = "big");
	
	data = as.data.frame(array(0,rowCols))
	
	while(length(ij<-readBin(binMatrix,"integer",2,endian = "big"))>0){
		val = readBin(binMatrix,"double",1,size=4,endian = "big")
		data[ij[1]+1,ij[2]+1] = val #note in java arrays are 0 based, in r, they are 1 based
	}
	
	close(binMatrix)
	return(data)
	
}



#require(tictoc)
# f = 'WTable.bin'
# 
# 
# tic()
# b = readBinarySparseMatrixOLD(f)
# toc()
# 
# tic()
# c = readBinarySparseMatrix(f)
# toc()
# 
# sum(b==c)
