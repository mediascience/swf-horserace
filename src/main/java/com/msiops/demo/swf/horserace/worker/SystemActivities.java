package com.msiops.demo.swf.horserace.worker;

import com.amazonaws.services.simpleworkflow.flow.annotations.Activities;
import com.amazonaws.services.simpleworkflow.flow.annotations.ActivityRegistrationOptions;

/**
 * General activities for monitoring the workflow.
 * 
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
@Activities(version = "1.0.0")
@ActivityRegistrationOptions(defaultTaskStartToCloseTimeoutSeconds = 60, defaultTaskScheduleToStartTimeoutSeconds = 60)
public interface SystemActivities {

	/**
	 * Put an entry in the log.
	 * 
	 * @param s
	 *            value to log.
	 */
	void log(String s);

}
