package com.msiops.demo.swf;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.simpleworkflow.flow.annotations.Asynchronous;
import com.amazonaws.services.simpleworkflow.flow.annotations.Wait;
import com.amazonaws.services.simpleworkflow.flow.core.Promise;

public final class PromiseUtil {

	/**
	 * Convert a list of promised values into a promised list of values. Use
	 * this to synchronize multiple independent promises.
	 *
	 * @param async
	 *            independent promises to synchronize. Notice the {@link Wait}
	 *            annotation. It tells FF that we want to wait for every promise
	 *            in the collection to be fulfilled.
	 *
	 * @param waitFor
	 *            anonymous dependencies.
	 *
	 * @return promise to fulfill all.
	 */
	@Asynchronous
	public static <T> Promise<List<T>> join(@Wait final List<Promise<T>> async,
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
	public static <T> Promise<List<T>> join(
			final Promise<List<Promise<T>>> async, final Promise<?>... waitFor) {
		return join(async.get());
	}

	/**
	 * Promise a value based on fulfillment of arbitrary values.
	 *
	 * @param sVal
	 *            substitution.
	 *
	 * @param waitFor
	 *            dependencies.
	 *
	 * @return promise to produce sVal.
	 */
	@Asynchronous
	public static <T> Promise<T> subst(final T sVal,
			final Promise<?>... waitFor) {

		return Promise.asPromise(sVal);

	}

	/**
	 * Synchronize on arbitrary dependencies.
	 *
	 * @param val
	 *            promise to return.
	 *
	 * @param waitFor
	 *            additional dependencies.
	 *
	 * @return promise to produce sVal.
	 */
	@Asynchronous
	public static <T> Promise<T> sync(final Promise<T> val,
			final Promise<?>... waitFor) {

		return val;

	}

	/**
	 * No instances.
	 */
	private PromiseUtil() {
		/*
		 * no instances
		 */
	}

}
