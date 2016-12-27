package com.intellectualcrafters.plot.object;

public interface LazyBlock {

	PlotBlock getPlotBlock();

	default int getId()
	{
		return this.getPlotBlock().id;
	}
}
