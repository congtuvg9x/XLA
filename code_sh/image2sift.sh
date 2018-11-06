#! /bin/bash

#
# Author: PHAM Nguyen Khang
# Description: Compute sift descriptors for an image
# Result format:
# 	nb_sifts
# 	bin1 bin2 ... bin128
# 	bin1 bin2 ... bin128
# ...
#

if [ $# -lt 2 ]
then
	echo "Usage: $0 <image file> <sift file> [sift extractor]"
	echo "Example: $0 image.jpg image.sift extract_feature.ln"
	exit 1
fi

IMAGE_FILE=$1
SIFT_FILE=$2

if [ ! -f $IMAGE_FILE ] ; then
	exit 1
fi
if [ "$3" != "" ] ; then
	CMD=$3
else
	WD=`echo $0 | sed -e '/\// ! d'  -e 's/\/[^\/][^\/]*$//'`
	if [ "$WD" != "" ] ;then
		WD=$WD/
	fi
	CMD=${WD}extract_features.ln
fi
echo $CMD

convert $IMAGE_FILE $SIFT_FILE.png
$CMD -hesaff -sift -i $SIFT_FILE.png -o1 $SIFT_FILE.des -thres 500
cat $SIFT_FILE.des | cut -d" " -f 6-133 > $SIFT_FILE

#for -o2
#cat $SIFT_FILE.des | cut -d" " -f 14-141 > $SIFT_FILE

/bin/rm -f $SIFT_FILE.png $SIFT_FILE.des
	
exit 0

