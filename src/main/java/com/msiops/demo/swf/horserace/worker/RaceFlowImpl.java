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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;
import com.amazonaws.services.simpleworkflow.flow.annotations.Wait;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

/**
 * Race flow implemtation.
 *
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
public class RaceFlowImpl implements RaceFlow {

	// private final RaceActivitiesClient race = new RaceActivitiesClientImpl();

	// private final SystemActivitiesClient sys = new
	// SystemActivitiesClientImpl();

	private final AnnouncerActivitiesClient announcer = new AnnouncerActivitiesClientImpl();

	private final HorseActivitiesClient horses = new HorseActivitiesClientImpl();

	/**
	 * The next horse that finishes gets this place. Even though the workflow is
	 * run in parallel, this value requires no synchronization!
	 */
	private int nextPlace = 1;

	@Override
	public void go(final Collection<String> horseNames, final int laps) {

		/*
		 * Start moving all horses to the starting gate and collect the promises
		 * to do so.
		 */
		final List<Promise<String>> arrivals = new ArrayList<>(
				horseNames.size());
		for (final String name : horseNames) {
			final Promise<String> arrival = subst(name, arriveGate(name));
			arrivals.add(arrival);
		}

		/*
		 * At this point, all arrivals are asynchronous. We need to synchronize
		 * so that we start only when all horses have arrived at the gate and
		 * are ready to go. Join returns a promise that is only fulfilled when
		 * every horse has arrived at the gate.
		 */
		final Promise<List<String>> ready = join(arrivals);

		/*
		 * Announce the race only after all horses have arrived.
		 */
		final Promise<Void> announced = announceRace(ready,
				Promise.asPromise(laps));

		/*
		 * Now start running the horses in parallel. Note that this invocation
		 * depends on "announced."
		 */
		final Promise<List<Promise<Void>>> results = runAll(laps, ready,
				announced);

		/*
		 * Before exiting the workflow, ensure that all horses have finished and
		 * all pending tasks are complete.
		 */
		announceEnd(join(results));

	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announceEnd(final Promise<?>... waitFor) {
		return this.announcer.announceEnd();
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announceFinished(final String name,
			final Promise<?>... waitFor) {
		return this.announcer.announceFinished(name);
	}

	/**
	 * Announce (or not) a single horse's result.
	 *
	 * @param name
	 *            name of the horse.
	 *
	 * @param result
	 *            horse's result ("ok" or "injured")
	 *
	 * @param waitFor
	 *            anonymous dependencies
	 *
	 * @return promise to respond.
	 *
	 */
	@Asynchronous
	private Promise<Void> announceHorseResult(final String name,
			final Promise<Status> result, final Promise<?>... waitFor) {

		final Promise<Void> rval;
		switch (result.get()) {
		case OK:
			if (this.nextPlace <= 3) {
				rval = announcePlace(name, this.nextPlace);
				this.nextPlace = this.nextPlace + 1;
			} else {
				rval = announceFinished(name);
			}
			break;
		case INJURY:
			rval = announceInjury(name);
			break;
		default:
			rval = announceMissing(name);
			break;
		}
		return rval;
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announceInjury(final String name,
			final Promise<?>... waitFor) {
		return this.announcer.announceInjury(name);
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announceLap(final String name, final int lap,
			final Promise<?>... waitFor) {
		return this.announcer.announceLap(name, lap);
	}

	/**
	 * Announce a lap only if the result was OK.
	 *
	 * @param name
	 *            horse name.
	 *
	 * @param lap
	 *            lap number.
	 *
	 * @param result
	 *            result of running lap.
	 *
	 * @param waitFor
	 *            anonymous dependencies.
	 *
	 * @return the passed result.
	 */
	@Asynchronous
	private Promise<Status> announceLapIfOk(final String name, final int lap,
			final Promise<Status> result, final Promise<?>... waitFor) {

		final Promise<Status> rval;
		if (result.get() == Status.OK) {
			rval = subst(result.get(), announceLap(name, lap));
		} else {
			rval = result;
		}
		return rval;

	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announceMissing(final String name,
			final Promise<?>... waitFor) {
		return this.announcer.announceMissing(name);
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announcePlace(final String name, final Integer place,
			final Promise<?>... waitFor) {
		return this.announcer.announcePlace(name, place);
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> announceRace(final Promise<List<String>> horses,
			final Promise<Integer> laps, final Promise<?>... waitFor) {

		return this.announcer.announceRace(horses, laps);

	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> arriveGate(final String name,
			final Promise<?>... waitFor) {
		return this.horses.arriveGate(name);
	}

	/**
	 * Convert a list of promised values into a promised list of values. Use
	 * this to synchronize multiple independent promises.
	 *
	 * @param async
	 *            independent promises to synchronize.
	 *
	 * @param waitFor
	 *            anonymous dependencies.
	 *
	 * @return promise to fulfill all.
	 */
	@Asynchronous
	private <T> Promise<List<T>> join(@Wait final List<Promise<T>> async,
			final Promise<?>... waitFor) {

		final List<T> result = new ArrayList<>(async.size());
		for (final Promise<T> pt : async) {
			result.add(pt.get());
		}

		return Promise.asPromise(result);

	}

	/**
	 * Convenient form of {@link #join(List, Promise...)} for a promised list of
	 * promises.
	 *
	 */
	@Asynchronous
	private <T> Promise<List<T>> join(final Promise<List<Promise<T>>> async,
			final Promise<?>... waitFor) {
		return join(async.get());
	}

	/**
	 * Run all the horses in parallel.
	 *
	 * @param laps
	 *            number of laps to run.
	 *
	 * @param horses
	 *            horses to run.
	 *
	 * @param waitFor
	 *            anonymous dependencies.
	 *
	 * @return a list of promises to run, one for each horse.
	 */
	@Asynchronous
	private Promise<List<Promise<Void>>> runAll(final int laps,
			final Promise<List<String>> horses, final Promise<?>... waitFor) {

		final List<Promise<Void>> race = new ArrayList<>(horses.get().size());
		for (final String name : horses.get()) {

			Promise<Status> horseRun = Promise.asPromise(Status.OK);
			for (int lapNum = 1; lapNum <= laps; lapNum = lapNum + 1) {
				horseRun = runLapIfOk(name, lapNum, horseRun);
				horseRun = announceLapIfOk(name, lapNum, horseRun);
			}
			final Promise<Void> done = announceHorseResult(name, horseRun);

			race.add(done);

		}

		return Promise.asPromise(race);

	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Status> runLap(final String name, final int lapNum,
			final Promise<?>... waitFor) {
		return this.horses.runLap(name, lapNum);
	}

	/**
	 * Run one lap if not injured.
	 *
	 * @param name
	 *            name of horse to run.
	 *
	 * @param lapNum
	 *            the lap number to run.
	 *
	 * @param prevStatus
	 *            previous lap run status.
	 *
	 * @param waitFor
	 *            anonymous dependencies.
	 *
	 * @return the result of running one lap or the previous lap result.
	 */
	@Asynchronous
	private Promise<Status> runLapIfOk(final String name, final int lapNum,
			final Promise<Status> prevStatus, final Promise<?>... waitFor) {

		if (prevStatus.get() == Status.OK) {
			/*
			 * horse is ok, run it.
			 */
			return runLap(name, lapNum);
		} else {
			/*
			 * something wrong, do not run.
			 */
			return prevStatus;
		}

	}

	/**
	 * Promise a value based on fulfillment of arbitrary values.
	 *
	 * @param sVal
	 *            promised value.
	 *
	 * @param waitFor
	 *            dependencies.
	 *
	 * @return promise to produce sVal.
	 */
	@Asynchronous
	private <T> Promise<T> subst(final T sVal, final Promise<?>... waitFor) {

		return Promise.asPromise(sVal);

	}

}
