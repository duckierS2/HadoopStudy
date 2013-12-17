package duckier.hadoop.chapter06;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import duckier.hadoop.chapter05.DelayCounters;

public class DelayCountWithDateKeyTest {

	MapDriver<LongWritable, Text, DateKey, IntWritable> mapDriver;
	ReduceDriver<DateKey, IntWritable, DateKey, IntWritable> reduceDriver;
	MapReduceDriver<LongWritable, Text, DateKey, IntWritable, DateKey, IntWritable> mapReduceDriver;

	@Before
	public void setUp() {
		DelayCountMapperWithDateKey mapper = new DelayCountMapperWithDateKey();
		DelayCountReducerWithDateKey reducer = new DelayCountReducerWithDateKey();
		mapDriver = MapDriver.newMapDriver(mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Test
	public void testMapper() throws IOException {
		mapDriver.withInput(new LongWritable(1), new Text(
				"2008,1,2,3,4,5,6,7,8,9,10,11,12,13,10,10,16,17,18,19,20"));
		mapDriver.withInput(new LongWritable(2), new Text(
				"2008,1,2,3,4,5,6,7,8,9,10,11,12,13,0,0,16,17,18,19,20"));
		mapDriver.withInput(new LongWritable(3), new Text(
				"2008,1,2,3,4,5,6,7,8,9,10,11,12,13,-10,-10,16,17,18,19,20"));
		mapDriver.withInput(new LongWritable(3), new Text(
				"2008,1,2,3,4,5,6,7,8,9,10,11,12,13,NA,NA,16,17,18,19,20"));
		mapDriver.withOutput(new DateKey("D,2008", 1), new IntWritable(1));
		mapDriver.withOutput(new DateKey("A,2008", 1), new IntWritable(1));
		mapDriver.runTest();
		assertEquals("Experted 1 counter increment", 1, mapDriver.getCounters()
				.findCounter(DelayCounters.not_available_departure).getValue());
		assertEquals("Experted 1 counter increment", 1, mapDriver.getCounters()
				.findCounter(DelayCounters.not_available_arrival).getValue());
	}

	@Ignore
	@Test
	public void testReducer() throws IOException {
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		reduceDriver.withInput(new DateKey("D,2008", 1), values);
		reduceDriver.withOutput(new DateKey("2008", 1), new IntWritable(2));
		reduceDriver.runTest();
	}
}
