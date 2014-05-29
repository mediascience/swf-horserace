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

import java.util.Collection;

import com.amazonaws.services.simpleworkflow.flow.annotations.Execute;
import com.amazonaws.services.simpleworkflow.flow.annotations.Workflow;
import com.amazonaws.services.simpleworkflow.flow.annotations.WorkflowRegistrationOptions;

/**
 * Horse Race workflow. The implementation of this class will implement the
 * workflow logic.
 *
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
@Workflow
@WorkflowRegistrationOptions(defaultExecutionStartToCloseTimeoutSeconds = 900, defaultTaskStartToCloseTimeoutSeconds = 60)
public interface RaceFlow {

	/**
	 * <p>
	 * Start the race including organizing the horses, counting laps, and
	 * announcing winners.
	 * </p>
	 * <p>
	 * Note that the method is marked with {@link Execute}. This signals the
	 * stub generator to generate corresponding methods in the stubs and
	 * configure the stubs to use them to request new workflows.
	 * </p>
	 *
	 * @param horseNames
	 *            the names of the horses in the race.
	 *
	 * @param laps
	 *            the number laps to run.
	 */
	@Execute(version = "1.0.0")
	void go(Collection<String> horseNames, int laps);

}
