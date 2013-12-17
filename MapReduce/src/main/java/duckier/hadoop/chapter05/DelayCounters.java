package duckier.hadoop.chapter05;

public enum DelayCounters {
	/** ��� ���� �ð��� NA(Not Available)�� ��� */
	not_available_arrival,
	/** ���ÿ� ����� ��� */
	scheduled_arrival,
	/** �������� ���� ����� ��� */
	ealry_arrival,
	/** ���� ���� �ð��� NA(Not Available)�� ��� */
	not_available_departure,
	/** ���ÿ� ������ ��� */
	scheduled_departure,
	/** �������� ���� ������ ��� */
	ealry_departure;
}
