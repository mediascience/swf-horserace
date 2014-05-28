/*
 * Licensed to Media Science International (MSI) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. MSI
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.msiops.demo.swf.horserace.worker;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RaceActivitiesImpl implements RaceActivities {

	private static final double CHANCE_OF_INJURY = 0.1;

	private final int instance;

	private final Random rng = new Random();

	RaceActivitiesImpl(final int instance) {
		this.instance = instance;
	}

	@Override
	public void announceRace(final List<String> names, final int laps) {

		final StringBuffer mbuf = new StringBuffer();

		mbuf.append("Running ");
		for (final String name : names) {
			mbuf.append('\'').append(name).append("' ");
		}
		mbuf.append("for ").append(laps).append(" laps.");

		say(mbuf.toString());

	}

	@Override
	public void arriveHorse(final String name) {

		delayS(this.rng.nextInt(3));
		say("'" + name + "' has arrived at the gate.");

	}

	@Override
	public void leaveHorse(final String name) {
		say("'" + name + "' has left the field with an injury.");
	}

	@Override
	public void placeHorse(final String name, final int place) {
		say("'" + name + "' finished in place " + place + ".");
	}

	@Override
	public Status runHorse(final String name, final int lapNum) {
		delayS(this.rng.nextInt(2));
		if (this.rng.nextDouble() < CHANCE_OF_INJURY) {
			return Status.INJURY;
		} else {
			say("'" + name + "' has completed lap " + lapNum + ".");
			return Status.OK;
		}

	}

	private final void delayS(final long seconds) {

		try {
			Thread.sleep(TimeUnit.MILLISECONDS.convert(seconds,
					TimeUnit.SECONDS));
		} catch (final InterruptedException e) {
			// re-assert
			Thread.currentThread().interrupt();
			throw new RuntimeException("Activity interrupted");
		}

	}

	private void say(final String s) {

		final String msg = String
			.format("RACE WORKER %d: %s", this.instance, s);
		System.out.println(msg);

	}

}
