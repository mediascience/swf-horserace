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
import com.amazonaws.services.simpleworkflow.flow.ActivityWorker;

/**
 * <p>
 * This class provides the full horse worker implementation, including polling
 * for tasks, performing announcer duties, and reporting results.
 * </p>
 * 
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
public final class HorseActivitiesWorker {

	/**
	 * This is the actual client interface used by the worker implementations.
	 * Its credentials can be configured like any other client in the Java SDK.
	 * By default, it checks the environment, system properties, and (if running
	 * on EC2) the host role.
	 */
	private static final AmazonSimpleWorkflowClient SWF = new AmazonSimpleWorkflowClient();

	/**
	 * The task list that this worker listens on. It also becomes the default task
	 * list for this worker's activity types if this worker registers them.
	 */
	private static final String TASKLIST = "HORSEACTIVITIES-1.0";

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

		final int instance = args.length > 0 ? Integer.valueOf(args[0]) : 1;

		new HorseActivitiesWorker(instance).start();

	}

	/**
	 * Delegate worker provided by FF.
	 */
	private final ActivityWorker worker;

	private HorseActivitiesWorker(final int instance) throws Exception {

		/*
		 * Configure an Flow Framework ACTIVITY worker with a domain and queue.
		 */
		this.worker = new ActivityWorker(SWF, DOMAIN, TASKLIST);

		/*
		 * Can add multiple activities implementation instances. Each is a
		 * singleton that remains active for the duration of the worker. There
		 * should be no shared mutable state in an activities implementation.
		 */
		this.worker.addActivitiesImplementation(new HorseActivitiesImpl(
				instance));

	}

	private void start() {
		/*
		 * By default and if not already registered, starting a ActivityWorker
		 * registers all activity types and versions captured from the @Activity
		 * annotations of the classes whose instances are registered to it.
		 * 
		 * It also sets the default timeouts and task list for the types from
		 * the settings in the annotations. Once set, those values cannot be
		 * changed so beware.
		 */
		this.worker.start();
	}

}
