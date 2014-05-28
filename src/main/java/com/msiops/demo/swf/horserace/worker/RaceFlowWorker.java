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

import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;

public final class RaceFlowWorker {

	/**
	 * This is the actual client interface used by the worker implementations.
	 * Its credentials can be configured like any other client in the Java SDK.
	 * By default, it checks the environment, system properties, and (if running
	 * on EC2) the host role.
	 */
	private static final AmazonSimpleWorkflowClient SWF = new AmazonSimpleWorkflowClient();

	private static final String TASKLIST = "RACEFLOW-1.0";

	private static final String DOMAIN = "Demo";

	public static void main(final String[] args) throws Exception {

		new RaceFlowWorker().start();

	}

	private final WorkflowWorker worker;

	private RaceFlowWorker() throws Exception {

		/*
		 * Configure an Flow Framework WORKFLOW worker with a domain and queue.
		 */
		this.worker = new WorkflowWorker(SWF, DOMAIN, TASKLIST);

		/*
		 * Can add multiple worker classes. FF takes care of the lifecycle for
		 * each instance. A new instance of the registered class is created and
		 * run for every decision.
		 */
		this.worker.addWorkflowImplementationType(RaceFlowImpl.class);

	}

	private void start() {
		this.worker.start();
	}

}
