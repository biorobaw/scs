
readBinaryMatrix = function(filename){
  
  binMatrix = file(filename,"rb")
  rowCols = readBin(binMatrix,"integer",2,endian = "big");
  
  data = readBin(binMatrix,"double",rowCols[1]*rowCols[2],size=4,endian = "big")
  data = as.data.frame(array(data,rowCols))
  
  
  close(binMatrix)
  
  return(data)
  
}




readBinarySparseMatrix= function(filename){
  
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







