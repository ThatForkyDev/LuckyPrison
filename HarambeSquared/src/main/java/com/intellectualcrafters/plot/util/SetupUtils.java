package com.intellectualcrafters.plot.util;

import java.util.HashMap;

import com.intellectualcrafters.plot.generator.GeneratorWrapper;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.SetupObject;

public abstract class SetupUtils {

	public static SetupUtils manager;

	public static HashMap<String, GeneratorWrapper<?>> generators = new HashMap<>();

	public abstract void updateGenerators();

	public abstract String getGenerator(PlotArea plotArea);

	public abstract String setupWorld(SetupObject object);
}
