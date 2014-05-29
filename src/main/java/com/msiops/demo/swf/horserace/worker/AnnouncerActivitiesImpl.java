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

import java.util.List;

/**
 * Implement announcer behavior.
 * 
 * @author greg wiley <aztec.rex@jammm.com>
 *
 */
final class AnnouncerActivitiesImpl implements AnnouncerActivities {

	private final int instance;

	public AnnouncerActivitiesImpl(final int instance) {
		this.instance = instance;
	}

	@Override
	public void announceEnd() {
		say("...and the race is over.");
	}

	@Override
	public void announceFinished(final String name) {
		say("'" + name + "' finished the race without placing.");
	}

	@Override
	public void announceInjury(final String name) {
		say("'" + name + "' is injured and leaving the field.");
	}

	@Override
	public void announceLap(final String name, final int lap) {
		say("'" + name + "' just completed lap " + lap + ".");
	}

	@Override
	public void announceMissing(final String name) {

		say("What happened to '" + name + "?!'");

	}

	@Override
	public void announcePlace(final String name, final int place) {
		say("'" + name + "' has finished the race in place " + place + "!");
	}

	@Override
	public void announceRace(final List<String> names, final int laps) {
		final StringBuffer mbuf = new StringBuffer();

		mbuf.append("Running ");
		for (final String name : names) {
			mbuf.append('\'').append(name).append("' ");
		}
		mbuf.append("for ").append(laps).append(" laps.");

		say(mbuf.toString());

		say("And they're off!");
	}

	/**
	 * Announce something.
	 * 
	 * @param s
	 *            what to announce.
	 */
	private void say(final String s) {

		/*
		 * include the instance id to demonstrate load balancing.
		 */
		final String msg = String.format("ANNOUNCER %d: %s", this.instance, s);
		System.out.println(msg);

	}

}
