package basic.PageRank;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class RankSortMapper extends Mapper<LongWritable, Text, FloatWritable, Text> {

    /**
     * The `map(...)` method is executed against each item in the input split. A key-value pair is
     * mapped to another, intermediate, key-value pair.
     *
     * Specifically, this method should take Text objects in the form:
     *      `"[page]    [finalPagerank]    outLinkA,outLinkB,outLinkC..."`
     * discard the outgoing links, parse the pagerank to a float and map each page to its rank.
     *
     * Note: The output from this Mapper will be sorted by the order of its keys.
     *
     * @param key the key associated with each item output from {@link uk.ac.ncl.cs.csc8101.hadoop.calculate.RankCalculateReducer RankCalculateReducer}
     * @param value the text value "[page]  [finalPagerank]   outLinkA,outLinkB,outLinkC..."
     * @param context Mapper context object, to which key-value pairs are written
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        // Get the page and split by tabs
        String valueStr = value.toString();
        String[] sections = valueStr.split("\\t");

        // Get the rank and the current page (ignoring the pages at the end)
        float rank = Float.parseFloat(sections[1]);
        String page = sections[0];

        // Output rank first
        context.write(new FloatWritable(rank), new Text(page));
    }

}