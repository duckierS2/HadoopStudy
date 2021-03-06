package duckier.hadoop.chapter06;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class DelayCountReducerWithDateKey extends
		Reducer<DateKey, IntWritable, DateKey, IntWritable> {

	private MultipleOutputs<DateKey, IntWritable> mos;

	// reduce 출력키
	private DateKey outputKey = new DateKey();

	// reduce 출력값
	private IntWritable result = new IntWritable();

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		mos = new MultipleOutputs<DateKey, IntWritable>(context);
	}

	@Override
	public void reduce(DateKey key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		// 콤마 구분자 분리
		String[] columns = key.getYear().split(",");

		int sum = 0;
		Integer bMonth = key.getMonth();

		if (columns[0].equals("D")) {
			for (IntWritable value : values) {
				if (bMonth != key.getMonth()) {
					outputKey.setYear(key.getYear().substring(2));
					outputKey.setMonth(bMonth);
					result.set(sum);
					mos.write("departure", outputKey, result);
					sum = 0;
				}
				sum += value.get();
				bMonth = key.getMonth();
			}
			if (bMonth == key.getMonth()) {
				outputKey.setYear(key.getYear().substring(2));
				outputKey.setMonth(key.getMonth());
				result.set(sum);
				mos.write("departure", outputKey, result);
			}
		} else {
			for (IntWritable value : values) {
				if (bMonth != key.getMonth()) {
					outputKey.setYear(key.getYear().substring(2));
					outputKey.setMonth(bMonth);
					result.set(sum);
					mos.write("arrival", outputKey, result);
					sum = 0;
				}
				sum += value.get();
				bMonth = key.getMonth();
			}
			if (bMonth == key.getMonth()) {
				outputKey.setYear(key.getYear().substring(2));
				outputKey.setMonth(key.getMonth());
				result.set(sum);
				mos.write("arrival", outputKey, result);
			}
		}
	}

	@Override
	public void cleanup(Context context) throws IOException,
			InterruptedException {
		mos.close();
	}
}
