package com.plotsquared.bukkit.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.intellectualcrafters.configuration.ConfigurationSection;
import com.intellectualcrafters.configuration.file.YamlConfiguration;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.ConfigurationNode;
import com.intellectualcrafters.plot.generator.GeneratorWrapper;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.SetupObject;
import com.intellectualcrafters.plot.util.SetupUtils;
import com.plotsquared.bukkit.generator.BukkitPlotGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

public class BukkitSetupUtils extends SetupUtils {

	@Override
	public void updateGenerators()
	{
		if (!SetupUtils.generators.isEmpty())
		{
			return;
		}
		String testWorld = "CheckingPlotSquaredGenerator";
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
		{
			try
			{
				if (plugin.isEnabled())
				{
					ChunkGenerator generator = plugin.getDefaultWorldGenerator(testWorld, "");
					if (generator != null)
					{
						PS.get().removePlotAreas(testWorld);
						String name = plugin.getDescription().getName();
						GeneratorWrapper<?> wrapped = generator instanceof GeneratorWrapper<?> ? (GeneratorWrapper<?>) generator : new BukkitPlotGenerator(testWorld, generator);
						SetupUtils.generators.put(name, wrapped);
					}
				}
			}
			catch (Throwable e)
			{ // Recover from third party generator error
				e.printStackTrace();
			}
		}
	}

	@Override
	public String setupWorld(SetupObject object)
	{
		SetupUtils.manager.updateGenerators();
		ConfigurationNode[] steps = object.step == null ? new ConfigurationNode[0] : object.step;
		String world = object.world;
		int type = object.type;
		String worldPath = "worlds." + object.world;
		if (!PS.get().worlds.contains(worldPath))
		{
			PS.get().worlds.createSection(worldPath);
		}
		ConfigurationSection worldSection = PS.get().worlds.getConfigurationSection(worldPath);
		switch (type)
		{
			case 2:
			{
				if (object.id != null)
				{
					String areaName = object.id + '-' + object.min + '-' + object.max;
					String areaPath = "areas." + areaName;
					if (!worldSection.contains(areaPath))
					{
						worldSection.createSection(areaPath);
					}
					ConfigurationSection areaSection = worldSection.getConfigurationSection(areaPath);
					HashMap<String, Object> options = new HashMap<>();
					for (ConfigurationNode step : steps)
					{
						options.put(step.getConstant(), step.getValue());
					}
					options.put("generator.type", object.type);
					options.put("generator.terrain", object.terrain);
					options.put("generator.plugin", object.plotManager);
					if (object.setupGenerator != null && !object.setupGenerator.equals(object.plotManager))
					{
						options.put("generator.init", object.setupGenerator);
					}
					for (Map.Entry<String, Object> entry : options.entrySet())
					{
						String key = entry.getKey();
						Object value = entry.getValue();
						if (worldSection.contains(key))
						{
							Object current = worldSection.get(key);
							if (!Objects.equals(value, current))
							{
								areaSection.set(key, value);
							}
						}
						else
						{
							worldSection.set(key, value);
						}
					}
				}
				GeneratorWrapper<?> gen = SetupUtils.generators.get(object.setupGenerator);
				if (gen != null && gen.isFull())
				{
					object.setupGenerator = null;
				}
				break;
			}
			case 1:
				for (ConfigurationNode step : steps)
				{
					worldSection.set(step.getConstant(), step.getValue());
				}
				PS.get().worlds.set("worlds." + world + ".generator.type", object.type);
				PS.get().worlds.set("worlds." + world + ".generator.terrain", object.terrain);
				PS.get().worlds.set("worlds." + world + ".generator.plugin", object.plotManager);
				if (object.setupGenerator != null && !object.setupGenerator.equals(object.plotManager))
				{
					PS.get().worlds.set("worlds." + world + ".generator.init", object.setupGenerator);
				}
				GeneratorWrapper<?> gen = SetupUtils.generators.get(object.setupGenerator);
				if (gen != null && gen.isFull())
				{
					object.setupGenerator = null;
				}
				break;
			case 0:
				for (ConfigurationNode step : steps)
				{
					worldSection.set(step.getConstant(), step.getValue());
				}
				break;
		}
		try
		{
			PS.get().worlds.save(PS.get().worldsFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if (object.setupGenerator != null)
		{
			if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null && Bukkit.getPluginManager().getPlugin("Multiverse-Core")
																						.isEnabled())
			{
				Bukkit.getServer()
					  .dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv create " + world + " normal -g " + object.setupGenerator);
				this.setGenerator(world, object.setupGenerator);
				if (Bukkit.getWorld(world) != null)
				{
					return world;
				}
			}
			if (Bukkit.getPluginManager().getPlugin("MultiWorld") != null && Bukkit.getPluginManager().getPlugin("MultiWorld").isEnabled())
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mw create " + world + " plugin:" + object.setupGenerator);
				this.setGenerator(world, object.setupGenerator);
				if (Bukkit.getWorld(world) != null)
				{
					return world;
				}
			}
			WorldCreator wc = new WorldCreator(object.world);
			wc.generator(object.setupGenerator);
			wc.environment(World.Environment.NORMAL);
			Bukkit.createWorld(wc);
			this.setGenerator(world, object.setupGenerator);
		}
		else
		{
			if (Bukkit.getPluginManager().getPlugin("Multiverse-Core") != null && Bukkit.getPluginManager().getPlugin("Multiverse-Core")
																						.isEnabled())
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mv create " + world + " normal");
				if (Bukkit.getWorld(world) != null)
				{
					return world;
				}
			}
			if (Bukkit.getPluginManager().getPlugin("MultiWorld") != null && Bukkit.getPluginManager().getPlugin("MultiWorld").isEnabled())
			{
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mw create " + world);
				if (Bukkit.getWorld(world) != null)
				{
					return world;
				}
			}
			Bukkit.createWorld(new WorldCreator(object.world).environment(World.Environment.NORMAL));
		}
		return object.world;
	}

	public void setGenerator(String world, String generator)
	{
		if (Bukkit.getWorlds().isEmpty() || !Bukkit.getWorlds().get(0).getName().equals(world))
		{
			return;
		}
		File file = new File("bukkit.yml").getAbsoluteFile();
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		yml.set("worlds." + world + ".generator", generator);
		try
		{
			yml.save(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public String getGenerator(PlotArea plotArea)
	{
		if (SetupUtils.generators.isEmpty())
		{
			this.updateGenerators();
		}
		World world = Bukkit.getWorld(plotArea.worldname);
		if (world == null)
		{
			return null;
		}
		ChunkGenerator generator = world.getGenerator();
		if (!(generator instanceof BukkitPlotGenerator))
		{
			return null;
		}
		for (Map.Entry<String, GeneratorWrapper<?>> entry : SetupUtils.generators.entrySet())
		{
			GeneratorWrapper<?> current = entry.getValue();
			if (current.equals(generator))
			{
				return entry.getKey();
			}
		}
		return null;
	}
}
