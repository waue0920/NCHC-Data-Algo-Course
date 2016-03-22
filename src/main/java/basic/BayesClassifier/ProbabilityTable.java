package basic.BayesClassifier;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1403035 on 2016/3/21.
 */
public class ProbabilityTable {
    private Map<String, Map<String, Double>> probabilityTable = new HashMap<String, Map<String, Double>>();

    public ProbabilityTable(Configuration conf, String path) throws IOException {

        Path p = new Path(path);
        SequenceFile.Reader reader = new SequenceFile.Reader(
                conf,  SequenceFile.Reader.file(p));

        Text key = (Text) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
        DoubleWritable value =  (DoubleWritable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
        while(reader.next(key, value))
        {
            String[] s = key.toString().split(",");
            Double d = new Double(value.get());
            if (!probabilityTable.containsKey(s[0])) {
                // feature not exist
                Map<String, Double> entry = new HashMap<String, Double>();
                entry.put(s[1],d);
                probabilityTable.put(s[0], entry);
            }else {
                Map<String, Double> entry = probabilityTable.get(s[0]);
                entry.put(s[1],d);
            }
        }
        reader.close();
    }

    private void showPT(){
        for(Map.Entry<String, Map<String, Double>> entry : probabilityTable.entrySet()){
            System.out.println(entry.getKey());
            System.out.println("===================");
            Map<String,Double> innerMap = entry.getValue();
            for(Map.Entry<String, Double> innerEntry:innerMap.entrySet()){
                System.out.println(innerEntry.getKey() + "/" + innerEntry.getValue());
            }

        }
    }

    public double getClassProbability(String aClass){
        return getConditionProbability("CLASS", aClass);
    }

    public double getConditionProbability(String feature, String aClass){
        if (probabilityTable.get(feature) == null)
            return 0.0;

        if (probabilityTable.get(feature).get(aClass) == null)
            return 0.0;

        return probabilityTable.get(feature).get(aClass).doubleValue();
    }

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        ProbabilityTable pt = new ProbabilityTable(conf, "classifier.seq");

        System.out.println("NO: " + pt.getClassProbability("No"));
        System.out.println("YES: " + pt.getClassProbability("Yes"));
        System.out.println("Cool,NO: " + pt.getConditionProbability("Cool", "No"));
        System.out.println("Cool,YES: " + pt.getConditionProbability("Cool", "Yes"));

        System.out.println("Cool,NO: " + pt.getConditionProbability("Cool", "No"));
        System.out.println("Cool,YES: " + pt.getConditionProbability("Cool", "Yes"));

        System.out.println("High,NO: " + pt.getConditionProbability("High","No"));
        System.out.println("High,YES: " + pt.getConditionProbability("High","Yes"));

        System.out.println("Hot,NO: " + pt.getConditionProbability("Hot","No"));
        System.out.println("Hot,YES: " + pt.getConditionProbability("Hot","Yes"));

        System.out.println("Mild,NO: " + pt.getConditionProbability("Mild","No"));
        System.out.println("Mild,YES: " + pt.getConditionProbability("Mild","Yes"));

        System.out.println("Normal,NO: " + pt.getConditionProbability("Normal","No"));
        System.out.println("Normal,YES: " + pt.getConditionProbability("Normal","Yes"));

        System.out.println("Overcast,NO: " + pt.getConditionProbability("Overcast","No"));
        System.out.println("Overcast,YES: " + pt.getConditionProbability("Overcast","Yes"));

        System.out.println("Rain,NO: " + pt.getConditionProbability("Rain","No"));
        System.out.println("Rain,YES: " + pt.getConditionProbability("Rain","Yes"));

        System.out.println("Sunny,NO: " + pt.getConditionProbability("Sunny","No"));
        System.out.println("Sunny,YES: " + pt.getConditionProbability("Sunny","Yes"));
    }
}
