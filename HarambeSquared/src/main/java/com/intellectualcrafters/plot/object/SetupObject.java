package com.intellectualcrafters.plot.object;

import com.intellectualcrafters.plot.config.ConfigurationNode;
import com.intellectualcrafters.plot.util.SetupUtils;

public class SetupObject {

	/**
	 * Specify a SetupUtils object here to override the existing
	 */
	public SetupUtils setupManager;

	/**
	 * The current state
	 */
	public int current;

	/**
	 * The index in generator specific settings
	 */
	public int setup_index;

	/**
	 * The name of the world
	 */
	public String world;

	/**
	 * The name of the plot manager
	 */
	public String plotManager;

	/**
	 * The name of the generator to use for world creation
	 */
	public String setupGenerator;

	/**
	 * The management type (normal, augmented, partial)
	 */
	public int type;

	/**
	 * The terrain type
	 */
	public int terrain;

	/**
	 * Area ID (may be null)
	 */
	public String id;

	/**
	 * Minimum plot id (may be null)
	 */
	public PlotId min;

	/**
	 * Max plot id (may be null)
	 */
	public PlotId max;

	/**
	 * Generator specific configuration steps
	 */
	public ConfigurationNode[] step;
}
