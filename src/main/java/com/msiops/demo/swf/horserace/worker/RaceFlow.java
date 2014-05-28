package com.msiops.demo.swf.horserace.worker;

import java.util.Collection;

/**
 * Run a horse race.
 * 
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
public interface RaceFlow {

	/**
	 * Start the race including organizing the horses, counting laps, and
	 * announcing winners.
	 * 
	 * @param horseNames
	 *            the names of the horses in the race.
	 * 
	 * @param laps
	 *            the number laps to run.
	 */
	void go(Collection<String> horseNames, int laps);

}
