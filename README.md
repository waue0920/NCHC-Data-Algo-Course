# how to use

 `git clone https://github.com/waue0920/NCHC-Data-Algo-Course`

 `cd NCHC-Data-Algo-Course/`

 `mvn clean package`

 `hadoop fs -mkdir -p /user/hadoop`

 `hadoop fs -put ~/hadoop/README.txt ./`

 `hadoop jar target/data-alg-1.0-SNAPSHOT.jar basic.WordCount.WordCountDriver ./*.txt ./output`

 `hadoop fs -cat ./output/part-r-00000`
