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
package org.encog.workbench.tabs.population;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

import org.encog.engine.util.Format;
import org.encog.neural.networks.training.genetic.NeuralGenome;
import org.encog.neural.networks.training.neat.NEATGenome;
import org.encog.solve.genetic.genome.Genome;
import org.encog.solve.genetic.population.Population;
import org.encog.workbench.WorkbenchFonts;

public class PopulationInfo extends JPanel {

	
	Population population;
	
	public PopulationInfo(Population population)
	{
		this.population = population;
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth()-1, getHeight()-1);
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		g.setFont(WorkbenchFonts.getTitle2Font());
		FontMetrics fm = g.getFontMetrics();
		int y = fm.getHeight();
		g.drawString("Maximum Population Count:", 20, y);
		g.drawString("Old Age Threshold:", 300, y);
		y+=fm.getHeight();
		g.drawString("Current Population Count:", 20, y);
		g.drawString("Old Age Penalty:", 300, y);
		y+=fm.getHeight();
		g.drawString("Species Count:", 20, y);
		g.drawString("Youth Age Threshold:", 300, y);
		y+=fm.getHeight();
		g.drawString("Innovation Count:", 20, y);
		g.drawString("Youth Bonus:", 300, y);
		y+=fm.getHeight();
		g.drawString("Population Type:", 20, y);
		g.drawString("Survival Rate:", 300, y);
		y+=fm.getHeight();
		g.drawString("Best Genome Score:", 20, y);
		
		int populationSize = 0;
		int speciesSize = 0;
		int innovationsSize = 0;
		double bestScore = 0;
		
		if( population.getBest()!=null)
			bestScore = population.getBest().getScore();
		
		if( population.getGenomes()!=null)
			populationSize = population.getGenomes().size();
		
		if( population.getSpecies()!=null)
			speciesSize = population.getSpecies().size();
		
		if( population.getInnovations()!=null )
			innovationsSize = population.getInnovations().getInnovations().size();
		
		String type = "Unknown";
		
		if( population.getGenomes().size()>0 )
		{
			Genome genome = population.getGenomes().get(0);
			if( genome instanceof NEATGenome )
			{
				type = "Neat";
			}
			else if( genome instanceof NeuralGenome )
			{
				type = "NON-NEAT Neural Network";
			}
		}
		
		y = fm.getHeight();
		g.setFont(WorkbenchFonts.getTextFont());
		g.drawString(Format.formatInteger(population.getPopulationSize()), 200, y);
		g.drawString(Format.formatInteger(population.getOldAgeThreshold()), 450, y);
		y+=fm.getHeight();
		g.drawString(Format.formatInteger(populationSize), 200, y);
		g.drawString(Format.formatPercent(population.getOldAgePenalty()), 450, y);
		y+=fm.getHeight();
		g.drawString(Format.formatInteger(speciesSize), 200, y);
		g.drawString(Format.formatInteger(population.getYoungBonusAgeThreshold()), 450, y);
		y+=fm.getHeight();
		g.drawString(Format.formatInteger(innovationsSize), 200, y);
		g.drawString(Format.formatPercent(population.getYoungScoreBonus()), 450, y);
		y+=fm.getHeight();
		g.drawString(type, 200, y);
		g.drawString(Format.formatPercent(population.getSurvivalRate()), 450, y);
		y+=fm.getHeight();
		g.drawString(Format.formatDouble(bestScore,2), 200, y);		
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(620,100);
	}
}
