package basic.BayesClassifier;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by 1403035 on 2016/3/21.
 */
public class NBCTrainMapper extends Mapper<Object, Text, Text, IntWritable> {
    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String[] tokens = value.toString().split(",");
        int classIndex = tokens.length -1;
        String theClass = tokens[classIndex];
        for(int i = 0 ; i < tokens.length-1 ; i++){
            String reduceKey = tokens[i]+","+theClass;
            context.write(new Text(reduceKey), new IntWritable(1));
        }

        context.write(new Text("CLASS,"+theClass), new IntWritable(1));
    }
}
