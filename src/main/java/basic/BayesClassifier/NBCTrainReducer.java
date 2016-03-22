package basic.BayesClassifier;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by 1403035 on 2016/3/21.
 */
public class NBCTrainReducer extends Reducer<Text,IntWritable,Text,IntWritable> {

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {
        int total = 0;
        IntWritable result = new IntWritable();
        for (IntWritable val : values) {
            total += val.get();
        }
        result.set(total);
        context.write(key, result);
    }
}
