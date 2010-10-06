/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */

package org.encog.neural.networks.training;

import org.encog.cloud.CloudTask;
import org.encog.cloud.EncogCloud;
import org.encog.engine.util.Format;

/**
 * Report the status of Encog training to the cloud.
 */
public class TrainingStatusUtility {

	/**
	 * The last time an update was performed.
	 */
	private long lastUpdate;

	/**
	 * The iteration number.
	 */
	private int iteration;

	/**
	 * The training object.
	 */
	private final Train train;

	/**
	 * The cloud.
	 */
	private final EncogCloud cloud;

	/**
	 * The task that we are reporting to.
	 */
	private CloudTask task;

	/**
	 * Create a training status utility.
	 * 
	 * @param cloud
	 *            The cloud to report to.
	 * @param train
	 *            The training object being used.
	 */
	public TrainingStatusUtility(final EncogCloud cloud, final Train train) {
		this.cloud = cloud;
		this.train = train;
		this.lastUpdate = 0;
		this.iteration = 0;
	}

	/**
	 * Report that we are finished.
	 */
	public void finish() {
		final StringBuilder status = new StringBuilder();
		status.append("Done at iteration #");
		status.append(Format.formatInteger(this.iteration));
		status.append(" - Error: ");
		status.append(Format.formatPercent(this.train.getError()));

		this.task.stop(status.toString());
		this.task = null;
	}

	/**
	 * Perform an update.
	 */
	public void update() {
		final long now = System.currentTimeMillis();
		final long elapsed = (now - this.lastUpdate) / 1000;

		if (this.task == null) {
			this.task = this.cloud.beginTask(this.train.getClass()
					.getSimpleName());
		}

		this.iteration++;

		if (elapsed > 10) {
			this.lastUpdate = now;
			final StringBuilder status = new StringBuilder();
			status.append("Iteration #");
			status.append(Format.formatInteger(this.iteration));
			status.append(" - Error: ");
			status.append(Format.formatPercent(this.train.getError()));
			this.task.setStatus(status.toString());
		}
	}
}
