package basic.BayesClassifier;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 1403035 on 2016/3/21.
 */
public class NBCClassifyReducer extends Reducer<Text,IntWritable,Text,Text> {

    static List<String> classifications = new ArrayList<String>(){{add("Yes");add("No");}};
    private ProbabilityTable probabilityTable;


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        probabilityTable = new ProbabilityTable(context.getConfiguration(), "classifier.seq");
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {


        String[] attributes = key.toString().split(",");
        String selectedClass = null;
        double maxPosterior = 0.0;

        for(String aClass: classifications){
            double posterior = probabilityTable.getClassProbability(aClass);
            System.out.println(aClass + " Pro. = " + posterior);
            for(int i =0;i<attributes.length;i++){
                double pp =  probabilityTable.getConditionProbability(attributes[i],aClass);
                System.out.println(attributes[i] + " / " + aClass + " Prob. = " + pp );
                posterior *= pp;
                System.out.println(posterior);
            }

            if(selectedClass == null){
                selectedClass = aClass;
                maxPosterior = posterior;
            }else{
                if (posterior > maxPosterior){
                    selectedClass = aClass;
                    maxPosterior = posterior;
                }
            }
        }

        String reduceValue = selectedClass+","+maxPosterior;
        context.write(key, new Text(reduceValue));
    }
}
