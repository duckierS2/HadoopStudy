package duckier.hadoop.chapter06;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import duckier.hadoop.chapter05.DelayCounters;

public class DelayCountMapperWithDateKey extends
		Mapper<LongWritable, Text, DateKey, IntWritable> {

	// map ��°�
	private final static IntWritable outputValue = new IntWritable(1);

	// map ���Ű
	private DateKey outputKey = new DateKey();

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		if (key.get() > 0) {
			// �޸� ������ �и�
			String[] columns = value.toString().split(",");
			if (columns != null && columns.length > 0) {
				try {
					// ��� ���� ������ ���
					if (!columns[15].equals("NA")) {
						int depDelayTime = Integer.parseInt(columns[15]);
						if (depDelayTime > 0) {
							// ���Ű ����
							outputKey.setYear("D," + columns[0]);
							outputKey.setMonth(new Integer(columns[1]));

							// ��� ������ ����
							context.write(outputKey, outputValue);
						} else if (depDelayTime == 0) {
							context.getCounter(
									DelayCounters.scheduled_departure)
									.increment(1);
						} else if (depDelayTime < 0) {
							context.getCounter(DelayCounters.ealry_departure)
									.increment(1);
						}
					} else {
						context.getCounter(DelayCounters.not_available_departure)
								.increment(1);
					}

					// ���� ���� ������ ���
					if (!columns[14].equals("NA")) {
						int arrDelayTime = Integer.parseInt(columns[14]);
						if (arrDelayTime > 0) {
							// ���Ű ����
							outputKey.setYear("A," + columns[0]);
							outputKey.setMonth(new Integer(columns[1]));

							// ��� ������ ����
							context.write(outputKey, outputValue);
						} else if (arrDelayTime == 0) {
							context.getCounter(DelayCounters.scheduled_arrival)
									.increment(1);
						} else if (arrDelayTime < 0) {
							context.getCounter(DelayCounters.ealry_arrival)
									.increment(1);
						}
					} else {
						context.getCounter(DelayCounters.not_available_arrival)
								.increment(1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
