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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform horse activities.
 *
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
final class HorseActivitiesImpl implements HorseActivities {

	private static final Logger LOG = LoggerFactory
		.getLogger(HorseActivitiesImpl.class);

	private static final double CHANCE_OF_INJURY = 0.035;

	private final int instance;

	private final Random rng = new Random();

	private static final int DELAY_BASE_MS = 2000;

	public HorseActivitiesImpl(final int instance) {
		this.instance = instance;
	}

	@Override
	public void arriveGate(final String name) {

		final long delay = this.rng.nextInt(DELAY_BASE_MS);
		LOG.info("HORSES {}: {} approaching gate", new Object[] {
				this.instance, name });
		delayMs(delay);
		LOG.info("HORSES {}: {} arrive gate after {}ms", new Object[] {
				this.instance, name, delay });

	}

	@Override
	public Status runLap(final String name, final int lap) {

		final int delayMax;
		final Status rval;
		if (this.rng.nextDouble() < CHANCE_OF_INJURY) {
			delayMax = DELAY_BASE_MS / 2;
			rval = Status.INJURY;
		} else {
			delayMax = DELAY_BASE_MS;
			rval = Status.OK;
		}
		final long delay = this.rng.nextInt(delayMax);
		LOG.info("HORSES {}: {} starting lap {}", new Object[] { this.instance,
				name, lap });
		delayMs(delay);
		LOG.info("HORSES {}: {} {} after {}ms", new Object[] { this.instance,
				name, rval, delay });

		return rval;

	}

	private final void delayMs(final long millis) {

		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			// re-assert
			Thread.currentThread().interrupt();
			throw new RuntimeException("Activity interrupted");
		}

	}

}
