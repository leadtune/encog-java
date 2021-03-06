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
package org.encog.workbench.process.generate;

import java.util.Map.Entry;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.ContextLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.synapse.DirectSynapse;
import org.encog.neural.networks.synapse.OneToOneSynapse;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.SynapseType;
import org.encog.neural.networks.synapse.WeightlessSynapse;

public class GenerateVB extends BasicGenerate {

	
	public String generate(final BasicNetwork network) {

		this.setNetwork(network);
		
		// the default Encog packages
		this.addPackage("Encog.Neural.Networks");
		this.addPackage("Encog.Neural.Networks.Layers");
		this.addPackage("Encog.Neural.Activation");
		this.addPackage("Encog.Neural.Networks.Synapse");

		this.addNewLine();
		this.addLine("' Neural Network file generated by Encog.  This file shows just a simple");
		this.addLine("' neural network generated for the structure designed in the workbench.");
		this.addLine("' Additional code will be needed for training and processing.");
		this.addLine("'");
		this.addLine("' http://www.encog.org");
		this.addLine("'");
		this.addNewLine();

		this.addNewLine();
		this.addLine("Module Module1");
		this.addNewLine();
		this.forwardIndent();

		generateMain();
		
		this.backwardIndent();
		this.addLine("End Module\n");

		final String importStr = generateUsing();

		return importStr + this.getSource().toString();
	}



	private String generateUsing() {
		final StringBuilder results = new StringBuilder();
		for (final String c : this.getImports()) {
			results.append("Imports ");
			results.append(c);
			results.append("\n");
		}
		return results.toString();
	}
	
	/**
	 * 
	 * @param previousLayer
	 * @param currentLayer
	 * @param synapse
	 */
	public void generateLayer(Layer previousLayer, Layer currentLayer, Synapse synapse)
	{		
		this.addNewLine();
		
		this.appendToLine("Dim ");
		this.appendToLine(nameLayer(currentLayer));
		this.appendToLine(" As ");
		
		if( currentLayer instanceof ContextLayer )
		{
			this.appendToLine(" ILayer = New ContextLayer( New ");
			this.appendToLine(currentLayer.getActivationFunction().getClass().getSimpleName());
			this.appendToLine("()");
			this.appendToLine(",");
			this.appendToLine(currentLayer.hasBias()?"true":"false");
			this.appendToLine(",");
			this.appendToLine(""+currentLayer.getNeuronCount());
			this.appendToLine(")");
		}
		else if( currentLayer instanceof RadialBasisFunctionLayer )
		{
			this.appendToLine(" RadialBasisFunctionLayer = New RadialBasisFunctionLayer(");
			this.appendToLine(""+currentLayer.getNeuronCount());
			this.appendToLine(");");
		}
		else if( currentLayer instanceof BasicLayer )
		{
			this.appendToLine(" BasicLayer = New BasicLayer( new ");
			this.appendToLine(currentLayer.getActivationFunction().getClass().getSimpleName());
			this.appendToLine("()");
			this.appendToLine(",");
			this.appendToLine(currentLayer.hasBias()?"true":"false");
			this.appendToLine(",");
			this.appendToLine(""+currentLayer.getNeuronCount());
			this.appendToLine(")");
		}
		
		this.addLine();
		
		if( previousLayer==null) {
			this.appendToLine("network.AddLayer(");
			this.appendToLine(nameLayer(currentLayer));
			this.appendToLine(")");
		}
		else
		{
			this.appendToLine(nameLayer(previousLayer));
			this.appendToLine(".AddNext(");
			this.appendToLine(nameLayer(currentLayer));
			
			if( synapse!=null )
			{
				if( synapse instanceof DirectSynapse )
				{
					this.appendToLine(",SynapseType.Direct");
				}
				else if( synapse instanceof OneToOneSynapse )
				{
					this.appendToLine(",SynapseType.OneToOne");
				}
				else if( synapse instanceof WeightlessSynapse )
				{
					this.appendToLine(",SynapseType.Weightless");
				}
			}

			this.appendToLine(")");
		}
		
		this.addLine();
		
		// next layers
		for(Synapse nextSynapse: currentLayer.getNext() )
		{
			Layer nextLayer = nextSynapse.getToLayer();
			if( this.getLayerMap().containsKey(nextLayer))
			{
				this.appendToLine(nameLayer(currentLayer));
				this.appendToLine(".AddNext(");
				this.appendToLine(nameLayer(nextLayer));
				this.appendToLine(")\n");
				this.addLine();
			}
			else
			{
				generateLayer( currentLayer, nextSynapse.getToLayer(),nextSynapse);
			}
		}

	}
	
	private void generateTags()
	{
		for(Entry<String,Layer> entry : this.getNetwork().getLayerTags().entrySet() )
		{
			String key = entry.getKey();
			Layer value = entry.getValue();
			String layerName = nameLayer(value);
			StringBuilder line = new StringBuilder();
			line.append("network.TagLayer(\"");
			line.append(key);
			line.append("\",");
			line.append(layerName);			
			line.append(")");	
			
			addLine(line.toString());
		}
	}

	public void generateMain() {
		this.addLine("Sub Main()");
		this.addNewLine();
		this.forwardIndent();

		this.addLine("Dim network as BasicNetwork = new BasicNetwork()");
	
		for( Layer layer: this.getNetwork().getLayerTags().values() )
		{
			if( !this.knownLayer(layer) ) {
				generateLayer(layer,layer,null);
			}
		}
				
		generateTags();
		
		this.addLine("network.Structure.FinalizeStructure()");
		this.addLine("network.Reset()");
		
		this.backwardIndent();
		this.addLine("End Sub");

	}
}
