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

public class SystemActivitiesWorker {

	/**
	 * This is the actual client interface used by the worker implementations.
	 * Its credentials can be configured like any other client in the Java SDK.
	 * By default, it checks the environment, system properties, and (if running
	 * on EC2) the host role.
	 */
	private static final AmazonSimpleWorkflowClient SWF = new AmazonSimpleWorkflowClient();

	private static final String TASKLIST = "SYSACTIVITIES-1.0";

	private static final String DOMAIN = "Demo";

	public static void main(final String[] args) throws Exception {

		final int instance = args.length > 0 ? Integer.valueOf(args[0]) : 1;

		new SystemActivitiesWorker(instance).start();

	}

	private final ActivityWorker worker;

	private SystemActivitiesWorker(final int instance) throws Exception {

		/*
		 * Configure an Flow Framework ACTIVITY worker with a domain and queue.
		 */
		this.worker = new ActivityWorker(SWF, DOMAIN, TASKLIST);

		/*
		 * Can add multiple activities implementation instances. Each is a
		 * singleton that remains active for the duration of the worker. There
		 * should be no shared mutable state in an activities implementation.
		 */
		this.worker.addActivitiesImplementation(new SystemActivitiesImpl(
				instance));

	}

	private void start() {
		this.worker.start();
	}

}
