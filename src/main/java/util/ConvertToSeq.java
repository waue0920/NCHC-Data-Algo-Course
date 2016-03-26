package util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by ogre0403 on 2016/3/26.
 */
public class ConvertToSeq {
    public static void main(String args[]) throws Exception{


        if (args.length != 2) {
            System.err.println("Arguments: [input space_separate_v file] [output sequence file]");
            return;
        }
        String inputFileName = args[0];
        String outputDirName = args[1];


        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(configuration);
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, configuration,
                new Path(outputDirName + "/point-vector"),
                Text.class, VectorWritable.class);


        BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
        VectorWritable vec = new VectorWritable();
        int count = 0;
        try {
            String s = null;
            while((s=reader.readLine())!=null){
                count++;
                String spl[] = s.split("\\s+");
                String key = count+"";
                Integer val = 0;
                double[] colvalues = new double[1000];
                for(int k=0;k<spl.length;k++){
                    colvalues[val] = Double.parseDouble(spl[k]);
                    val++;
                }
                NamedVector nmv = new NamedVector(new DenseVector(colvalues),key);
                vec.set(nmv);
                writer.append(new Text(nmv.getName()), vec);
            }
        } catch (Exception e) {
            System.out.println("ERROR: "+e);}

        writer.close();
    }
}
