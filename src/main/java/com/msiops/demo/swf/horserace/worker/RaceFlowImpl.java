package com.msiops.demo.swf.horserace.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.annotations.ExponentialRetry;
import com.amazonaws.services.simpleworkflow.flow.annotations.Wait;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

public class RaceFlowImpl implements RaceFlow {

	private final RaceActivitiesClient race = new RaceActivitiesClientImpl();

	private final SystemActivitiesClient sys = new SystemActivitiesClientImpl();

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
			final Promise<String> arrival = subst(name, arriveHorse(name));
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
		log("Finished", join(results));

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
				rval = placeHorse(name, this.nextPlace);
				this.nextPlace = this.nextPlace + 1;
			} else {
				rval = log("'" + name + "' did not place");
			}
			break;
		case INJURY:
			rval = leaveHorse(name);
			break;
		default:
			rval = log("What happened to '" + name + "?'");
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
	private Promise<Void> announceRace(final Promise<List<String>> horses,
			final Promise<Integer> laps, final Promise<?>... waitFor) {

		return this.race.announceRace(horses, laps);

	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> arriveHorse(final String name,
			final Promise<?>... waitFor) {
		return this.race.arriveHorse(name);
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
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> leaveHorse(final String name,
			final Promise<?>... waitFor) {
		return this.race.leaveHorse(name);
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> log(final Object o, final Promise<?>... waitFor) {
		return this.sys.log(String.valueOf(o));
	}

	/**
	 * Log a promised value.
	 *
	 * @param po
	 *            promised value.
	 *
	 * @param waitFor
	 *            anonymous dependencies.
	 *
	 * @return promise to log promised value.
	 */
	@Asynchronous
	private Promise<Void> log(final Promise<? extends Object> po,
			final Promise<?>... waitFor) {
		return log(po.get());
	}

	/**
	 * Local wrapper for activities client invocation. This lets us specify
	 * retry policy that handles failures from workers and from SWF itself. All
	 * parameters passed directly to client and return whatever the client
	 * returns.
	 */
	@ExponentialRetry(initialRetryIntervalSeconds = 2, maximumRetryIntervalSeconds = 30, maximumAttempts = 5)
	@Asynchronous
	private Promise<Void> placeHorse(final String name, final Integer place,
			final Promise<?>... waitFor) {
		return this.race.placeHorse(name, place);
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
				horseRun = runLap(name, lapNum, horseRun);
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
	private Promise<Status> runHorse(final String name, final int lapNum,
			final Promise<?>... waitFor) {
		return this.race.runHorse(name, lapNum);
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
	private Promise<Status> runLap(final String name, final int lapNum,
			final Promise<Status> prevStatus, final Promise<?>... waitFor) {

		if (prevStatus.get().equals("ok")) {
			/*
			 * horse is ok, run it.
			 */
			return runHorse(name, lapNum);
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
