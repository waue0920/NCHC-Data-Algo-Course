package basic.BayesClassifier;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by 1403035 on 2016/3/21.
 */
public class NBCClassifyMapper  extends Mapper<Object, Text, Text, IntWritable> {
    @Override
    protected void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        context.write(value, new IntWritable(1));
    }
}
