#!/bin/bash

if [ $# -lt 4 ] ; then
	echo "Usage: $0 <SIFT-DIR> <assign executable file> <kmeans model> <contingency table>"
	exit 1
fi

SRC=$1
ASSIGN=$2
MODEL=$3
TABLE=$4

TMP1=$TABLE.tmp1
TMP2=$TABLE.list

rm -f $TMP1
touch $TMP1

for FILE in `find $SRC -mindepth 1 -type f -name "*.sift"`; do
	echo $FILE >> $TMP1
done

TOTAL=`cat $TMP1 | wc -l`
echo total = $TOTAL
echo $TOTAL > $TMP2
cat $TMP1 | sort >> $TMP2
rm -f $TMP1

#Concatenation
echo Assigning ...

$ASSIGN $TMP2 $MODEL $TABLE


