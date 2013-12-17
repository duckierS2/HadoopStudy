package duckier.hadoop.chapter06;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.mortbay.log.Log;

public class DelayCountReducerWithDateKey extends
		Reducer<DateKey, IntWritable, DateKey, IntWritable> {

	private MultipleOutputs<DateKey, IntWritable> mos;

	// reduce ���Ű
	private DateKey outputKey = new DateKey();

	// reduce ��°�
	private IntWritable result = new IntWritable();

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		mos = new MultipleOutputs<DateKey, IntWritable>(context);
	}

	@Override
	public void reduce(DateKey key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		// �޸� ������ �и�
		String[] columns = key.getYear().split(",");

		int sum = 0;
		// TODO ���������� �ƴѵ� ����Ű�� ���� ���� �����ϴ°�??
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
				// TODO �ٽ� ������ ���� �����ϴ� ������?
				bMonth = key.getMonth();
			}
			// TODO ������ ���� ���� Ƚ��
			if (bMonth == key.getMonth()) {
				outputKey.setYear(columns[1]);
				// TODO bMonth�� �ƴ� key.getMonth()�� �ϴ� ����
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