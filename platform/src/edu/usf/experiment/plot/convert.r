files <- list.files('.', '*.csv', recursive=F)
runtimeFrames<-lapply(files,function(x) {
  data <- read.csv(x, sep='\t')
  nameNoExt <- strsplit(x, "\\.")[[1]][1]
  save(data, file=paste(nameNoExt, ".RData", sep=''))
  file.remove(x)
})