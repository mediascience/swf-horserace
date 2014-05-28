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

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

/**
 * Activities performed during a horse race.
 *
 * @author greg wiley <actec.rex@jammm.com>
 *
 */
@Activities(version = "1.0.0")
@ActivityRegistrationOptions(defaultTaskStartToCloseTimeoutSeconds = 60, defaultTaskScheduleToStartTimeoutSeconds = 60)
public interface RaceActivities {

	/**
	 * Announce the start of a race.
	 *
	 * @param names
	 *            the names of the horses in the race.
	 *
	 * @param laps
	 *            the number laps in the race.
	 */
	public void announceRace(List<String> names, int laps);

	/**
	 * Bring a horse to the starting gate and ready it for running.
	 *
	 * @param name
	 *            name of arriving horse.
	 */
	void arriveHorse(String name);

	/**
	 * Announce that an injured horse is leaving.
	 *
	 * @param horseName
	 *            name of leaving horse.
	 */
	void leaveHorse(String horseName);

	/**
	 * Announce that a horse has placed.
	 *
	 * @param horseName
	 *            name of horse to announce.
	 *
	 * @param place
	 *            finishing place.
	 */
	void placeHorse(String horseName, int place);

	/**
	 * Run a horse around the track one time.
	 *
	 * @param horseName
	 *            name of horse to run.
	 *
	 * @param lapNum
	 *            the current lap number.
	 *
	 * @return result of attempting lap.
	 */
	Status runHorse(String horseName, int lapNum);

}
