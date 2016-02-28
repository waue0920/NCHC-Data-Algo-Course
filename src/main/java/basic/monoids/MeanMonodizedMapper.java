package basic.monoids;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import edu.umd.cloud9.io.pair.PairOfLongInt;

/**
 * This is a mapper class for a monodic MapReduce algorithm.
 *
 * PairOfLongInt = Tuple2<Long, Integer>
 * PairOfLongInt.getLeftElement() returns Long
 * PairOfLongInt.getRightElement() returns Integer
 *
 *
 * @author Mahmoud Parsian
 *
 */
public class MeanMonodizedMapper
        extends Mapper<Object, Text, Text, PairOfLongInt> {

    // pairOfSumAndCount = (partial sum as long, count as int)
    private final static PairOfLongInt pairOfSumAndCount = new PairOfLongInt();
    private final static Text reduceKey = new Text();

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {
        String[] v = value.toString().split("\\s+");

        // create a monoid
        pairOfSumAndCount.set(Long.parseLong(v[1]), 1);
        reduceKey.set(v[0]);
        context.write(reduceKey, pairOfSumAndCount);
    }
}
