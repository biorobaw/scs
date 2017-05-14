#!/bin/sh

# Group name - e.g. Control
group=$1
# Individual number - e.g. 1
individual=$2
# Trial name - e.g. Training
trial=$3
# Plot name, including folder inside plots, only initial part of filename - e.g. value/value
plot=$4

key="${plot}$trial.$group.$individual.*.pdf"

echo Making gif for $key 

mkdir movie

for f in $(find . -iname $key -not -path "./movie/*"); do
	gs -q -o movie/$(filename -r $f).png -sDEVICE=pngalpha -dLastPage=1 -r72 $f 
done

cd movie

#find . -iname "*.pdf" -exec convert {} {}.png \;

# Make the gif
input="$plot$trial.$group.$individual.%d.png"
output="$plot.$trial.$group.$individual.gif"

palette="/tmp/palette.png"

filters="fps=15,scale=320:-1:flags=lanczos"

ffmpeg -v warning -i $input -vf "$filters,palettegen" -y $palette
ffmpeg -v warning -i $input -i $palette -lavfi "$filters [x]; [x][1:v] paletteuse" -y $output

cd ..

cp movie/$output .

rm -r movie
