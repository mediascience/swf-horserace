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
public interface HorseActivities {

	/**
	 * Bring a horse to the starting gate and ready it for running.
	 *
	 * @param name
	 *            name of arriving horse.
	 */
	void arriveGate(String name);

	/**
	 * Run a horse around the track one time. A horse has a chance of becoming
	 * injured while running.
	 *
	 * @param name
	 *            name of horse to run.
	 *
	 * @param lapNum
	 *            the current lap number.
	 *
	 * @return result of attempting the lap.
	 */
	Status runLap(String name, int lapNum);

}
