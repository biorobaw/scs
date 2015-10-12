for (i in seq(8,20)){
  cat(paste("univ.addWall(", data[i,'x'], "f,", data[i,'y'], "f,",
            data[i,'xend'], "f,", data[i,'yend'], "f);\n", sep=""))
}
