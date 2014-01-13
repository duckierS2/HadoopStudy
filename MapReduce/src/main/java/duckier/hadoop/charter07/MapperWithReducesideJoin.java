package duckier.hadoop.charter07;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperWithReducesideJoin extends
		Mapper<LongWritable, Text, Text, Text> {

	// �±� ����
	public final static String DATA_TAG = "B";

	// map ���Ű
	private Text outputKey = new Text();

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		if (key.get() > 0) {
			// �޸� ������ �и�
			String[] columns = value.toString().split(",");
			if (columns != null && columns.length > 0) {
				try {
					outputKey.set(columns[0] + "_" + DATA_TAG);
					context.write(outputKey, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
