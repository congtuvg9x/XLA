#! /bin/bash

if [ $# -lt 1 ]
then
	echo "Usage: $0 <image dir>"
	exit 1
fi

IMAGE_DIR=$1

#BUOC 1: dir2sift.sh
echo 1. Compute sift...
./dir2sift.sh $IMAGE_DIR ../data/sift

#BUOC 2: sampling.sh
echo 2. Sampling...
./sampling.sh ../data/sift ../data/sampling

#BUOC 3: k-means
echo 3. k-means...
./kmeans -k 2000 -i 30 ../data/sampling ../data/kmeans

#BUOC 4 tao ma tran dong xuat hien
echo 4. create-table...
./create-table.sh ../data/sift ./assign ../data/kmeans ../data/F.txt


