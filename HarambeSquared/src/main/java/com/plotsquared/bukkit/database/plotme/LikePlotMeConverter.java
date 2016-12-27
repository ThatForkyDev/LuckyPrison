package com.plotsquared.bukkit.database.plotme;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.intellectualcrafters.configuration.MemorySection;
import com.intellectualcrafters.configuration.file.FileConfiguration;
import com.intellectualcrafters.configuration.file.YamlConfiguration;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.generator.HybridGen;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.bukkit.generator.BukkitPlotGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandException;

public class LikePlotMeConverter {

	private final String plugin;

	public LikePlotMeConverter(String plugin)
	{
		this.plugin = plugin;
	}

	public static String getWorld(String world)
	{
		for (World newWorld : Bukkit.getWorlds())
		{
			if (newWorld.getName().equalsIgnoreCase(world))
			{
				return newWorld.getName();
			}
		}
		return world;
	}

	private void sendMessage(String message)
	{
		PS.debug("&3PlotMe&8->&3PlotSquared&8: &7" + message);
	}

	public String getPlotMePath()
	{
		return new File(".").getAbsolutePath() + File.separator + "plugins" + File.separator + this.plugin + File.separator;
	}

	public FileConfiguration getPlotMeConfig(String dataFolder)
	{
		File plotMeFile = new File(dataFolder + "config.yml");
		if (!plotMeFile.exists())
		{
			return null;
		}
		return YamlConfiguration.loadConfiguration(plotMeFile);
	}

	public Set<String> getPlotMeWorlds(FileConfiguration plotConfig)
	{
		return plotConfig.getConfigurationSection("worlds").getKeys(false);
	}

	public void mergeWorldYml(FileConfiguration plotConfig)
	{
		try
		{
			File genConfig =
					new File("plugins" + File.separator + this.plugin + File.separator + "PlotMe-DefaultGenerator" + File.separator + "config.yml");
			if (genConfig.exists())
			{
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(genConfig);
				for (String key : yml.getKeys(true))
				{
					if (!plotConfig.contains(key))
					{
						Object value = yml.get(key);
						if (!(value instanceof MemorySection))
						{
							plotConfig.set(key, value);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateWorldYml(String location)
	{
		try
		{
			Path path = Paths.get(location);
			File file = new File(location);
			if (!file.exists())
			{
				return;
			}
			String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			content = content.replace("PlotMe-DefaultGenerator", "PlotSquared");
			content = content.replace("PlotMe", "PlotSquared");
			content = content.replace("AthionPlots", "PlotSquared");
			content = content.replace("PlotZWorld", "PlotSquared");
			Files.write(path, content.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException ignored) {}
	}

	public boolean run(PlotMeConnector connector)
	{
		try
		{
			String dataFolder = this.getPlotMePath();
			FileConfiguration plotConfig = this.getPlotMeConfig(dataFolder);
			if (plotConfig == null)
			{
				return false;
			}

			String version = plotConfig.getString("Version");
			if (version == null)
			{
				version = plotConfig.getString("version");
			}
			if (!connector.accepts(version))
			{
				return false;
			}

			PS.debug("&3Using connector: " + connector.getClass().getCanonicalName());

			Connection connection = connector.getPlotMeConnection(this.plugin, plotConfig, dataFolder);

			if (!connector.isValidConnection(connection))
			{
				this.sendMessage("Cannot connect to PlotMe DB. Conversion process will not continue");
				return false;
			}

			this.sendMessage("PlotMe conversion has started. To disable this, please set 'plotme-convert.enabled' to false in the 'settings.yml'");

			this.mergeWorldYml(plotConfig);

			this.sendMessage("Connecting to PlotMe DB");

			this.sendMessage("Collecting plot data");

			String dbPrefix = "PlotMe".toLowerCase();
			this.sendMessage(" - " + dbPrefix + "Plots");
			Set<String> worlds = this.getPlotMeWorlds(plotConfig);

			if (Settings.Enabled_Components.PLOTME_CONVERTER)
			{
				this.sendMessage("Updating bukkit.yml");
				this.updateWorldYml("bukkit.yml");
				this.updateWorldYml("plugins/Multiverse-Core/worlds.yml");
				for (String world : plotConfig.getConfigurationSection("worlds").getKeys(false))
				{
					this.sendMessage("Copying config for: " + world);
					try
					{
						String actualWorldName = getWorld(world);
						connector.copyConfig(plotConfig, world, actualWorldName);
						PS.get().worlds.save(PS.get().worldsFile);
					}
					catch (IOException e)
					{
						e.printStackTrace();
						this.sendMessage("&c-- &lFailed to save configuration for world '" + world
										 + "'\nThis will need to be done using the setup command, or manually");
					}
				}
			}
			HashMap<String, HashMap<PlotId, Plot>> plots = connector.getPlotMePlots(connection);
			int plotCount = 0;
			for (Map.Entry<String, HashMap<PlotId, Plot>> entry : plots.entrySet())
			{
				plotCount += entry.getValue().size();
			}
			if (!Settings.Enabled_Components.PLOTME_CONVERTER)
			{
				return false;
			}

			this.sendMessage(" - " + dbPrefix + "Allowed");

			this.sendMessage("Collected " + plotCount + " plots from PlotMe");
			File plotmeDgFile = new File(dataFolder + File.separator + "PlotMe-DefaultGenerator" + File.separator + "config.yml");
			if (plotmeDgFile.exists())
			{
				YamlConfiguration plotmeDgYml = YamlConfiguration.loadConfiguration(plotmeDgFile);
				try
				{
					for (String world : plots.keySet())
					{
						String actualWorldName = getWorld(world);
						String plotMeWorldName = world.toLowerCase();
						Integer pathWidth = plotmeDgYml.getInt("worlds." + plotMeWorldName + ".PathWidth"); //
						PS.get().worlds.set("worlds." + world + ".road.width", pathWidth);

						int pathHeight = plotmeDgYml.getInt("worlds." + plotMeWorldName + ".RoadHeight", 64); //
						PS.get().worlds.set("worlds." + world + ".road.height", pathHeight);
						PS.get().worlds.set("worlds." + world + ".wall.height", pathHeight);
						PS.get().worlds.set("worlds." + world + ".plot.height", pathHeight);
						int plotSize = plotmeDgYml.getInt("worlds." + plotMeWorldName + ".PlotSize", 32); //
						PS.get().worlds.set("worlds." + world + ".plot.size", plotSize);
						String wallblock = plotmeDgYml.getString("worlds." + plotMeWorldName + ".WallBlock", "44"); //
						PS.get().worlds.set("worlds." + world + ".wall.block", wallblock);
						String floor = plotmeDgYml.getString("worlds." + plotMeWorldName + ".PlotFloorBlock", "2"); //
						PS.get().worlds.set("worlds." + world + ".plot.floor", Collections.singletonList(floor));
						String filling = plotmeDgYml.getString("worlds." + plotMeWorldName + ".FillBlock", "3"); //
						PS.get().worlds.set("worlds." + world + ".plot.filling", Collections.singletonList(filling));
						String road = plotmeDgYml.getString("worlds." + plotMeWorldName + ".RoadMainBlock", "5");
						PS.get().worlds.set("worlds." + world + ".road.block", road);
						int height = plotmeDgYml.getInt("worlds." + plotMeWorldName + ".RoadHeight"); //
						if (height == 0)
						{
							height = plotmeDgYml.getInt("worlds." + plotMeWorldName + ".GroundHeight", 64); //
						}
						PS.get().worlds.set("worlds." + actualWorldName + ".road.height", height);
						PS.get().worlds.set("worlds." + actualWorldName + ".plot.height", height);
						PS.get().worlds.set("worlds." + actualWorldName + ".wall.height", height);
						PS.get().worlds.save(PS.get().worldsFile);
					}
				}
				catch (IOException ignored) {}
			}
			ArrayList<Plot> createdPlots = new ArrayList<>();
			for (Map.Entry<String, HashMap<PlotId, Plot>> entry : plots.entrySet())
			{
				String world = entry.getKey();
				PlotArea area = PS.get().getPlotArea(world, null);
				int duplicate = 0;
				if (area != null)
				{
					for (Map.Entry<PlotId, Plot> entry2 : entry.getValue().entrySet())
					{
						if (area.getOwnedPlotAbs(entry2.getKey()) != null)
						{
							duplicate++;
						}
						else
						{
							createdPlots.add(entry2.getValue());
						}
					}
					if (duplicate > 0)
					{
						PS.debug("&c[WARNING] Found " + duplicate + " duplicate plots already in DB for world: '" + world
								 + "'. Have you run the converter already?");
					}
				}
				else
				{
					if (PS.get().plots_tmp != null)
					{
						HashMap<PlotId, Plot> map = PS.get().plots_tmp.get(world);
						if (map != null)
						{
							for (Map.Entry<PlotId, Plot> entry2 : entry.getValue().entrySet())
							{
								if (map.containsKey(entry2.getKey()))
								{
									duplicate++;
								}
								else
								{
									createdPlots.add(entry2.getValue());
								}
							}
							if (duplicate > 0)
							{
								PS.debug("&c[WARNING] Found " + duplicate + " duplicate plots already in DB for world: '" + world
										 + "'. Have you run the converter already?");
							}
							continue;
						}
					}
					createdPlots.addAll(entry.getValue().values());
				}
			}
			this.sendMessage("Creating plot DB");
			Thread.sleep(1000);
			AtomicBoolean done = new AtomicBoolean(false);
			DBFunc.createPlotsAndData(createdPlots, () ->
			{
				if (done.get())
				{
					this.done();
					this.sendMessage("&aDatabase conversion is now complete!");
					PS.debug("&c - Stop the server");
					PS.debug("&c - Disable 'plotme-convert.enabled' and 'plotme-convert.cache-uuids' in the settings.yml");
					PS.debug("&c - Correct any generator settings that haven't copied to 'settings.yml' properly");
					PS.debug("&c - Start the server");
					PS.get().setPlots(DBFunc.getPlots());
				}
				else
				{
					this.sendMessage("&cPlease wait until database conversion is complete. You will be notified with instructions when this happens!");
					done.set(true);
				}
			});
			this.sendMessage("Saving configuration...");
			try
			{
				PS.get().worlds.save(PS.get().worldsFile);
			}
			catch (IOException ignored)
			{
				this.sendMessage(" - &cFailed to save configuration.");
			}
			TaskManager.runTask(() ->
								{
									try
									{
										boolean mv = false;
										boolean mw = false;
										if ((Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null) && Bukkit.getPluginManager().getPlugin("Multiverse-Core")
																													  .isEnabled())
										{
											mv = true;
										}
										else if ((Bukkit.getPluginManager().getPlugin("MultiWorld") != null) && Bukkit.getPluginManager().getPlugin("MultiWorld")
																													  .isEnabled())
										{
											mw = true;
										}
										for (String worldName : worlds)
										{
											World world = Bukkit.getWorld(getWorld(worldName));
											if (world == null)
											{
												this.sendMessage("&cInvalid world in PlotMe configuration: " + worldName);
											}
											String actualWorldName = world.getName();
											this.sendMessage("Reloading generator for world: '" + actualWorldName + "'...");
											PS.get().removePlotAreas(actualWorldName);
											if (mv)
											{
												// unload world with MV
												Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv unload " + actualWorldName);
												try
												{
													Thread.sleep(1000);
												}
												catch (InterruptedException ignored)
												{
													Thread.currentThread().interrupt();
												}
												// load world with MV
												Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
																				   "mv import " + actualWorldName + " normal -g PlotSquared");
											}
											else if (mw)
											{
												// unload world with MW
												Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mw unload " + actualWorldName);
												try
												{
													Thread.sleep(1000);
												}
												catch (InterruptedException ignored)
												{
													Thread.currentThread().interrupt();
												}
												// load world with MW
												Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
																				   "mw create " + actualWorldName + " plugin:PlotSquared");
											}
											else
											{
												// Load using Bukkit API
												// - User must set generator manually
												Bukkit.getServer().unloadWorld(world, true);
												World myWorld = WorldCreator.name(actualWorldName).generator(new BukkitPlotGenerator(new HybridGen())).createWorld();
												myWorld.save();
											}
										}
									}
									catch (CommandException e)
									{
										e.printStackTrace();
									}
									if (done.get())
									{
										this.done();
										this.sendMessage("&aDatabase conversion is now complete!");
										PS.debug("&c - Stop the server");
										PS.debug("&c - Disable 'plotme-convert.enabled' and 'plotme-convert.cache-uuids' in the settings.yml");
										PS.debug("&c - Correct any generator settings that haven't copied to 'settings.yml' properly");
										PS.debug("&c - Start the server");
									}
									else
									{
										this.sendMessage("&cPlease wait until database conversion is complete. You will be notified with instructions when this happens!");
										done.set(true);
									}
								});
		}
		catch (InterruptedException | SQLException e)
		{
			e.printStackTrace();
			PS.debug("&/end/");
		}
		return true;
	}

	public void done()
	{
		PS.get().setPlots(DBFunc.getPlots());
	}
}
