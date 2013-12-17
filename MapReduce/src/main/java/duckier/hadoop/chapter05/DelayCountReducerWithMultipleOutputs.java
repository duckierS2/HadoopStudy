package duckier.hadoop.chapter05;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class DelayCountReducerWithMultipleOutputs extends
		Reducer<Text, IntWritable, Text, IntWritable> {

	private MultipleOutputs<Text, IntWritable> mos;

	// reduce 출력키
	private Text outputKey = new Text();
	// reduce 출력값
	private IntWritable result = new IntWritable();

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		mos = new MultipleOutputs<Text, IntWritable>(context);
	}

	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
		// 콤마 구분자 분리
		String[] columns = key.toString().split(",");
		// 출력키 설정
		outputKey.set(columns[1] + "," + columns[2]);

		// 출발 지연
		if (columns[0].equals("D")) {
			// 지연 횟수 합산
			int sum = 0;
			for (IntWritable value : values) {
				sum += value.get();
			}
			// 출력값 설정
			result.set(sum);
			// 출력 데이터 생성
			mos.write("departure", outputKey, result);
		}
		// 도착 지연
		else {
			// 지연 횟수 합산
			int sum = 0;
			for (IntWritable value : values) {
				sum += value.get();
			}
			// 출력값 설정
			result.set(sum);
			// 출력 데이터 생성
			mos.write("arrival", outputKey, result);
		}
	}

	@Override
	public void cleanup(Context context) throws IOException,
			InterruptedException {
		mos.close();
	}
}
