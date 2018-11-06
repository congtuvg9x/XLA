#!/bin/bash

#
# Author: PHAM Nguyen Khang
# Description: Compute sift descriptors for an image
# Returned files:
#       $SRC_DIR/*.$EXT => $DES_DIR/*.sift-region
# *.sift-region format
#-----------------------
# cols = 128
# rows = nb of sifts
# cx cy a b c bin_1 bin_2 ... bin_128
# cx cy a b c bin_1 bin_2 ... bin_128
# ..
#

function compute_sift {
	convert $1 $2.pgm
	$CMD -hesaff -sift -i $2.pgm -o1 $2 -thres 200
	if [ ! -f $2 ] ; then 
		echo 128 > $2
		echo 0  >> $2
	fi
	/bin/rm -f $2.pgm
}

function list_process {
	for i in `cat $1` ; do
		para=`echo $i | sed 's/:/ /'`
		compute_sift $para
	done
}

function recursive_process {
	mkdir -p $2
	for i in `find $1 -mindepth 1 -maxdepth 1 -type d -or -type f` ; do
		# get basename from i
		name=`basename $i`
		if [ -d $i ] ; then
			echo Processing directory \'$i\' ...
			recursive_process $i $2/$name 
		else
			image_name=`basename $name .$EXT`
			if [ "$image_name.$EXT" == $name ] ; then
				# compute sift-region for $name
				echo $i:$2/$image_name.sift-region >> $TEMP$(($total % $nb_proc))
				total=$(($total + 1))
			fi
		fi
	done
}

if [ $# -lt 2 ]
then
	echo "Usage: $0 <source dir> <destination dir> [extension] [extractor]"
	exit 1
fi

SRC_DIR=$1
DES_DIR=$2
EXT=jpg

if [ -n "$3" ] ; then
	EXT=$3
fi

if [ -n "$4" ] ; then
	CMD=$4
else
	WD=`echo $0 | sed -e '/\// ! d'  -e 's/\/[^\/][^\/]*$//'`
	if [ -n "$WD" ] ;then
		WD=$WD/
	fi
	CMD=${WD}extract_features.ln
fi

echo $CMD

nb_proc=`cat /proc/cpuinfo | grep processor | wc -l`
if [ $nb_proc -lt 1 ] ; then
        nb_proc=1
fi

#nb_proc=1

echo "#processor = $nb_proc"

TEMP=$DES_DIR.tmp


for ((i = 0; i < $nb_proc ; i++)) ; do
	rm -f $TEMP$i
done

total=0
recursive_process $SRC_DIR $DES_DIR

for ((i = 1; i < $nb_proc ; i++)) ; do
	list_process $TEMP$i &
done

list_process ${TEMP}0

wait

for ((i = 0; i < $nb_proc ; i++)) ; do
	rm -f $TEMP$i
done
echo Finish !
