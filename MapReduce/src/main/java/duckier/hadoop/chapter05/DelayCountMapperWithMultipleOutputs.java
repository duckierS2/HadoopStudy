package duckier.hadoop.chapter05;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DelayCountMapperWithMultipleOutputs extends
		Mapper<LongWritable, Text, Text, IntWritable> {

	// map 출력값
	private final static IntWritable outputValue = new IntWritable(1);
	// map 출력키
	private Text outputKey = new Text();

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		if (key.get() > 0) {
			// 콤마 구분자 분리
			String[] columns = value.toString().split(",");
			if (columns != null && columns.length > 0) {
				try {
					// 출발 지연 데이터 출력
					if (!columns[15].equals("NA")) {
						int depDelayTime = Integer.parseInt(columns[15]);
						if (depDelayTime > 0) {
							// 출력키 설정
							outputKey.set("D," + columns[0] + "," + columns[1]);
							// 출력 데이터 생성
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
						context.getCounter(
								DelayCounters.not_available_departure)
								.increment(1);
					}

					// 도착 지연 데이터 출력
					if (!columns[14].equals("NA")) {
						int arrDelayTime = Integer.parseInt(columns[14]);
						if (arrDelayTime > 0) {
							// 출력키 설정
							outputKey.set("A," + columns[0] + "," + columns[1]);
							// 출력 데이터 생성
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
