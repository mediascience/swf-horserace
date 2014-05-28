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
	 * @return result, either "ok" or "injury"
	 */
	String runHorse(String horseName, int lapNum);

}
