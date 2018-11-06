#! /bin/bash

if [ $# -lt 2 ]; then
	echo "Usage: $0 <IMAGE_DIR> <OUTPUT_DIR> [STEM]"
	exit 1
fi

SRC_DIR=$1
DES_DIR=$2
STEM=$3
if [ -z "$STEM" ] ; then
	STEM=data
fi

DIR2SIFT=./dir2sift-region.sh
SAMPLING=./sampling-region.sh
KMEANS=./kmeans
ASSIGN=./assign
CA=./ca

# 1. Compute SIFT
echo 1. Compute SIFT
$DIR2SIFT $SRC_DIR $DES_DIR/SIFT

# 2. sample for k-means 
echo 2. Sampling ...
$SAMPLING $DES_DIR/SIFT $DES_DIR/$STEM.kmeans
# Also, we can use
# $SAMPLING $DES_DIR/SIFT $DES_DIR/$STEM.kmeans 100
# It will sample 100 images to create the data for k-means
# By default, we use 1/5 images to create visual words

# 3. run k-means
echo 3. Run k-means ...
$KMEANS -k 1000 -i 30 $DES_DIR/$STEM.kmeans $DES_DIR/$STEM.voc

# 4. run assign to create the contingency table
# 4.1 create list of *.sift-region
echo 4. Run assign ...
find $DES_DIR/SIFT -name "*.sift-region" | sort > $DES_DIR/$STEM.assgin.temp
cat $DES_DIR/$STEM.assgin.temp | wc -l > $DES_DIR/$STEM.assgin
cat $DES_DIR/$STEM.assgin.temp >> $DES_DIR/$STEM.assgin
rm -f $DES_DIR/$STEM.assgin.temp

$ASSIGN -t 1 -d $DES_DIR/$STEM.des $DES_DIR/$STEM.assgin $DES_DIR/$STEM.voc $DES_DIR/$STEM.ca

# 5. run CA
echo 5. Run CA ...
mkdir -p $DES_DIR/CA
$CA -k 100 $DES_DIR/$STEM.ca  $DES_DIR/CA/$STEM

# 6. create label file
find $SRC_DIR -name "*.jpg" | sort > $DES_DIR/$STEM.label.temp
sed 's/^/1 /' $DES_DIR/$STEM.label.temp > $DES_DIR/$STEM.label
rm -f $DES_DIR/$STEM.label.temp

# 7. write XML file
echo "<xml>" > $DES_DIR/$STEM.xml
echo "	<base href=\".\"/>" >> $DES_DIR/$STEM.xml
echo "	<file name=\"Z\" path=\"CA/${STEM}_Z.txt\"/>" >> $DES_DIR/$STEM.xml
echo "	<file name=\"W\" path=\"CA/${STEM}_W.txt\"/>" >> $DES_DIR/$STEM.xml
echo "	<file name=\"P\" path=\"CA/${STEM}_P.txt\"/>" >> $DES_DIR/$STEM.xml
echo "	<file name=\"Q\" path=\"CA/${STEM}_Q.txt\"/>" >> $DES_DIR/$STEM.xml
echo "	<file name=\"LAMBDA\" path=\"CA/${STEM}_Lambda.txt\"/>" >> $DES_DIR/$STEM.xml 
echo "	<file name=\"descriptor\" path=\"${STEM}.des\"/>" >> $DES_DIR/$STEM.xml
echo "	<file name=\"label\" path=\"${STEM}.label\"/>" >> $DES_DIR/$STEM.xml
echo "</xml>" >> $DES_DIR/$STEM.xml

