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
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
@Activities(version = "1.0.0")
@ActivityRegistrationOptions(defaultTaskStartToCloseTimeoutSeconds = 15, defaultTaskScheduleToStartTimeoutSeconds = 15)
public interface AnnouncerActivities {

	/**
	 * Announce the end of a race.
	 */
	public void announceEnd();

	/**
	 * Announce that a horse finished without placing.
	 *
	 * @param name
	 *            horse name.
	 */
	public void announceFinished(String name);

	/**
	 * Announce that a horse is injured.
	 *
	 * @param name
	 */
	public void announceInjury(String name);

	/**
	 * Announce that a horse has completed a lap.
	 *
	 * @param name
	 *            horse name.
	 *
	 * @param lap
	 *            lap number
	 */
	public void announceLap(String name, int lap);

	/**
	 * Announce that a horse is missing. This happens if the horse's status
	 * cannot be determined (bad flow logic).
	 *
	 * @param name
	 */
	public void announceMissing(String name);

	/**
	 * Announce that a horse placed.
	 *
	 * @param name
	 *            horse name.
	 *
	 * @param place
	 *            horse place.
	 *
	 */
	public void announcePlace(String name, int place);

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

}
