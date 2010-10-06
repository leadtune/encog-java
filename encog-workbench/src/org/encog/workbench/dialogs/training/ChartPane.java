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

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Simple pane that holds the chart of training.  This uses JFreeChart
 * @author jheaton
 *
 */
public class ChartPane extends JPanel {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Series 1 holds the current error.
	 */
	XYSeries series1;
	
	/**
	 * Series 2 holds the error improvement.
	 */
	XYSeries series2;
	
	/**
	 * The data set for the current error.
	 */
	XYSeriesCollection dataset1;
	
	/**
	 * The 
	 */
	XYSeriesCollection dataset2;
	
	/**
	 * The chart.
	 */
	JFreeChart chart;
	
	/**
	 * The panel that the chart is drawn upon.
	 */
	ChartPanel chartPanel;
	
	/**
	 * How many data points have been displayed.
	 */
	int count;

	/**
	 * Construct the pane.
	 */
	public ChartPane() {
		this.series1 = new XYSeries("Current Error");
		this.dataset1 = new XYSeriesCollection();
		this.dataset1.addSeries(this.series1);
		this.series1.setMaximumItemCount(50);

		this.series2 = new XYSeries("Error Improvement");
		this.dataset2 = new XYSeriesCollection();
		this.dataset2.addSeries(this.series2);
		this.series2.setMaximumItemCount(50);

		// addData(1,1,0.01);

		final JFreeChart chart = createChart();
		this.chartPanel = new ChartPanel(chart);
		this.chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
		this.chartPanel.setDomainZoomable(true);
		this.chartPanel.setRangeZoomable(true);
		setLayout(new BorderLayout());
		add(this.chartPanel, BorderLayout.CENTER);

	}

	/**
	 * Add a data point to the chart.
	 * @param iteration Which iteration is this.
	 * @param error What is the error at this point.
	 * @param improvement What is the error improvement from the last
	 * iteration.
	 */
	public void addData(final int iteration, final double error,
			final double improvement) {
		if (iteration > 10) {
			this.series1.add(iteration, error * 100.0);
			this.series2.add(iteration, improvement * 100.0);
		}

	}

	/**
	 * Create the initial chart.
	 * @return The chart.
	 */
	private JFreeChart createChart() {

		this.chart = ChartFactory.createXYLineChart(null, "Iteration",
				"Current Error", this.dataset1, PlotOrientation.VERTICAL, true,
				true, false);

		final XYPlot plot = (XYPlot) this.chart.getPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);

		plot.getRangeAxis().setFixedDimension(15.0);

		// AXIS 2
		final NumberAxis axis2 = new NumberAxis("Error Improvement");
		axis2.setFixedDimension(10.0);
		axis2.setAutoRangeIncludesZero(false);
		axis2.setLabelPaint(Color.red);
		axis2.setTickLabelPaint(Color.red);
		plot.setRangeAxis(1, axis2);
		plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);

		plot.setDataset(1, this.dataset2);
		plot.mapDatasetToRangeAxis(1, 1);
		final XYItemRenderer renderer2 = new StandardXYItemRenderer();
		renderer2.setSeriesPaint(0, Color.red);
		plot.setRenderer(1, renderer2);

		ChartUtilities.applyCurrentTheme(this.chart);

		return this.chart;
	}

}
