#!/bin/sh

mkdir movie

find . -iname "*value*.pdf" -exec cp {} movie/ \;

cd movie

find . -iname "value*" -exec convert {} {}.png \;

# Make the gif
input="valueTraining.Symmetric.0.%d.pdf.png"
output="value.gif"

palette="/tmp/palette.png"

filters="fps=15,scale=320:-1:flags=lanczos"

ffmpeg -v warning -i $input -vf "$filters,palettegen" -y $palette
ffmpeg -v warning -i $input -i $palette -lavfi "$filters [x]; [x][1:v] paletteuse" -y $output

cd ..

cp movie/$output .

# rm -r movie
