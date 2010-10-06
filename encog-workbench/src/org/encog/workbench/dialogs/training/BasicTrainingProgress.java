/*
 * Encog(tm) Workbench v2.5
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
package org.encog.workbench.dialogs.training;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.TrainingContinuation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.workbench.EncogWorkBench;
import org.encog.workbench.tabs.EncogCommonTab;
import org.encog.workbench.util.EncogFonts;
import org.encog.workbench.util.TimeSpanFormatter;

/**
 * Common dialog box that displays the training progress for most of the
 * training methods. A chart is shown that displays the decline of the error.
 * Additionally, the user can start, stop or abort the training process.
 * 
 * @author jheaton
 * 
 */
public abstract class BasicTrainingProgress extends EncogCommonTab implements
		Runnable, ActionListener {

	/**
	 * The start button.
	 */
	private final JButton buttonStart;

	/**
	 * The stop button.
	 */
	private final JButton buttonStop;

	/**
	 * The close button.
	 */
	private final JButton buttonClose;

	/**
	 * The body of the dialog box is stored in this panel.
	 */
	private final JPanel panelBody;

	/**
	 * The buttons are hold in this panel.
	 */
	private final JPanel panelButtons;

	/**
	 * The background thread that processes training.
	 */
	private Thread thread;

	/**
	 * Has training been canceled.
	 */
	private boolean cancel;

	/**
	 * Panel used to display the current status of the training.
	 */
	protected TrainingStatusPanel statusPanel;

	/**
	 * Panel that holds the chart.
	 */
	protected ChartPane chartPanel;

	/**
	 * The training method being used.
	 */
	private Train train;

	/**
	 * The network being trained.
	 */
	private BasicNetwork network;

	private EncogCLDevice device;
	
	private BasicNetwork oldNetwork;

	/**
	 * The training data.
	 */
	private NeuralDataSet trainingData;

	/**
	 * The max allowed error.
	 */
	private double maxError;

	/**
	 * What iteration are we on.
	 */
	private int iteration;

	/**
	 * The font to use for headings.
	 */
	private Font headFont;

	/**
	 * The font for body text.
	 */
	private Font bodyFont;

	/**
	 * The current error.
	 */
	private double currentError;

	/**
	 * The last error.
	 */
	private double lastError;

	/**
	 * The error improvement.
	 */
	private double errorImprovement;

	/**
	 * When was training started.
	 */
	private Date started;

	/**
	 * When was the last update.
	 */
	private long lastUpdate;

	/**
	 * Number formatter.
	 */
	private final NumberFormat nf = NumberFormat.getInstance();

	/**
	 * Shorter number formatter.
	 */
	private final NumberFormat nfShort = NumberFormat.getInstance();

	/**
	 * The current performance count, how many iterations per minute.
	 */
	private int performanceCount;

	/**
	 * What time did we last take a performance sample.
	 */
	private Date performanceLast;

	/**
	 * What was the iteration number, the last time a performance sample was
	 * taken.
	 */
	private int performanceLastIteration;

	private String status;

	/**
	 * Should the dialog box exit? Are we waiting for training to shut down
	 * first.
	 */
	private boolean shouldExit;

	/**
	 * Construct the dialog box.
	 * 
	 * @param owner
	 *            The owner of the dialog box.
	 */
	public BasicTrainingProgress(final Frame owner) {
		super(null);

		this.buttonStart = new JButton("Start");
		this.buttonStop = new JButton("Stop");
		this.buttonClose = new JButton("Close");

		this.buttonStart.addActionListener(this);
		this.buttonStop.addActionListener(this);
		this.buttonClose.addActionListener(this);

		setLayout(new BorderLayout());
		this.panelBody = new JPanel();
		this.panelButtons = new JPanel();
		this.panelButtons.add(this.buttonStart);
		this.panelButtons.add(this.buttonStop);
		this.panelButtons.add(this.buttonClose);
		add(this.panelBody, BorderLayout.CENTER);
		add(this.panelButtons, BorderLayout.SOUTH);
		this.panelBody.setLayout(new BorderLayout());
		this.panelBody.add(this.statusPanel = new TrainingStatusPanel(this),
				BorderLayout.NORTH);
		this.panelBody.add(this.chartPanel = new ChartPane(),
				BorderLayout.CENTER);
		this.buttonStop.setEnabled(false);

		this.shouldExit = false;
		this.bodyFont = EncogFonts.getInstance().getBodyFont();
		this.headFont = EncogFonts.getInstance().getHeadFont();
		this.status = "Ready to Start";
	}

	private void performClose() {
		
		if (this.oldNetwork != null) {

			if (EncogWorkBench.askQuestion("Training",
					"Save the training to this network?")) {

				EncogWorkBench.getInstance().getCurrentFile().add(
						this.network.getName(), this.train.getNetwork());
				saveNetwork();

				if (this.train instanceof ResilientPropagation) {
					ResilientPropagation rprop = (ResilientPropagation) this.train;
					TrainingContinuation cont = rprop.pause();
					cont.setDescription("Training state from last RPROP.");
					EncogWorkBench.getInstance().getCurrentFile().add(
							this.network.getName() + "-rprop", cont);
					EncogWorkBench.getInstance().getMainWindow().redraw();
				}
			} else {
				EncogWorkBench.getInstance().getCurrentFile().add(
						this.network.getName(), this.oldNetwork);
			}
		}
	}

	/**
	 * Track button presses.
	 * 
	 * @param e
	 *            Event info.
	 */
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == this.buttonClose) {
			dispose();
		} else if (e.getSource() == this.buttonStart) {
			performStart();
		} else if (e.getSource() == this.buttonStop) {
			performStop();
		}
	}
	
	public boolean close()
	{
		if (this.thread == null) {
			performClose();
			return true;
		} else {
			this.shouldExit = true;
			this.cancel = true;
			return false;
		}		
	}

	/**
	 * @return the network
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * @return the train
	 */
	public Train getTrain() {
		return this.train;
	}

	/**
	 * @return the trainingData
	 */
	public NeuralDataSet getTrainingData() {
		return this.trainingData;
	}

	public abstract void iteration();

	public void paintStatus(final Graphics g) {
		g.setColor(Color.white);
		final int width = getWidth();
		final int height = getHeight();
		g.fillRect(0, 0, width, height);
		g.setColor(Color.black);
		g.setFont(this.headFont);
		final FontMetrics fm = g.getFontMetrics();
		int y = fm.getHeight();
		g.drawString("Iteration:", 10, y);
		y += fm.getHeight();
		g.drawString("Current Error:", 10, y);
		y += fm.getHeight();
		g.drawString("Error Improvement:", 10, y);

		y = fm.getHeight();
		g.drawString("Elapsed Time:", 400, y);
		y += fm.getHeight();
		g.drawString("Performance:", 400, y);

		y = fm.getHeight();
		g.setFont(this.bodyFont);
		String str = this.nf.format(this.iteration);

		str += " (" + this.status + ")";

		g.drawString(str, 150, y);
		y += fm.getHeight();
		g.drawString("" + 100.0 * this.currentError + "%", 150, y);
		y += fm.getHeight();
		g.drawString("" + 100.0 * this.errorImprovement + "%", 150, y);
		y = fm.getHeight();

		long seconds = 0;
		if (this.started != null) {
			final Date now = new Date();
			seconds = (now.getTime() - this.started.getTime()) / 1000;
		}
		g.drawString(TimeSpanFormatter.formatTime(seconds), 500, y);

		y += fm.getHeight();

		if (this.performanceCount == -1) {
			str = "  (calculating performance)";
		} else {
			final double d = this.performanceCount / 60.0;
			str = "  (" + this.nfShort.format(d) + "/sec)";
		}

		g.drawString(str, 500, y);

	}

	/**
	 * Start the training.
	 */
	private void performStart() {

		if (!EncogWorkBench.getInstance().getMainWindow().getTabManager()
				.checkTrainingOrNetworkOpen())
			return;

		this.started = new Date();
		this.performanceLast = this.started;
		this.performanceCount = -1;
		this.performanceLastIteration = 0;

		this.buttonStart.setEnabled(false);
		this.buttonStop.setEnabled(true);
		this.cancel = false;
		this.status = "Started";
		this.thread = new Thread(this);
		this.thread.start();
	}

	/**
	 * Request that the training stop.
	 */
	private void performStop() {
		this.status = "Canceled";
		this.cancel = true;
	}

	/**
	 * Repaint the dialog box.
	 */
	public void redraw() {
		this.statusPanel.repaint();
		this.lastUpdate = System.currentTimeMillis();
		this.chartPanel.addData(this.iteration, this.train.getError(),
				this.errorImprovement);
	}

	/**
	 * Process the background thread. Cycle through training iterations. If the
	 * cancel flag is set, then exit.
	 */
	public void run() {

		try {

			startup();

			this.oldNetwork = (BasicNetwork) network.clone();

			// see if we need to continue training.
			if (this.train instanceof ResilientPropagation) {
				ResilientPropagation rprop = (ResilientPropagation) this.train;
				TrainingContinuation state = (TrainingContinuation) EncogWorkBench
						.getInstance().getCurrentFile().find(
								this.network.getName() + "-rprop");
				if (state != null) {
					if (rprop.isValidResume(state))
						rprop.resume(state);
				}
				EncogWorkBench.getInstance().getCurrentFile().delete(
						this.network.getName() + "-rprop");
			}

			// connect to the cloud, if needed
			if (EncogWorkBench.getInstance().getCloud() != null) {
				train.setCloud(EncogWorkBench.getInstance().getCloud());
			}

			while (!this.cancel) {
				this.iteration++;
				this.lastError = this.train.getError();

				iteration();

				this.currentError = this.train.getError();

				if (this.currentError < this.maxError) {
					this.status = "Max Error Reached";
					this.cancel = true;
				}
				
				if( this.train.isTrainingDone() ) {
					this.status = "Training Complete";
					this.cancel = true;
				}

				this.errorImprovement = (this.lastError - this.currentError)
						/ this.lastError;
				if (System.currentTimeMillis() - this.lastUpdate > 1000
						|| this.cancel) {
					redraw();
				}

				final Date now = new Date();
				if (now.getTime() - this.performanceLast.getTime() > 60000) {
					this.performanceLast = now;
					this.performanceCount = this.iteration
							- this.performanceLastIteration;
					this.performanceLastIteration = this.iteration;
				}
			}
			this.train.finishTraining();
			shutdown();
			stopped();

			if (this.shouldExit) {
				dispose();
			}
		} catch (Throwable t) {
			EncogWorkBench.displayError("Error", t, this.network,
					this.trainingData);
			shutdown();
			stopped();
			dispose();
		}
	}

	/**
	 * @param maxError
	 *            the maxError to set
	 */
	public void setMaxError(final double maxError) {
		this.maxError = maxError;
	}

	/**
	 * @param network
	 *            the network to set
	 */
	public void setNetwork(final BasicNetwork network) {
		this.network = network;
	}

	/**
	 * @param train
	 *            the train to set
	 */
	public void setTrain(final Train train) {
		this.train = train;
	}

	/**
	 * @param trainingData
	 *            the trainingData to set
	 */
	public void setTrainingData(final NeuralDataSet trainingData) {
		this.trainingData = trainingData;
	}

	/**
	 * Implemented by subclasses to perform any shutdown after training.
	 */
	public abstract void shutdown();

	/**
	 * Implemented by subclasses to perform any activity before training.
	 */
	public abstract void startup();

	/**
	 * Called when training has stopped.
	 */
	private void stopped() {
		this.thread = null;
		this.buttonStart.setEnabled(true);
		this.buttonStop.setEnabled(false);
		this.cancel = true;
	}

	public void saveNetwork() {

	}

	/**
	 * @return the device
	 */
	public EncogCLDevice getDevice() {
		return device;
	}

	/**
	 * @param device the device to set
	 */
	public void setDevice(EncogCLDevice device) {
		this.device = device;
	}
	
	

}
