package cmsc433.p3;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
//import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

//import cmsc433.p3.CodeCompareMR.HashReducer;
//import cmsc433.p3.CodeCompareMR.TokenizerMapper;


/**
 * This class uses Hadoop to take an input list in the form of
 * "<code>string</code>&#09;<code>int</code>" and output a list where the keys
 * and values are the same, except the output should now be sorted by the
 * natural ordering of the values, the integers, and not the keys.
 */
public class ValueSortMR {
	
	/** Minimum <code>int</code> value for a pair to be included in the output.
	 * Pairs with an <code>int</code> less than this value are omitted. */
	public static int CUTOFF = 1;
	
	
	public static class SwapMapper extends
			Mapper<Object, Text, IntWritable, Text> {

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			// Read lines using only tabs as the delimiter, as titles can contain spaces
			StringTokenizer itr = new StringTokenizer(value.toString(), "\t");
			if(itr.hasMoreTokens()){
				Text tit = new Text();
				tit.set(itr.nextToken());
				if(itr.hasMoreTokens()){
					String v = itr.nextToken();
					Integer v2 = Integer.parseInt(v);
					if(v2 >= CUTOFF){
						IntWritable count = new IntWritable();
						count.set(v2);
						
						context.write( count, tit);
					}
				}
			}

		}
	}

	public static class SwapReducer extends
			Reducer<IntWritable, Text, Text, IntWritable> {

		@Override
		public void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			for (Text tit : values) {
				System.out.println("This: " +tit + " , "+key);
				context.write(tit, key);
			}
		}
	}

	//Source: http://stackoverflow.com/questions/9493644/sort-order-with-hadoop-mapred
	static class ReverseComparator extends WritableComparator {
        private static final Text.Comparator TEXT_COMPARATOR = new Text.Comparator();

        public ReverseComparator() {
            super(Text.class);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return (-1)* TEXT_COMPARATOR
			        .compare(b1, s1, l1, b2, s2, l2);
        }

        @SuppressWarnings("rawtypes")
		@Override
        public int compare(WritableComparable a, WritableComparable b) {
            if (a instanceof Text && b instanceof Text) {
                return (-1)*(((Text) a)
                        .compareTo((Text) b));
            }
            return super.compare(a, b);
        }
    }
	/**
	 * This method performs value-based sorting on the given input by
	 * configuring the job as appropriate and using Hadoop.
	 * @param job Job created for this function
	 * @param input String representing location of input directory
	 * @param output String representing location of output directory
	 * @return True if successful, false otherwise
	 * @throws Exception
	 */
	public static boolean sort(Job job, String input, String output) throws Exception {
		job.setJarByClass(ValueSortMR.class);

		job.setMapperClass(SwapMapper.class);
		job.setReducerClass(SwapReducer.class);
		
		// Describe the input- and output-specifications
		// for the Map-Reduce job.
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		// Set the types of the output keys and values
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
	
		job.setSortComparatorClass(ReverseComparator.class);
	
		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));
		
		return job.waitForCompletion(true);
	}
}
