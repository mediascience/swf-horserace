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

/**
 * <p>
 * This class provides the full workflow worker implementation, including
 * polling for tasks, performing announcer duties, and reporting results.
 * </p>
 * 
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
public final class RaceFlowWorker {

	/**
	 * This is the actual client interface used by the worker implementations.
	 * Its credentials can be configured like any other client in the Java SDK.
	 * By default, it checks the environment, system properties, and (if running
	 * on EC2) the host role.
	 */
	private static final AmazonSimpleWorkflowClient SWF = new AmazonSimpleWorkflowClient();

	/**
	 * The task list that this worker listens on. It also becomes the default
	 * task list for this worker's workflow types if this worker registers them.
	 */
	private static final String TASKLIST = "RACEFLOW-1.0";

	/**
	 * <p>
	 * Domain is an administrative boundary in SWF. You are limited to 100
	 * domains per account and they cannot be deleted. You create a domain
	 * through an API call or through the AWS web console.
	 * </p>
	 * <p>
	 * Note that a domain is configured with a workflow retention period that
	 * cannot be changed. Also, there hard limits on the numbers of workflow and
	 * activity types and versions that can be registered on a domain.
	 * </p>
	 */
	private static final String DOMAIN = "Demo";

	public static void main(final String[] args) throws Exception {

		new RaceFlowWorker().start();

	}

	/**
	 * Delegate worker provided by FF.
	 */
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
		/*
		 * By default and if not already registered, starting a WorkflowWorker registers all workflow types
		 * and versions captured from the @Execute and @Signal annotations of
		 * the classes registered to it.
		 * 
		 * It also sets the default timeouts and task list for the workflow from
		 * the settings in the annotations. Once set, those values cannot be
		 * changed so beware.
		 */
		this.worker.start();
	}

}
