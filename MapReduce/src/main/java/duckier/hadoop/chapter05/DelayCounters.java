package duckier.hadoop.chapter05;

public enum DelayCounters {
	/** 출발 지연 시간이 NA(Not Available)일 경우 */
	not_available_arrival,
	/** 정시에 출발한 경우 */
	scheduled_arrival,
	/** 예정보다 일찍 출발한 경우 */
	ealry_arrival,
	/** 도착 지연 시간이 NA(Not Available)일 경우 */
	not_available_departure,
	/** 정시에 도착한 경우 */
	scheduled_departure,
	/** 예정보다 일찍 도착한 경우 */
	ealry_departure;
}
