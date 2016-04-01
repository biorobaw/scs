#!/usr/local/bin/Rscript

load("installedpackages.rda")

for (count in 1:length(installedpackages)) install.packages(installedpackages[count])

