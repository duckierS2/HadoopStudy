package duckier.hadoop.chapter06;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.mortbay.log.Log;

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
		// TODO 전역변수가 아닌데 이전키에 대한 값을 유지하는가??
		Integer bMonth = key.getMonth();

		if (columns[0].equals("D")) {
			for (IntWritable value : values) {
				if (bMonth != key.getMonth()) {
					outputKey.setYear(columns[1]);
					outputKey.setMonth(bMonth);
					result.set(sum);
					mos.write("departure", outputKey, result);
					sum = 0;
				}
				sum += value.get();
				// TODO 다시 변수에 값을 셋팅하는 이유는?
				bMonth = key.getMonth();
			}
			// TODO 마지막 월의 지연 횟수
			if (bMonth == key.getMonth()) {
				outputKey.setYear(columns[1]);
				// TODO bMonth가 아닌 key.getMonth()를 하는 이유
				outputKey.setMonth(key.getMonth());
				result.set(sum);
				mos.write("departure", outputKey, result);
			}
		} else {
			for (IntWritable value : values) {
				if (bMonth != key.getMonth()) {
					outputKey.setYear(columns[1]);
					outputKey.setMonth(bMonth);
					result.set(sum);
					mos.write("arrival", outputKey, result);
					sum = 0;
				}
				sum += value.get();
				bMonth = key.getMonth();
			}
			if (bMonth == key.getMonth()) {
				outputKey.setYear(columns[1]);
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
