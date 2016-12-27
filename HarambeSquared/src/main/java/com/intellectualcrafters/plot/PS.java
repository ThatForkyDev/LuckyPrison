package com.intellectualcrafters.plot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.intellectualcrafters.configuration.ConfigurationSection;
import com.intellectualcrafters.configuration.MemorySection;
import com.intellectualcrafters.configuration.file.YamlConfiguration;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Config;
import com.intellectualcrafters.plot.config.Configuration;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.config.Storage;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.database.Database;
import com.intellectualcrafters.plot.database.SQLManager;
import com.intellectualcrafters.plot.generator.GeneratorWrapper;
import com.intellectualcrafters.plot.generator.HybridPlotWorld;
import com.intellectualcrafters.plot.generator.HybridUtils;
import com.intellectualcrafters.plot.generator.IndependentPlotGenerator;
import com.intellectualcrafters.plot.logger.DelegateLogger;
import com.intellectualcrafters.plot.logger.ILogger;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotCluster;
import com.intellectualcrafters.plot.object.PlotFilter;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotManager;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.object.StringWrapper;
import com.intellectualcrafters.plot.util.AbstractTitle;
import com.intellectualcrafters.plot.util.ChatManager;
import com.intellectualcrafters.plot.util.ChunkManager;
import com.intellectualcrafters.plot.util.CommentManager;
import com.intellectualcrafters.plot.util.EventUtil;
import com.intellectualcrafters.plot.util.InventoryUtil;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.ReflectionUtils;
import com.intellectualcrafters.plot.util.SchematicHandler;
import com.intellectualcrafters.plot.util.SetupUtils;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.TaskManager;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.WorldUtil;
import com.intellectualcrafters.plot.util.area.QuadMap;
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.intellectualcrafters.plot.util.expiry.ExpireManager;
import com.intellectualcrafters.plot.util.expiry.ExpiryTask;

/**
 * An implementation of the core, with a static getter for easy access.
 */
public class PS {
	private static PS instance;
	// Implementation
	public final IPlotMain IMP;
	// Implementation logger
	private ILogger logger;
	// Current thread
	private final Thread thread;
	// Platform / Version / Update URL
	private final String platform;
	private final int[] version;
	private int[] lastVersion;
	public URL update;
	// Files and configuration
	private File jarFile; // This file
	public File styleFile;
	public File configFile;
	public File worldsFile;
	public File commandsFile;
	public File translationFile;
	private File storageFile;
	public YamlConfiguration style;
	public YamlConfiguration config;
	public YamlConfiguration worlds;
	public YamlConfiguration storage;
	public YamlConfiguration commands;
	// Temporary hold the plots/clusters before the worlds load
	public HashMap<String, Set<PlotCluster>> clusters_tmp;
	public HashMap<String, HashMap<PlotId, Plot>> plots_tmp;
	// All plot areas
	private PlotArea[] plotAreas = new PlotArea[0];
	// All plot areas mapped by world
	private final HashMap<String, PlotArea[]> plotAreaMap = new HashMap<>();
	// All plot areas mapped by position
	private final HashMap<String, QuadMap<PlotArea>> plotAreaGrid = new HashMap<>();
	// Optimization if there are no hash collisions
	private boolean plotAreaHasCollision;
	private final HashSet<Integer> plotAreaHashCheck = new HashSet<>();

	/**
	 * Initialize PlotSquared with the desired Implementation class.
	 *
	 * @param iPlotMain Implementation of {@link IPlotMain} used
	 * @param platform  The platform being used
	 */
	public PS(IPlotMain iPlotMain, String platform)
	{
		instance = this;
		this.thread = Thread.currentThread();
		this.IMP = iPlotMain;
		this.logger = iPlotMain;
		this.platform = platform;
		this.version = this.IMP.getPluginVersion();
		try
		{
			new ReflectionUtils(this.IMP.getNMSPackage());
			try
			{
				URL url = PS.class.getProtectionDomain().getCodeSource().getLocation();
				this.jarFile = new File(new URL(url.toURI().toString().split("\\!")[0].replaceAll("jar:file", "file")).toURI().getPath());
			}
			catch (MalformedURLException | URISyntaxException | SecurityException e)
			{
				e.printStackTrace();
				this.jarFile = new File(this.IMP.getDirectory().getParentFile(), "PlotSquared.jar");
				if (!this.jarFile.exists())
				{
					this.jarFile = new File(this.IMP.getDirectory().getParentFile(), "PlotSquared-" + platform + ".jar");
				}
			}
			if (this.getJavaVersion() < 1.8)
			{
				log(C.CONSOLE_JAVA_OUTDATED_1_8);
			}
			TaskManager.IMP = this.IMP.getTaskManager();
			this.setupConfigs();
			this.translationFile =
					MainUtil.getFile(this.IMP.getDirectory(), Settings.Paths.TRANSLATIONS + File.separator + "PlotSquared.use_THIS.yml");
			C.load(this.translationFile);

			// Database
			if (Settings.Enabled_Components.DATABASE)
			{
				this.setupDatabase();
			}
			// Comments
			CommentManager.registerDefaultInboxes();
			// Kill entities
			if (Settings.Enabled_Components.KILL_ROAD_MOBS || Settings.Enabled_Components.KILL_ROAD_VEHICLES)
			{
				this.IMP.runEntityTask();
			}
			if (Settings.Enabled_Components.EVENTS)
			{
				this.IMP.registerPlayerEvents();
				this.IMP.registerInventoryEvents();
				this.IMP.registerPlotPlusEvents();
			}
			// Required
			this.IMP.registerWorldEvents();
			if (Settings.Enabled_Components.CHUNK_PROCESSOR)
			{
				this.IMP.registerChunkProcessor();
			}
			// create UUIDWrapper
			UUIDHandler.implementation = this.IMP.initUUIDHandler();
			if (Settings.Enabled_Components.UUID_CACHE)
			{
				this.startUuidCatching();
			}
			else
			{
				// Start these separately
				UUIDHandler.add(new StringWrapper("*"), DBFunc.everyone);
				this.startExpiryTasks();
				this.startPlotMeConversion();
			}
			// create event util class
			EventUtil.manager = this.IMP.initEventUtil();
			// create Hybrid utility class
			HybridUtils.manager = this.IMP.initHybridUtils();
			// Inventory utility class
			InventoryUtil.manager = this.IMP.initInventoryUtil();
			// create setup util class
			SetupUtils.manager = this.IMP.initSetupUtils();
			// World Util
			WorldUtil.IMP = this.IMP.initWorldUtil();
			// Set block
			GlobalBlockQueue.IMP = new GlobalBlockQueue(this.IMP.initBlockQueue(), 1);
			GlobalBlockQueue.IMP.runTask();
			// Set chunk
			ChunkManager.manager = this.IMP.initChunkManager();
			// Schematic handler
			SchematicHandler.manager = this.IMP.initSchematicHandler();
			// Titles
			AbstractTitle.TITLE_CLASS = this.IMP.initTitleManager();
			// Chat
			ChatManager.manager = this.IMP.initChatManager();
			// Commands
			if (Settings.Enabled_Components.COMMANDS)
			{
				this.IMP.registerCommands();
			}

			// World generators:
			ConfigurationSection section = this.worlds.getConfigurationSection("worlds");
			if (section != null)
			{
				for (String world : section.getKeys(false))
				{
					if ("CheckingPlotSquaredGenerator".equals(world))
					{
						continue;
					}
					if (WorldUtil.IMP.isWorld(world))
					{
						this.IMP.setGenerator(world);
					}
				}
				TaskManager.runTaskLater(() ->
										 {
											 for (String world : section.getKeys(false))
											 {
												 if ("CheckingPlotSquaredGenerator".equals(world))
												 {
													 continue;
												 }
												 if (!WorldUtil.IMP.isWorld(world))
												 {
													 debug("&c`" + world + "` was not properly loaded - PlotSquared will now try to load it properly: ");
													 debug(
															 "&8 - &7Are you trying to delete this world? Remember to remove it from the settings.yml, bukkit.yml and "
															 + "multiverse worlds.yml");
													 debug("&8 - &7Your world management plugin may be faulty (or non existent)");
													 this.IMP.setGenerator(world);
												 }
											 }
										 }, 1);
			}

			// Copy files
			this.copyFile("automerge.js", Settings.Paths.SCRIPTS);
			this.copyFile("town.template", Settings.Paths.TEMPLATES);
			this.copyFile("skyblock.template", Settings.Paths.TEMPLATES);
			this.copyFile("german.yml", Settings.Paths.TRANSLATIONS);
			this.copyFile("s_chinese_unescaped.yml", Settings.Paths.TRANSLATIONS);
			this.copyFile("s_chinese.yml", Settings.Paths.TRANSLATIONS);
			this.copyFile("italian.yml", Settings.Paths.TRANSLATIONS);
			this.showDebug();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		if (!C.ENABLED.s().isEmpty())
		{
			log(C.ENABLED);
		}
	}

	/**
	 * Get an instance of PlotSquared.
	 *
	 * @return instance of PlotSquared
	 */
	public static PS get()
	{
		return instance;
	}

	/**
	 * Log a message to the IPlotMain logger.
	 *
	 * @param message Message to log
	 * @see IPlotMain#log(String)
	 */
	public static void log(Object message)
	{
		get().logger.log(StringMan.getString(message));
	}

	/**
	 * Log a message to the IPlotMain logger.
	 *
	 * @param message Message to log
	 * @see IPlotMain#log(String)
	 */
	public static void debug(Object message)
	{
		if (Settings.DEBUG)
		{
			log(message);
		}
	}

	/**
	 * Get the current logger.
	 *
	 * @return The assigned logger
	 */
	public ILogger getLogger()
	{
		return this.logger;
	}

	/**
	 * Set the Logger.
	 *
	 * @param logger the logger the plugin should use
	 * @see DelegateLogger
	 * @see #getLogger()
	 */
	public void setLogger(ILogger logger)
	{
		this.logger = logger;
	}

	private void startUuidCatching()
	{
		TaskManager.runTaskLater(() ->
								 {
									 debug("Starting UUID caching");
									 UUIDHandler.startCaching(() ->
															  {
																  UUIDHandler.add(new StringWrapper("*"), DBFunc.everyone);
																  this.foreachPlotRaw(new RunnableVal<Plot>() {
																	  @Override
																	  public void run(Plot plot)
																	  {
																		  if (plot.hasOwner() && plot.temp != -1)
																		  {
																			  if (UUIDHandler.getName(plot.owner) == null)
																			  {
																				  UUIDHandler.implementation.unknown.add(plot.owner);
																			  }
																		  }
																	  }
																  });
																  this.startExpiryTasks();
																  this.startPlotMeConversion();
															  });
								 }, 20);
	}

	private void startExpiryTasks()
	{
		if (Settings.Enabled_Components.PLOT_EXPIRY)
		{
			ExpireManager.IMP = new ExpireManager();
			ExpireManager.IMP.runAutomatedTask();
			for (Settings.Auto_Clear settings : Settings.AUTO_CLEAR.getInstances())
			{
				ExpiryTask task = new ExpiryTask(settings);
				ExpireManager.IMP.addTask(task);
			}
		}
	}

	private void startPlotMeConversion()
	{
		if (Settings.Enabled_Components.PLOTME_CONVERTER || Settings.PlotMe.CACHE_UUDS)
		{
			TaskManager.runTaskLater(() ->
									 {
										 if (this.IMP.initPlotMeConverter())
										 {
											 log("&c=== IMPORTANT ===");
											 log("&cTHIS MESSAGE MAY BE EXTREMELY HELPFUL IF YOU HAVE TROUBLE CONVERTING PlotMe!");
											 log("&c - Make sure 'UUID.read-from-disk' is disabled (false)!");
											 log("&c - Sometimes the database can be locked, deleting PlotMe.jar beforehand will fix the issue!");
											 log("&c - After the conversion is finished, please set 'plotme-convert.enabled' to false in the "
													+ "'settings.yml'");
										 }
									 }, 20);
		}
	}

	public boolean isMainThread(Thread thread)
	{
		return this.thread == thread;
	}

	/**
	 * Check if `version` is >= `version2`.
	 *
	 * @param version
	 * @param version2
	 * @return true if `version` is >= `version2`
	 */
	public boolean checkVersion(int[] version, int... version2)
	{
		return version[0] > version2[0] || version[0] == version2[0] && version[1] > version2[1] || version[0] == version2[0]
																									&& version[1] == version2[1] && version[2] >= version2[2];
	}

	/**
	 * Get the last PlotSquared version.
	 *
	 * @return last version in config or null
	 */
	public int[] getLastVersion()
	{
		return this.lastVersion;
	}

	/**
	 * Get the current PlotSquared version.
	 *
	 * @return current version in config or null
	 */
	public int[] getVersion()
	{
		return this.version;
	}

	/**
	 * Get the server platform this plugin is running on this is running on.
	 * <p>
	 * <p>This will be either <b>Bukkit</b> or <b>Sponge</b></p>
	 *
	 * @return the server implementation
	 */
	public String getPlatform()
	{
		return this.platform;
	}

	/**
	 * Get the relevant plot area for a specified location.
	 * <ul>
	 * <li>If there is only one plot area globally that will be returned.
	 * <li>If there is only one plot area in the world, it will return that.
	 * <li>If the plot area for a location cannot be unambiguously
	 * resolved, null will be returned.
	 * </ul>
	 * Note: An applicable plot area may not include the location i.e. clusters
	 *
	 * @param location the location
	 * @return
	 */
	public PlotArea getApplicablePlotArea(Location location)
	{
		switch (this.plotAreas.length)
		{
			case 0:
				return null;
			case 1:
				return this.plotAreas[0];
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				String world = location.getWorld();
				int hash = world.hashCode();
				for (PlotArea area : this.plotAreas)
				{
					if (hash == area.worldhash)
					{
						if (area.contains(location.getX(), location.getZ()) && (!this.plotAreaHasCollision || world.equals(area.worldname)))
						{
							return area;
						}
					}
				}
				return null;
			default:
				PlotArea[] areas = this.plotAreaMap.get(location.getWorld());
				if (areas == null)
				{
					return null;
				}
				int y;
				int x;
				switch (areas.length)
				{
					case 1:
						return areas[0];
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						x = location.getX();
						y = location.getY();
						for (PlotArea area : areas)
						{
							if (area.contains(x, y))
							{
								return area;
							}
						}
						return null;
					default:
						QuadMap<PlotArea> search = this.plotAreaGrid.get(location.getWorld());
						return search.get(location.getX(), location.getZ());
				}
		}
	}

	public PlotArea getPlotArea(String world, String id)
	{
		PlotArea[] areas = this.plotAreaMap.get(world);
		if (areas == null)
		{
			return null;
		}
		if (areas.length == 1)
		{
			return areas[0];
		}
		else if (id == null)
		{
			return null;
		}
		for (PlotArea area : areas)
		{
			if (StringMan.isEqual(id, area.id))
			{
				return area;
			}
		}
		return null;
	}

	public PlotArea getPlotAreaAbs(String world, String id)
	{
		PlotArea[] areas = this.plotAreaMap.get(world);
		if (areas == null)
		{
			return null;
		}
		for (PlotArea area : areas)
		{
			if (StringMan.isEqual(id, area.id))
			{
				return area;
			}
		}
		return null;
	}

	/**
	 * Get the {@code PlotArea} which contains a location.
	 * <ul>
	 * <li>If the plot area does not contain a location, null
	 * will be returned.
	 * </ul>
	 *
	 * @param location the location
	 * @return the {@link PlotArea} in the location, null if non existent
	 */
	public PlotArea getPlotAreaAbs(Location location)
	{
		switch (this.plotAreas.length)
		{
			case 0:
				return null;
			case 1:
				PlotArea pa = this.plotAreas[0];
				return pa.contains(location) ? pa : null;
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				String world = location.getWorld();
				int hash = world.hashCode();
				for (PlotArea area : this.plotAreas)
				{
					if (hash == area.worldhash)
					{
						if (area.contains(location.getX(), location.getZ()) && (!this.plotAreaHasCollision || world.equals(area.worldname)))
						{
							return area;
						}
					}
				}
				return null;
			default:
				PlotArea[] areas = this.plotAreaMap.get(location.getWorld());
				if (areas == null)
				{
					return null;
				}
				int x;
				int y;
				switch (areas.length)
				{
					case 0:
						PlotArea a = areas[0];
						return a.contains(location.getX(), location.getZ()) ? a : null;
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
						x = location.getX();
						y = location.getY();
						for (PlotArea area : areas)
						{
							if (area.contains(x, y))
							{
								return area;
							}
						}
						return null;
					default:
						QuadMap<PlotArea> search = this.plotAreaGrid.get(location.getWorld());
						return search.get(location.getX(), location.getZ());
				}
		}
	}

	public PlotArea getPlotAreaByString(String search)
	{
		String[] split = search.split(";|,");
		PlotArea[] areas = this.plotAreaMap.get(split[0]);
		if (areas == null)
		{
			for (PlotArea area : this.plotAreas)
			{
				if (area.worldname.equalsIgnoreCase(split[0]))
				{
					if (area.id == null || split.length == 2 && area.id.equalsIgnoreCase(split[1]))
					{
						return area;
					}
				}
			}
			return null;
		}
		if (areas.length == 1)
		{
			return areas[0];
		}
		else if (split.length == 1)
		{
			return null;
		}
		else
		{
			for (PlotArea area : areas)
			{
				if (StringMan.isEqual(split[1], area.id))
				{
					return area;
				}
			}
			return null;
		}
	}

	public Set<PlotArea> getPlotAreas(String world, RegionWrapper region)
	{
		QuadMap<PlotArea> areas = this.plotAreaGrid.get(world);
		return areas != null ? areas.get(region) : new HashSet<>();
	}

	public PlotManager getPlotManager(Plot plot)
	{
		return plot.getArea().manager;
	}

	public PlotManager getPlotManager(Location location)
	{
		PlotArea pa = this.getPlotAreaAbs(location);
		return pa != null ? pa.manager : null;
	}

	/**
	 * Add a global reference to a plot world.
	 *
	 * @param plotArea the {@code PlotArea} to add.
	 * @see #removePlotArea(PlotArea) To remove the reference
	 */
	public void addPlotArea(PlotArea plotArea)
	{
		HashMap<PlotId, Plot> plots;
		if (this.plots_tmp == null || (plots = this.plots_tmp.remove(plotArea.toString())) == null)
		{
			if (plotArea.TYPE == 2)
			{
				plots = this.plots_tmp.get(plotArea.worldname);
				if (plots != null)
				{
					Iterator<Map.Entry<PlotId, Plot>> iterator = plots.entrySet().iterator();
					while (iterator.hasNext())
					{
						Map.Entry<PlotId, Plot> next = iterator.next();
						PlotId id = next.getKey();
						if (plotArea.contains(id))
						{
							next.getValue().setArea(plotArea);
							iterator.remove();
						}
					}
				}
			}
		}
		else
		{
			for (Plot entry : plots.values())
			{
				entry.setArea(plotArea);
			}
		}
		Set<PlotCluster> clusters;
		if (this.clusters_tmp == null || (clusters = this.clusters_tmp.remove(plotArea.toString())) == null)
		{
			if (plotArea.TYPE == 2)
			{
				clusters = this.clusters_tmp.get(plotArea.worldname);
				if (clusters != null)
				{
					Iterator<PlotCluster> iterator = clusters.iterator();
					while (iterator.hasNext())
					{
						PlotCluster next = iterator.next();
						if (next.intersects(plotArea.getMin(), plotArea.getMax()))
						{
							next.setArea(plotArea);
							iterator.remove();
						}
					}
				}
			}
		}
		else
		{
			for (PlotCluster cluster : clusters)
			{
				cluster.setArea(plotArea);
			}
		}
		Set<PlotArea> localAreas = this.getPlotAreas(plotArea.worldname);
		Set<PlotArea> globalAreas = this.getPlotAreas();
		localAreas.add(plotArea);
		globalAreas.add(plotArea);
		this.plotAreas = globalAreas.toArray(new PlotArea[globalAreas.size()]);
		this.plotAreaMap.put(plotArea.worldname, localAreas.toArray(new PlotArea[localAreas.size()]));
		QuadMap<PlotArea> map = this.plotAreaGrid.get(plotArea.worldname);
		if (map == null)
		{
			map = new QuadMap<PlotArea>(Integer.MAX_VALUE, 0, 0) {
				@Override
				public RegionWrapper getRegion(PlotArea value)
				{
					return value.getRegion();
				}
			};
			this.plotAreaGrid.put(plotArea.worldname, map);
		}
		map.add(plotArea);
		plotArea.setupBorder();
	}

	/**
	 * Remove a plot world reference.
	 *
	 * @param area the {@code PlotArea} to remove
	 */
	public void removePlotArea(PlotArea area)
	{
		Set<PlotArea> areas = this.getPlotAreas(area.worldname);
		areas.remove(area);
		this.plotAreas = areas.toArray(new PlotArea[areas.size()]);
		if (areas.isEmpty())
		{
			this.plotAreaMap.remove(area.worldname);
			this.plotAreaGrid.remove(area.worldname);
		}
		else
		{
			this.plotAreaMap.put(area.worldname, areas.toArray(new PlotArea[areas.size()]));
			this.plotAreaGrid.get(area.worldname).remove(area);
		}
		this.setPlotsTmp(area);
	}

	public void removePlotAreas(String world)
	{
		for (PlotArea area : this.getPlotAreas(world))
		{
			this.removePlotArea(area);
		}
	}

	private void setPlotsTmp(PlotArea area)
	{
		if (this.plots_tmp == null)
		{
			this.plots_tmp = new HashMap<>();
		}
		HashMap<PlotId, Plot> map = this.plots_tmp.get(area.toString());
		if (map == null)
		{
			map = new HashMap<>();
			this.plots_tmp.put(area.toString(), map);
		}
		for (Plot plot : area.getPlots())
		{
			map.put(plot.getId(), plot);
		}
		if (this.clusters_tmp == null)
		{
			this.clusters_tmp = new HashMap<>();
		}
		this.clusters_tmp.put(area.toString(), area.getClusters());
	}

	public Set<PlotCluster> getClusters(String world)
	{
		HashSet<PlotCluster> set = new HashSet<>();
		for (PlotArea area : this.getPlotAreas(world))
		{
			set.addAll(area.getClusters());
		}
		return set;
	}

	/**
	 * Get all the base plots in a single set (for merged plots it just returns
	 * the bottom plot).
	 *
	 * @return Set of base Plots
	 */
	public Set<Plot> getBasePlots()
	{
		int size = this.getPlotCount();
		Set<Plot> result = new HashSet<>(size);
		this.foreachPlotArea(new RunnableVal<PlotArea>() {
			@Override
			public void run(PlotArea value)
			{
				for (Plot plot : value.getPlots())
				{
					if (!plot.isBasePlot())
					{
						continue;
					}
					result.add(plot);
				}
			}
		});
		return result;
	}

	public List<Plot> sortPlotsByTemp(Collection<Plot> plots)
	{
		int max = 0;
		int overflowCount = 0;
		for (Plot plot : plots)
		{
			if (plot.temp > 0)
			{
				if (plot.temp > max)
				{
					max = plot.temp;
				}
			}
			else
			{
				overflowCount++;
			}
		}
		Plot[] array = new Plot[max + 1];
		List<Plot> overflow = new ArrayList<>(overflowCount);
		for (Plot plot : plots)
		{
			if (plot.temp <= 0)
			{
				overflow.add(plot);
			}
			else
			{
				array[plot.temp] = plot;
			}
		}
		ArrayList<Plot> result = new ArrayList<>(plots.size());
		for (Plot plot : array)
		{
			if (plot != null)
			{
				result.add(plot);
			}
		}
		Collections.sort(overflow, (a, b) -> a.hashCode() - b.hashCode());
		result.addAll(overflow);
		return result;
	}

	/**
	 * Sort plots by hashcode.
	 *
	 * @param plots the collection of plots to sort
	 * @return the sorted collection
	 * @deprecated Unchecked, please use
	 * {@link #sortPlots(Collection, SortType, PlotArea)} which has
	 * additional checks before calling this
	 */
	@Deprecated
	public ArrayList<Plot> sortPlotsByHash(Collection<Plot> plots)
	{
		int hardmax = 256000;
		int max = 0;
		int overflowSize = 0;
		for (Plot plot : plots)
		{
			int hash = MathMan.getPositiveId(plot.hashCode());
			if (hash > max)
			{
				if (hash >= hardmax)
				{
					overflowSize++;
				}
				else
				{
					max = hash;
				}
			}
		}
		hardmax = Math.min(hardmax, max);
		Plot[] cache = new Plot[hardmax + 1];
		List<Plot> overflow = new ArrayList<>(overflowSize);
		ArrayList<Plot> extra = new ArrayList<>();
		for (Plot plot : plots)
		{
			int hash = MathMan.getPositiveId(plot.hashCode());
			if (hash < hardmax)
			{
				if (hash >= 0)
				{
					cache[hash] = plot;
				}
				else
				{
					extra.add(plot);
				}
			}
			else if (Math.abs(plot.getId().x) > 15446 || Math.abs(plot.getId().y) > 15446)
			{
				extra.add(plot);
			}
			else
			{
				overflow.add(plot);
			}
		}
		Plot[] overflowArray = overflow.toArray(new Plot[overflow.size()]);
		this.sortPlotsByHash(overflowArray);
		ArrayList<Plot> result = new ArrayList<>(cache.length + overflowArray.length);
		for (Plot plot : cache)
		{
			if (plot != null)
			{
				result.add(plot);
			}
		}
		Collections.addAll(result, overflowArray);
		for (Plot plot : extra)
		{
			result.add(plot);
		}
		return result;
	}

	/**
	 * Unchecked, use {@link #sortPlots(Collection, SortType, PlotArea)} instead which will in turn call this.
	 *
	 * @param input an array of plots to sort
	 */
	@Deprecated
	public void sortPlotsByHash(Plot[] input)
	{
		List<Plot>[] bucket = new ArrayList[32];
		for (int i = 0; i < bucket.length; i++)
		{
			bucket[i] = new ArrayList<>();
		}
		boolean maxLength = false;
		int placement = 1;
		while (!maxLength)
		{
			maxLength = true;
			for (Plot i : input)
			{
				int tmp = MathMan.getPositiveId(i.hashCode()) / placement;
				bucket[tmp & 31].add(i);
				if (maxLength && tmp > 0)
				{
					maxLength = false;
				}
			}
			int a = 0;
			for (int b = 0; b < 32; b++)
			{
				for (Plot i : bucket[b])
				{
					input[a++] = i;
				}
				bucket[b].clear();
			}
			placement *= 32;
		}
	}

	@Deprecated
	public ArrayList<Plot> sortPlotsByTimestamp(Collection<Plot> plots)
	{
		int hardMax = 256000;
		int max = 0;
		int overflowSize = 0;
		for (Plot plot : plots)
		{
			int hash = MathMan.getPositiveId(plot.hashCode());
			if (hash > max)
			{
				if (hash >= hardMax)
				{
					overflowSize++;
				}
				else
				{
					max = hash;
				}
			}
		}
		hardMax = Math.min(hardMax, max);
		Plot[] cache = new Plot[hardMax + 1];
		List<Plot> overflow = new ArrayList<>(overflowSize);
		ArrayList<Plot> extra = new ArrayList<>();
		for (Plot plot : plots)
		{
			int hash = MathMan.getPositiveId(plot.hashCode());
			if (hash < hardMax)
			{
				if (hash >= 0)
				{
					cache[hash] = plot;
				}
				else
				{
					extra.add(plot);
				}
			}
			else if (Math.abs(plot.getId().x) > 15446 || Math.abs(plot.getId().y) > 15446)
			{
				extra.add(plot);
			}
			else
			{
				overflow.add(plot);
			}
		}
		Plot[] overflowArray = overflow.toArray(new Plot[overflow.size()]);
		this.sortPlotsByHash(overflowArray);
		ArrayList<Plot> result = new ArrayList<>(cache.length + overflowArray.length);
		for (Plot plot : cache)
		{
			if (plot != null)
			{
				result.add(plot);
			}
		}
		Collections.addAll(result, overflowArray);
		for (Plot plot : extra)
		{
			result.add(plot);
		}
		return result;
	}

	/**
	 * Sort plots by creation timestamp.
	 *
	 * @param input
	 * @return
	 * @deprecated Unchecked, use {@link #sortPlots(Collection, SortType, PlotArea)} instead which will call this after checks
	 */
	@Deprecated
	public List<Plot> sortPlotsByModified(Collection<Plot> input)
	{
		List<Plot> list = input instanceof List ? (List<Plot>) input : new ArrayList<>(input);
		Collections.sort(list, (a, b) -> Long.compare(ExpireManager.IMP.getTimestamp(a.owner), ExpireManager.IMP.getTimestamp(b.owner)));
		return list;
	}

	/**
	 * Sort a collection of plots by world (with a priority world), then
	 * by hashcode.
	 *
	 * @param plots        the plots to sort
	 * @param type         The sorting method to use for each world (timestamp, or hash)
	 * @param priorityArea Use null, "world", or "gibberish" if you
	 *                     want default world order
	 * @return ArrayList of plot
	 */
	public ArrayList<Plot> sortPlots(Collection<Plot> plots, SortType type, PlotArea priorityArea)
	{
		// group by world
		// sort each
		HashMap<PlotArea, Collection<Plot>> map = new HashMap<>();
		int totalSize = this.getPlotCount();
		if (plots.size() == totalSize)
		{
			for (PlotArea area : this.plotAreas)
			{
				map.put(area, area.getPlots());
			}
		}
		else
		{
			for (PlotArea area : this.plotAreas)
			{
				map.put(area, new ArrayList<>(0));
			}
			Collection<Plot> lastList = null;
			PlotArea lastWorld = null;
			for (Plot plot : plots)
			{
				if (lastWorld == plot.getArea())
				{
					lastList.add(plot);
				}
				else
				{
					lastWorld = plot.getArea();
					lastList = map.get(lastWorld);
					lastList.add(plot);
				}
			}
		}
		List<PlotArea> areas = Arrays.asList(this.plotAreas);
		Collections.sort(areas, (a, b) ->
		{
			if (priorityArea != null && StringMan.isEqual(a.toString(), b.toString()))
			{
				return -1;
			}
			return a.hashCode() - b.hashCode();
		});
		ArrayList<Plot> toReturn = new ArrayList<>(plots.size());
		for (PlotArea area : areas)
		{
			switch (type)
			{
				case CREATION_DATE:
					toReturn.addAll(this.sortPlotsByTemp(map.get(area)));
					break;
				case CREATION_DATE_TIMESTAMP:
					toReturn.addAll(this.sortPlotsByTimestamp(map.get(area)));
					break;
				case DISTANCE_FROM_ORIGIN:
					toReturn.addAll(this.sortPlotsByHash(map.get(area)));
					break;
				case LAST_MODIFIED:
					toReturn.addAll(this.sortPlotsByModified(map.get(area)));
					break;
				default:
					break;
			}
		}
		return toReturn;
	}

	/**
	 * A more generic way to filter plots - make your own method
	 * if you need complex filters.
	 *
	 * @param filters the filter
	 * @return a filtered set of plots
	 */
	public Set<Plot> getPlots(PlotFilter... filters)
	{
		HashSet<Plot> set = new HashSet<>();
		this.foreachPlotArea(new RunnableVal<PlotArea>() {
			@Override
			public void run(PlotArea value)
			{
				for (PlotFilter filter : filters)
				{
					if (!filter.allowsArea(value))
					{
						return;
					}
				}
				loop:
				for (Map.Entry<PlotId, Plot> entry2 : value.getPlotEntries())
				{
					Plot plot = entry2.getValue();
					for (PlotFilter filter : filters)
					{
						if (!filter.allowsPlot(plot))
						{
							continue loop;
						}
					}
					set.add(plot);
				}
			}
		});
		return set;
	}

	/**
	 * Get all the plots in a single set.
	 *
	 * @return Set of Plots
	 */
	public Set<Plot> getPlots()
	{
		int size = this.getPlotCount();
		Set<Plot> result = new HashSet<>(size);
		this.foreachPlotArea(new RunnableVal<PlotArea>() {
			@Override
			public void run(PlotArea value)
			{
				result.addAll(value.getPlots());
			}
		});
		return result;
	}

	public void setPlots(HashMap<String, HashMap<PlotId, Plot>> plots)
	{
		if (this.plots_tmp == null)
		{
			this.plots_tmp = new HashMap<>();
		}
		for (Map.Entry<String, HashMap<PlotId, Plot>> entry : plots.entrySet())
		{
			String world = entry.getKey();
			PlotArea area = this.getPlotArea(world, null);
			if (area == null)
			{
				HashMap<PlotId, Plot> map = this.plots_tmp.get(world);
				if (map == null)
				{
					map = new HashMap<>();
					this.plots_tmp.put(world, map);
				}
				map.putAll(entry.getValue());
			}
			else
			{
				for (Plot plot : entry.getValue().values())
				{
					plot.setArea(area);
					area.addPlot(plot);
				}
			}
		}
	}

	/**
	 * Get all the plots owned by a player name.
	 *
	 * @param world  the world
	 * @param player the plot owner
	 * @return Set of Plot
	 */
	public Set<Plot> getPlots(String world, String player)
	{
		UUID uuid = UUIDHandler.getUUID(player, null);
		return this.getPlots(world, uuid);
	}

	/**
	 * Get all the plots owned by a player name.
	 *
	 * @param area   the PlotArea
	 * @param player the plot owner
	 * @return Set of Plot
	 */
	public Set<Plot> getPlots(PlotArea area, String player)
	{
		UUID uuid = UUIDHandler.getUUID(player, null);
		return this.getPlots(area, uuid);
	}

	/**
	 * Get all plots by a PlotPlayer.
	 *
	 * @param world  the world
	 * @param player the plot owner
	 * @return Set of plot
	 */
	public Set<Plot> getPlots(String world, PlotPlayer player)
	{
		return this.getPlots(world, player.getUUID());
	}

	/**
	 * Get all plots by a PlotPlayer.
	 *
	 * @param area   the PlotArea
	 * @param player the plot owner
	 * @return Set of plot
	 */
	public Set<Plot> getPlots(PlotArea area, PlotPlayer player)
	{
		return this.getPlots(area, player.getUUID());
	}

	/**
	 * Get all plots by a UUID in a world.
	 *
	 * @param world the world
	 * @param uuid  the plot owner
	 * @return Set of plot
	 */
	public Set<Plot> getPlots(String world, UUID uuid)
	{
		ArrayList<Plot> myPlots = new ArrayList<>();
		for (Plot plot : this.getPlots(world))
		{
			if (plot.hasOwner())
			{
				if (plot.isOwnerAbs(uuid))
				{
					myPlots.add(plot);
				}
			}
		}
		return new HashSet<>(myPlots);
	}

	/**
	 * Get all plots by a UUID in an area.
	 *
	 * @param area the {@code PlotArea}
	 * @param uuid the plot owner
	 * @return Set of plot
	 */
	public Set<Plot> getPlots(PlotArea area, UUID uuid)
	{
		ArrayList<Plot> myplots = new ArrayList<>();
		for (Plot plot : this.getPlots(area))
		{
			if (plot.hasOwner())
			{
				if (plot.isOwnerAbs(uuid))
				{
					myplots.add(plot);
				}
			}
		}
		return new HashSet<>(myplots);
	}

	/**
	 * Use {@link #hasPlotArea(String)}.
	 * Note: Worlds may have more than one plot area
	 *
	 * @param world the world
	 * @return if the world is a plotworld
	 * @deprecated
	 */
	@Deprecated
	public boolean isPlotWorld(String world)
	{
		return this.plotAreaMap.containsKey(world);
	}

	/**
	 * Check if a plot world.
	 *
	 * @param world the world
	 * @return if a plot world is registered
	 * @see #getPlotAreaByString(String) to get the PlotArea object
	 */
	public boolean hasPlotArea(String world)
	{
		switch (this.plotAreas.length)
		{
			case 0:
				return false;
			case 1:
				PlotArea a = this.plotAreas[0];
				return world.hashCode() == a.worldhash && (!this.plotAreaHasCollision || a.worldname.equals(world));
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				int hash = world.hashCode();
				for (PlotArea area : this.plotAreas)
				{
					if (area.worldhash == hash && (!this.plotAreaHasCollision || area.worldname.equals(world)))
					{
						return true;
					}
				}
				return false;
			default:
				return this.plotAreaMap.containsKey(world);
		}
	}

	public Collection<Plot> getPlots(String world)
	{
		HashSet<Plot> set = new HashSet<>();
		this.foreachPlotArea(world, new RunnableVal<PlotArea>() {
			@Override
			public void run(PlotArea value)
			{
				set.addAll(value.getPlots());
			}
		});
		return set;
	}

	/**
	 * Get the plots for a PlotPlayer.
	 *
	 * @param player the player to retrieve the plots for
	 * @return Set of Plot
	 */
	public Set<Plot> getPlots(PlotPlayer player)
	{
		return this.getPlots(player.getUUID());
	}

	public Collection<Plot> getPlots(PlotArea area)
	{
		return area == null ? new HashSet<>() : area.getPlots();
	}

	public Plot getPlot(PlotArea area, PlotId id)
	{
		return area == null ? null : id == null ? null : area.getPlot(id);
	}

	public Set<Plot> getBasePlots(PlotPlayer player)
	{
		return this.getBasePlots(player.getUUID());
	}

	/**
	 * Get the plots for a UUID.
	 *
	 * @param uuid the plot owner
	 * @return Set of Plot's owned by the player
	 */
	public Set<Plot> getPlots(UUID uuid)
	{
		ArrayList<Plot> myPlots = new ArrayList<>();
		this.foreachPlot(new RunnableVal<Plot>() {
			@Override
			public void run(Plot value)
			{
				if (value.isOwnerAbs(uuid))
				{
					myPlots.add(value);
				}
			}
		});
		return new HashSet<>(myPlots);
	}

	public Set<Plot> getBasePlots(UUID uuid)
	{
		ArrayList<Plot> myplots = new ArrayList<>();
		this.foreachBasePlot(new RunnableVal<Plot>() {
			@Override
			public void run(Plot value)
			{
				if (value.isOwner(uuid))
				{
					myplots.add(value);
				}
			}
		});
		return new HashSet<>(myplots);
	}

	/**
	 * Get the plots for a UUID.
	 *
	 * @param uuid the UUID of the owner
	 * @return Set of Plot
	 */
	public Set<Plot> getPlotsAbs(UUID uuid)
	{
		ArrayList<Plot> myPlots = new ArrayList<>();
		this.foreachPlot(new RunnableVal<Plot>() {
			@Override
			public void run(Plot value)
			{
				if (value.isOwnerAbs(uuid))
				{
					myPlots.add(value);
				}
			}
		});
		return new HashSet<>(myPlots);
	}

	/**
	 * Unregister a plot from local memory (does not call DB).
	 *
	 * @param plot      the plot to remove
	 * @param callEvent If to call an event about the plot being removed
	 * @return true if plot existed | false if it didn't
	 */
	public boolean removePlot(Plot plot, boolean callEvent)
	{
		if (plot == null)
		{
			return false;
		}
		if (callEvent)
		{
			EventUtil.manager.callDelete(plot);
		}
		if (plot.getArea().removePlot(plot.getId()))
		{
			PlotId last = (PlotId) plot.getArea().getMeta("lastPlot");
			int last_max = Math.max(Math.abs(last.x), Math.abs(last.y));
			int this_max = Math.max(Math.abs(plot.getId().x), Math.abs(plot.getId().y));
			if (this_max < last_max)
			{
				plot.getArea().setMeta("lastPlot", plot.getId());
			}
			return true;
		}
		return false;
	}

	/**
	 * This method is called by the PlotGenerator class normally.
	 * <ul>
	 * <li>Initializes the PlotArea and PlotManager classes
	 * <li>Registers the PlotArea and PlotManager classes
	 * <li>Loads (and/or generates) the PlotArea configuration
	 * <li>Sets up the world border if configured
	 * </ul>
	 * <p>
	 * <p>If loading an augmented plot world:
	 * <ul>
	 * <li>Creates the AugmentedPopulator classes
	 * <li>Injects the AugmentedPopulator classes if required
	 * </ul>
	 *
	 * @param world         the world to load
	 * @param baseGenerator The generator for that world, or null
	 */
	public void loadWorld(String world, GeneratorWrapper<?> baseGenerator)
	{
		if ("CheckingPlotSquaredGenerator".equals(world))
		{
			return;
		}
		if (!this.plotAreaHasCollision && !this.plotAreaHashCheck.add(world.hashCode()))
		{
			this.plotAreaHasCollision = true;
		}
		Set<String> worlds = this.worlds.contains("worlds") ? this.worlds.getConfigurationSection("worlds").getKeys(false) : new HashSet<>();
		String path = "worlds." + world;
		ConfigurationSection worldSection = this.worlds.getConfigurationSection(path);
		int type = worldSection != null ? worldSection.getInt("generator.type", 0) : 0;
		if (type == 0)
		{
			if (this.plotAreaMap.containsKey(world))
			{
				debug("World possibly already loaded: " + world);
				return;
			}
			IndependentPlotGenerator plotGenerator;
			if (baseGenerator != null && baseGenerator.isFull())
			{
				plotGenerator = baseGenerator.getPlotGenerator();
			}
			else if (worldSection != null)
			{
				String secondaryGeneratorName = worldSection.getString("generator.plugin");
				GeneratorWrapper<?> secondaryGenerator = this.IMP.getGenerator(world, secondaryGeneratorName);
				if (secondaryGenerator != null && secondaryGenerator.isFull())
				{
					plotGenerator = secondaryGenerator.getPlotGenerator();
				}
				else
				{
					String primaryGeneratorName = worldSection.getString("generator.init");
					GeneratorWrapper<?> primaryGenerator = this.IMP.getGenerator(world, primaryGeneratorName);
					if (primaryGenerator != null && primaryGenerator.isFull())
					{
						plotGenerator = primaryGenerator.getPlotGenerator();
					}
					else
					{
						return;
					}
				}
			}
			else
			{
				return;
			}
			// Conventional plot generator
			PlotArea plotArea = plotGenerator.getNewPlotArea(world, null, null, null);
			PlotManager plotManager = plotGenerator.getNewPlotManager();
			log(C.PREFIX + "&aDetected world load for '" + world + '\'');
			log(C.PREFIX + "&3 - generator: &7" + baseGenerator + '>' + plotGenerator);
			log(C.PREFIX + "&3 - plotworld: &7" + plotArea.getClass().getName());
			log(C.PREFIX + "&3 - manager: &7" + plotManager.getClass().getName());
			if (!this.worlds.contains(path))
			{
				this.worlds.createSection(path);
				worldSection = this.worlds.getConfigurationSection(path);
			}
			plotArea.saveConfiguration(worldSection);
			plotArea.loadDefaultConfiguration(worldSection);
			try
			{
				this.worlds.save(this.worldsFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			// Now add it
			this.addPlotArea(plotArea);
			plotGenerator.initialize(plotArea);
		}
		else
		{
			if (!worlds.contains(world))
			{
				return;
			}
			ConfigurationSection areasSection = worldSection.getConfigurationSection("areas");
			if (areasSection == null)
			{
				if (this.plotAreaMap.containsKey(world))
				{
					debug("World possibly already loaded: " + world);
					return;
				}
				log(C.PREFIX + "&aDetected world load for '" + world + '\'');
				String gen_string = worldSection.getString("generator.plugin", "PlotSquared");
				if (type == 2)
				{
					Set<PlotCluster> clusters = this.clusters_tmp != null ? this.clusters_tmp.get(world) : new HashSet<>();
					if (clusters == null)
					{
						throw new IllegalArgumentException("No cluster exists for world: " + world);
					}
					ArrayDeque<PlotArea> toLoad = new ArrayDeque<>();
					for (PlotCluster cluster : clusters)
					{
						PlotId pos1 = cluster.getP1(); // Cluster pos1
						PlotId pos2 = cluster.getP2(); // Cluster pos2
						String name = cluster.getName(); // Cluster name
						String fullId = name + '-' + pos1 + '-' + pos2;
						worldSection.createSection("areas." + fullId);
						DBFunc.replaceWorld(world, world + ';' + name, pos1, pos2); // NPE

						log(C.PREFIX + "&3 - " + name + '-' + pos1 + '-' + pos2);
						GeneratorWrapper<?> areaGen = this.IMP.getGenerator(world, gen_string);
						if (areaGen == null)
						{
							throw new IllegalArgumentException("Invalid Generator: " + gen_string);
						}
						PlotArea pa = areaGen.getPlotGenerator().getNewPlotArea(world, name, pos1, pos2);
						pa.saveConfiguration(worldSection);
						pa.loadDefaultConfiguration(worldSection);
						try
						{
							this.worlds.save(this.worldsFile);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						log(C.PREFIX + "&c | &9generator: &7" + baseGenerator + '>' + areaGen);
						log(C.PREFIX + "&c | &9plotworld: &7" + pa);
						log(C.PREFIX + "&c | &9manager: &7" + pa);
						log(C.PREFIX + "&cNote: &7Area created for cluster:" + name + " (invalid or old configuration?)");
						areaGen.getPlotGenerator().initialize(pa);
						areaGen.augment(pa);
						toLoad.add(pa);
					}
					for (PlotArea area : toLoad)
					{
						this.addPlotArea(area);
					}
					return;
				}
				GeneratorWrapper<?> areaGen = this.IMP.getGenerator(world, gen_string);
				if (areaGen == null)
				{
					throw new IllegalArgumentException("Invalid Generator: " + gen_string);
				}
				PlotArea pa = areaGen.getPlotGenerator().getNewPlotArea(world, null, null, null);
				pa.saveConfiguration(worldSection);
				pa.loadDefaultConfiguration(worldSection);
				try
				{
					this.worlds.save(this.worldsFile);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				log(C.PREFIX + "&3 - generator: &7" + baseGenerator + '>' + areaGen);
				log(C.PREFIX + "&3 - plotworld: &7" + pa);
				log(C.PREFIX + "&3 - manager: &7" + pa.getPlotManager());
				areaGen.getPlotGenerator().initialize(pa);
				areaGen.augment(pa);
				this.addPlotArea(pa);
				return;
			}
			if (type == 1)
			{
				throw new IllegalArgumentException("Invalid type for multi-area world. Expected `2`, got `" + 1 + '`');
			}
			for (String areaId : areasSection.getKeys(false))
			{
				log(C.PREFIX + "&3 - " + areaId);
				int i1 = areaId.indexOf('-');
				int i2 = areaId.indexOf(';');
				if (i1 == -1 || i2 == -1)
				{
					throw new IllegalArgumentException("Invalid Area identifier: " + areaId + ". Expected form `<name>-<pos1>-<pos2>`");
				}
				String name = areaId.substring(0, i1);
				String rest = areaId.substring(i1 + 1);
				int i3 = rest.indexOf('-', i2 - name.length() - 1);
				PlotId pos1 = PlotId.fromString(rest.substring(0, i3));
				PlotId pos2 = PlotId.fromString(rest.substring(i3 + 1));
				if (pos1 == null || pos2 == null || name.isEmpty())
				{
					throw new IllegalArgumentException("Invalid Area identifier: " + areaId + ". Expected form `<name>-<x1;z1>-<x2;z2>`");
				}
				if (this.getPlotAreaAbs(world, name) != null)
				{
					continue;
				}
				ConfigurationSection section = areasSection.getConfigurationSection(areaId);
				YamlConfiguration clone = new YamlConfiguration();
				for (String key : section.getKeys(true))
				{
					if (section.get(key) instanceof MemorySection)
					{
						continue;
					}
					if (!clone.contains(key))
					{
						clone.set(key, section.get(key));
					}
				}
				for (String key : worldSection.getKeys(true))
				{
					if (worldSection.get(key) instanceof MemorySection)
					{
						continue;
					}
					if (!key.startsWith("areas") && !clone.contains(key))
					{
						clone.set(key, worldSection.get(key));
					}
				}
				String gen_string = clone.getString("generator.plugin", "PlotSquared");
				GeneratorWrapper<?> areaGen = this.IMP.getGenerator(world, gen_string);
				if (areaGen == null)
				{
					throw new IllegalArgumentException("Invalid Generator: " + gen_string);
				}
				PlotArea pa = areaGen.getPlotGenerator().getNewPlotArea(world, name, pos1, pos2);
				pa.saveConfiguration(clone);
				// netSections is the combination of
				for (String key : clone.getKeys(true))
				{
					if (clone.get(key) instanceof MemorySection)
					{
						continue;
					}
					if (!worldSection.contains(key))
					{
						worldSection.set(key, clone.get(key));
					}
					else
					{
						Object value = worldSection.get(key);
						if (!Objects.equals(value, clone.get(key)))
						{
							section.set(key, clone.get(key));
						}
					}
				}
				pa.loadDefaultConfiguration(clone);
				try
				{
					this.worlds.save(this.worldsFile);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				log(C.PREFIX + "&aDetected area load for '" + world + '\'');
				log(C.PREFIX + "&c | &9generator: &7" + baseGenerator + '>' + areaGen);
				log(C.PREFIX + "&c | &9plotworld: &7" + pa);
				log(C.PREFIX + "&c | &9manager: &7" + pa.getPlotManager());
				areaGen.getPlotGenerator().initialize(pa);
				areaGen.augment(pa);
				this.addPlotArea(pa);
			}
		}
	}

	/**
	 * Setup the configuration for a plot world based on world arguments.
	 * <p>
	 * <p>
	 * <i>e.g. /mv create &lt;world&gt; normal -g PlotSquared:&lt;args&gt;</i>
	 *
	 * @param world     The name of the world
	 * @param args      The arguments
	 * @param generator the plot generator
	 * @return boolean | if valid arguments were provided
	 */
	public boolean setupPlotWorld(String world, String args, IndependentPlotGenerator generator)
	{
		if (args != null && !args.isEmpty())
		{
			// save configuration
			String[] split = args.split(",");
			HybridPlotWorld plotworld = new HybridPlotWorld(world, null, generator, null, null);
			for (String element : split)
			{
				String[] pair = element.split("=");
				if (pair.length != 2)
				{
					log("&cNo value provided for: &7" + element);
					return false;
				}
				String key = pair[0].toLowerCase();
				String value = pair[1];
				try
				{
					String base = "worlds." + world + '.';
					switch (key)
					{
						case "s":
						case "size":
							this.worlds.set(base + "plot.size", Configuration.INTEGER.parseString(value).shortValue());
							break;
						case "g":
						case "gap":
							this.worlds.set(base + "road.width", Configuration.INTEGER.parseString(value).shortValue());
							break;
						case "h":
						case "height":
							this.worlds.set(base + "road.height", Configuration.INTEGER.parseString(value).shortValue());
							this.worlds.set(base + "plot.height", Configuration.INTEGER.parseString(value).shortValue());
							this.worlds.set(base + "wall.height", Configuration.INTEGER.parseString(value).shortValue());
							break;
						case "f":
						case "floor":
							this.worlds.set(base + "plot.floor",
											new ArrayList<>(Arrays.asList(StringMan.join(Configuration.BLOCKLIST.parseString(value), ",").split(","))));
							break;
						case "m":
						case "main":
							this.worlds.set(base + "plot.filling",
											new ArrayList<>(Arrays.asList(StringMan.join(Configuration.BLOCKLIST.parseString(value), ",").split(","))));
							break;
						case "w":
						case "wall":
							this.worlds.set(base + "wall.filling", Configuration.BLOCK.parseString(value).toString());
							break;
						case "b":
						case "border":
							this.worlds.set(base + "wall.block", Configuration.BLOCK.parseString(value).toString());
							break;
						default:
							log("&cKey not found: &7" + element);
							return false;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					log("&cInvalid value: &7" + value + " in arg " + element);
					return false;
				}
			}
			try
			{
				ConfigurationSection section = this.worlds.getConfigurationSection("worlds." + world);
				plotworld.saveConfiguration(section);
				plotworld.loadConfiguration(section);
				this.worlds.save(this.worldsFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean canUpdate(String current, String other)
	{
		String s1 = this.normalisedVersion(current);
		String s2 = this.normalisedVersion(other);
		int cmp = s1.compareTo(s2);
		return cmp < 0;
	}

	public String normalisedVersion(String version)
	{
		String[] split = Pattern.compile(".", Pattern.LITERAL).split(version);
		StringBuilder sb = new StringBuilder();
		for (String s : split)
		{
			sb.append(String.format("%" + 4 + 's', s));
		}
		return sb.toString();
	}

	public boolean update(PlotPlayer sender, URL url)
	{
		try
		{
			String name = this.jarFile.getName();
			MainUtil.sendMessage(sender, "$1Downloading from provided URL: &7" + url);
			URLConnection con = url.openConnection();
			try (InputStream stream = con.getInputStream())
			{
				File newJar = new File("plugins/update/" + name);
				File parent = newJar.getParentFile();
				if (!parent.exists())
				{
					parent.mkdirs();
				}
				MainUtil.sendMessage(sender, "$2 - Output: " + newJar);
				if (!newJar.delete())
				{
					MainUtil.sendMessage(sender, "Failed to update PlotSquared");
					MainUtil.sendMessage(sender, "Jar file failed to delete.");
					MainUtil.sendMessage(sender, " - Please update manually");
				}
				Files.copy(stream, newJar.toPath());
			}
			MainUtil.sendMessage(sender, "$1The update will take effect when the server is restarted next");
			return true;
		}
		catch (IOException e)
		{
			MainUtil.sendMessage(sender, "Failed to update PlotSquared");
			MainUtil.sendMessage(sender, " - Please update manually");
			log("============ Stacktrace ============");
			e.printStackTrace();
			log("====================================");
		}
		return false;
	}

	/**
	 * Copy a file from inside the jar to a location
	 *
	 * @param file   Name of the file inside PlotSquared.jar
	 * @param folder The output location relative to /plugins/PlotSquared/
	 */
	public void copyFile(String file, String folder)
	{
		try
		{
			File output = this.IMP.getDirectory();
			if (!output.exists())
			{
				output.mkdirs();
			}
			File newFile = MainUtil.getFile(output, folder + File.separator + file);
			if (newFile.exists())
			{
				return;
			}
			try (InputStream stream = this.IMP.getClass().getResourceAsStream(file))
			{
				byte[] buffer = new byte[2048];
				if (stream == null)
				{
					try (ZipInputStream zis = new ZipInputStream(new FileInputStream(this.jarFile)))
					{
						ZipEntry ze = zis.getNextEntry();
						while (ze != null)
						{
							String name = ze.getName();
							if (name.equals(file))
							{
								new File(newFile.getParent()).mkdirs();
								try (FileOutputStream fos = new FileOutputStream(newFile))
								{
									int len;
									while ((len = zis.read(buffer)) > 0)
									{
										fos.write(buffer, 0, len);
									}
								}
								ze = null;
							}
							else
							{
								ze = zis.getNextEntry();
							}
						}
						zis.closeEntry();
					}
					return;
				}
				newFile.createNewFile();
				try (FileOutputStream fos = new FileOutputStream(newFile))
				{
					int len;
					while ((len = stream.read(buffer)) > 0)
					{
						fos.write(buffer, 0, len);
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			log("&cCould not save " + file);
		}
	}

	private Map<String, Map<PlotId, Plot>> getPlotsRaw()
	{
		HashMap<String, Map<PlotId, Plot>> map = new HashMap<>();
		for (PlotArea area : this.plotAreas)
		{
			Map<PlotId, Plot> map2 = map.get(area.toString());
			if (map2 == null)
			{
				map.put(area.toString(), area.getPlotsRaw());
			}
			else
			{
				map2.putAll(area.getPlotsRaw());
			}
		}
		return map;
	}

	/**
	 * Close the database connection.
	 */
	public void disable()
	{
		try
		{
			// Validate that all data in the db is correct
			HashSet<Plot> plots = new HashSet<>();
			this.foreachPlotRaw(new RunnableVal<Plot>() {
				@Override
				public void run(Plot value)
				{
					plots.add(value);
				}
			});
			DBFunc.validatePlots(plots);

			// Close the connection
			DBFunc.close();
			UUIDHandler.handleShutdown();
		}
		catch (NullPointerException ignored)
		{
			log("&cCould not close database connection!");
		}
	}

	/**
	 * Setup the database connection.
	 */
	public void setupDatabase()
	{
		try
		{
			if (DBFunc.dbManager != null)
			{
				DBFunc.dbManager.close();
			}
			Database database;
			if (Storage.MySQL.USE)
			{
				database = new com.intellectualcrafters.plot.database.MySQL(Storage.MySQL.HOST, Storage.MySQL.PORT, Storage.MySQL.DATABASE,
																			Storage.MySQL.USER, Storage.MySQL.PASSWORD);
			}
			else if (Storage.SQLite.USE)
			{
				File file = MainUtil.getFile(this.IMP.getDirectory(), Storage.SQLite.DB + ".db");
				database = new com.intellectualcrafters.plot.database.SQLite(file);
			}
			else
			{
				log(C.PREFIX + "&cNo storage type is set!");
				this.IMP.disable();
				return;
			}
			DBFunc.dbManager = new SQLManager(database, Storage.PREFIX, false);
			this.plots_tmp = DBFunc.getPlots();
			this.clusters_tmp = DBFunc.getClusters();
		}
		catch (ClassNotFoundException | SQLException e)
		{
			log(C.PREFIX + "&cFailed to open DATABASE connection. The plugin will disable itself.");
			if (Storage.MySQL.USE)
			{
				log("$4MYSQL");
			}
			else if (Storage.SQLite.USE)
			{
				log("$4SQLITE");
			}
			log("&d==== Here is an ugly stacktrace, if you are interested in those things ===");
			e.printStackTrace();
			log("&d==== End of stacktrace ====");
			log("&6Please go to the PlotSquared 'storage.yml' and configure the database correctly.");
			this.IMP.disable();
		}
	}

	/**
	 * Setup the default configuration.
	 *
	 * @throws IOException if the config failed to save
	 */
	public void setupConfig() throws IOException
	{
		String lastVersionString = this.config.getString("version");
		if (lastVersionString != null)
		{
			String[] split = lastVersionString.split("\\.");
			this.lastVersion = new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};
		}
		if (this.lastVersion != null && this.checkVersion(new int[]{3, 4, 0}, this.lastVersion))
		{
			Settings.convertLegacy(this.configFile);
			if (this.config.contains("worlds"))
			{
				ConfigurationSection worldSection = this.config.getConfigurationSection("worlds");
				this.worlds.set("worlds", worldSection);
				try
				{
					this.worlds.save(this.worldsFile);
				}
				catch (IOException e)
				{
					debug("Failed to save PlotSquared worlds.yml");
					e.printStackTrace();
				}
			}
		}
		else
		{
			Settings.load(this.configFile);
		}
		Settings.VERSION = StringMan.join(this.version, ".");
		Settings.PLATFORM = this.platform;
		Settings.save(this.configFile);
		this.config = YamlConfiguration.loadConfiguration(this.configFile);
	}

	/**
	 * Setup all configuration files<br>
	 * - Config: settings.yml<br>
	 * - Storage: storage.yml<br>
	 * - Translation: PlotSquared.use_THIS.yml, style.yml<br>
	 */
	public void setupConfigs()
	{
		File folder = new File(this.IMP.getDirectory(), "config");
		if (!folder.exists() && !folder.mkdirs())
		{
			log(C.PREFIX + "&cFailed to create the /plugins/config folder. Please create it manually.");
		}
		try
		{
			this.worldsFile = new File(folder, "worlds.yml");
			if (!this.worldsFile.exists() && !this.worldsFile.createNewFile())
			{
				log("Could not create the worlds file, please create \"worlds.yml\" manually.");
			}
			this.worlds = YamlConfiguration.loadConfiguration(this.worldsFile);
		}
		catch (IOException ignored)
		{
			log("Failed to save settings.yml");
		}
		try
		{
			this.configFile = new File(folder, "settings.yml");
			if (!this.configFile.exists() && !this.configFile.createNewFile())
			{
				log("Could not create the settings file, please create \"settings.yml\" manually.");
			}
			this.config = YamlConfiguration.loadConfiguration(this.configFile);
			this.setupConfig();
		}
		catch (IOException ignored)
		{
			log("Failed to save settings.yml");
		}
		try
		{
			this.styleFile = MainUtil.getFile(this.IMP.getDirectory(), Settings.Paths.TRANSLATIONS + File.separator + "style.yml");
			if (!this.styleFile.exists())
			{
				if (!this.styleFile.getParentFile().exists())
				{
					this.styleFile.getParentFile().mkdirs();
				}
				if (!this.styleFile.createNewFile())
				{
					log("Could not create the style file, please create \"translations/style.yml\" manually");
				}
			}
			this.style = YamlConfiguration.loadConfiguration(this.styleFile);
			this.setupStyle();
		}
		catch (IOException err)
		{
			err.printStackTrace();
			log("failed to save style.yml");
		}
		try
		{
			this.storageFile = new File(folder, "storage.yml");
			if (!this.storageFile.exists() && !this.storageFile.createNewFile())
			{
				log("Could not the storage settings file, please create \"storage.yml\" manually.");
			}
			this.storage = YamlConfiguration.loadConfiguration(this.storageFile);
			this.setupStorage();
		}
		catch (IOException ignored)
		{
			log("Failed to save storage.yml");
		}
		try
		{
			this.commandsFile = new File(folder, "commands.yml");
			if (!this.commandsFile.exists() && !this.commandsFile.createNewFile())
			{
				log("Could not the storage settings file, please create \"commands.yml\" manually.");
			}
			this.commands = YamlConfiguration.loadConfiguration(this.commandsFile);
		}
		catch (IOException ignored)
		{
			log("Failed to save commands.yml");
		}
		try
		{
			this.style.save(this.styleFile);
			this.commands.save(this.commandsFile);
		}
		catch (IOException e)
		{
			log("Configuration file saving failed");
			e.printStackTrace();
		}
	}

	/**
	 * Setup the storage file (load + save missing nodes).
	 */
	private void setupStorage()
	{
		Storage.load(this.storageFile);
		Storage.save(this.storageFile);
		this.storage = YamlConfiguration.loadConfiguration(this.storageFile);
	}

	/**
	 * Show startup debug information.
	 */
	private void showDebug()
	{
		if (Settings.DEBUG)
		{
			Map<String, Object> components = Config.getFields(Settings.Enabled_Components.class);
			for (Map.Entry<String, Object> component : components.entrySet())
			{
				log(C.PREFIX + String.format("&cKey: &6%s&c, Value: &6%s", component.getKey(), component.getValue()));
			}
		}
	}

	/**
	 * Setup the style.yml file
	 */
	private void setupStyle()
	{
		this.style.set("version", StringMan.join(this.version, "."));
		Map<String, Object> o = new HashMap<>(4);
		o.put("color.1", "6");
		o.put("color.2", "7");
		o.put("color.3", "8");
		o.put("color.4", "3");
		if (!this.style.contains("color"))
		{
			for (Map.Entry<String, Object> node : o.entrySet())
			{
				this.style.set(node.getKey(), node.getValue());
			}
		}
	}

	/**
	 * Get the Java version.
	 *
	 * @return the java version
	 */
	private double getJavaVersion()
	{
		return Double.parseDouble(System.getProperty("java.specification.version"));
	}

	public void foreachPlotArea(RunnableVal<PlotArea> runnable)
	{
		for (PlotArea area : this.plotAreas)
		{
			runnable.run(area);
		}
	}

	public void foreachPlotArea(String world, RunnableVal<PlotArea> runnable)
	{
		PlotArea[] array = this.plotAreaMap.get(world);
		if (array == null)
		{
			return;
		}
		for (PlotArea area : array)
		{
			runnable.run(area);
		}
	}

	public void foreachPlot(RunnableVal<Plot> runnable)
	{
		for (PlotArea area : this.plotAreas)
		{
			for (Plot plot : area.getPlots())
			{
				runnable.run(plot);
			}
		}
	}

	public void foreachPlotRaw(RunnableVal<Plot> runnable)
	{
		for (PlotArea area : this.plotAreas)
		{
			for (Plot plot : area.getPlots())
			{
				runnable.run(plot);
			}
		}
		if (this.plots_tmp != null)
		{
			for (HashMap<PlotId, Plot> entry : this.plots_tmp.values())
			{
				for (Plot entry2 : entry.values())
				{
					runnable.run(entry2);
				}
			}
		}
	}

	public void foreachBasePlot(RunnableVal<Plot> run)
	{
		for (PlotArea area : this.plotAreas)
		{
			area.foreachBasePlot(run);
		}
	}

	public PlotArea getFirstPlotArea()
	{
		return this.plotAreas.length > 0 ? this.plotAreas[0] : null;
	}

	public int getPlotAreaCount()
	{
		return this.plotAreas.length;
	}

	public int getPlotCount()
	{
		int count = 0;
		for (PlotArea area : this.plotAreas)
		{
			count += area.getPlotCount();
		}
		return count;
	}

	public Set<PlotArea> getPlotAreas()
	{
		HashSet<PlotArea> set = new HashSet<>(this.plotAreas.length);
		Collections.addAll(set, this.plotAreas);
		return set;
	}

	/**
	 * Not recommended for use since worlds can have multiple PlotAreas.
	 *
	 * @return Set of world names
	 */
	@Deprecated
	public Set<String> getPlotWorldStrings()
	{
		HashSet<String> set = new HashSet<>(this.plotAreaMap.size());
		for (String entry : this.plotAreaMap.keySet())
		{
			set.add(entry);
		}
		return set;
	}

	public boolean isAugmented(String world)
	{
		PlotArea[] areas = this.plotAreaMap.get(world);
		if (areas == null)
		{
			return false;
		}
		if (areas.length > 1)
		{
			return true;
		}
		return areas[0].TYPE != 0;
	}

	/**
	 * Get a list of PlotArea objects.
	 *
	 * @param world the world
	 * @return Collection of PlotArea objects
	 */
	public Set<PlotArea> getPlotAreas(String world)
	{
		PlotArea[] areas = this.plotAreaMap.get(world);
		if (areas == null)
		{
			return new HashSet<>(0);
		}
		HashSet<PlotArea> set = new HashSet<>(areas.length);
		Collections.addAll(set, areas);
		return set;
	}

	public enum SortType {
		CREATION_DATE, CREATION_DATE_TIMESTAMP, LAST_MODIFIED, DISTANCE_FROM_ORIGIN
	}
}
