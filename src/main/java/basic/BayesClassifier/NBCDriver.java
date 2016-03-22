package basic.BayesClassifier;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Created by 1403035 on 2016/3/21.
 */
public class NBCDriver {
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.set("mapreduce.job.queuename", "root.MR");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (otherArgs.length != 4) {
            System.err.println("Usage: <input> <output>  ");
            System.exit(2);
        }

        Job trainJob = Job.getInstance(conf, "Training NBC");
        trainJob.setJarByClass(NBCDriver.class);
        trainJob.setMapperClass(NBCTrainMapper.class);
        trainJob.setReducerClass(NBCTrainReducer.class);
        trainJob.setNumReduceTasks(1);
        trainJob.setOutputKeyClass(Text.class);
        trainJob.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(trainJob, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(trainJob, new Path(otherArgs[1]));
        trainJob.waitForCompletion(true);

        buildProp(otherArgs, conf);

        Job classifyJob = Job.getInstance(conf, "Classify");
        classifyJob.setJarByClass(NBCDriver.class);
        classifyJob.setMapperClass(NBCClassifyMapper.class);
        classifyJob.setReducerClass(NBCClassifyReducer.class);
        classifyJob.setNumReduceTasks(1);
        classifyJob.setMapOutputKeyClass(Text.class);
        classifyJob.setMapOutputValueClass(IntWritable.class);
        classifyJob.setOutputKeyClass(Text.class);
        classifyJob.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(classifyJob, new Path(otherArgs[2]));
        FileOutputFormat.setOutputPath(classifyJob, new Path(otherArgs[3]));
        classifyJob.waitForCompletion(true);

    }

    private static void buildProp(String[] otherArgs, Configuration conf) throws IOException {
        Path path = new Path("classifier.seq");
        SequenceFile.Writer writer =
                SequenceFile.createWriter(path.getFileSystem(conf), conf, path,
                        new Text().getClass(), new DoubleWritable().getClass());
        Map<String, Double> countMap =new HashMap<String, Double>();
        double trainingDataSize = 14;
        Path pt=new Path(otherArgs[1]+"/part-r-00000");
        FileSystem fs = FileSystem.get(conf);
        BufferedReader br;
        String line;
        br =new BufferedReader(new InputStreamReader(fs.open(pt)));
        while ((line=br.readLine()) != null){
            String[] tokens = line.split("\\t");
            countMap.put(tokens[0],Double.parseDouble(tokens[1]));
        }
        br.close();
        br =new BufferedReader(new InputStreamReader(fs.open(pt)));
        while ((line=br.readLine()) != null){
            String[] tokens = line.split("\\t");
            String[] key_compoments = tokens[0].split(",");
            int count = Integer.parseInt(tokens[1]);
            String classification = key_compoments[1];
            if(key_compoments[0].equals("CLASS")){
                writer.append(new Text(tokens[0]), new DoubleWritable(count / trainingDataSize));
            }else{
                Double count1 = countMap.get("CLASS,"+classification);
                if(count1 == null){
                    writer.append(new Text(tokens[0]), new DoubleWritable(0.0));
                }else{
                    writer.append(new Text(tokens[0]), new DoubleWritable(count/count1));
                }
            }
        }
        br.close();
        writer.close();
    }
}
