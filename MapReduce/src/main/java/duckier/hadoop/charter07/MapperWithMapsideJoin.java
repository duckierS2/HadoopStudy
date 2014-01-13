package duckier.hadoop.charter07;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MapperWithMapsideJoin extends
		Mapper<LongWritable, Text, Text, Text> {

	private Hashtable<String, String> joinMap = new Hashtable<String, String>();

	// map ���Ű
	private Text outputKey = new Text();

	public void setup(Context context) throws IOException, InterruptedException {
		try {
			// �л�ĳ�� ��ȸ
			Path[] cacheFiles = DistributedCache.getLocalCacheFiles(context
					.getConfiguration());
			// ���� ������ ����
			if (cacheFiles != null && cacheFiles.length > 0) {
				String line;
				String[] tokens;
				BufferedReader br = new BufferedReader(new FileReader(
						cacheFiles[0].toString()));
				try {
					while ((line = br.readLine()) != null) {
						tokens = line.toString().replaceAll("\"", "")
								.split(",");
						joinMap.put(tokens[0], tokens[1]);
					}
				} finally {
					br.close();
				}
			} else {
				System.out.println("### cache files is null!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		if (key.get() > 0) {
			// �޸� ������ �и�
			String[] columns = value.toString().split(",");
			if (columns != null && columns.length > 0) {
				try {
					outputKey.set(joinMap.get(columns[8]));
					context.write(outputKey, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}