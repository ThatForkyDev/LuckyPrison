package com.intellectualcrafters.plot.object;

public interface PlotFilter {
	default boolean allowsArea(PlotArea area)
	{
		return true;
	}

	default boolean allowsPlot(Plot plot)
	{
		return true;
	}
}
