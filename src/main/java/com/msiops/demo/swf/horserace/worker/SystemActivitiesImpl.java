package com.msiops.demo.swf.horserace.worker;

public class SystemActivitiesImpl implements SystemActivities {

	private final int instance;

	public SystemActivitiesImpl(final int instance) {
		this.instance = instance;
	}

	@Override
	public void log(final String s) {
		System.out.println(String.format("SYSTEM %d: %s", this.instance, s));
	}

}
