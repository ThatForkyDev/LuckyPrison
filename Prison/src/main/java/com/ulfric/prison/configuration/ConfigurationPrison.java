package com.ulfric.prison.configuration;

import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.RegionHook.Region;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.inventory.WeightedItemPair;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.prison.Prison;
import com.ulfric.prison.entity.MineWrapper;
import com.ulfric.prison.tasks.TaskMinereset;

public enum ConfigurationPrison {

	INSTANCE(Prison.get());

	public final void poke() { }

	private final FileConfiguration config;
	ConfigurationPrison(Prison i)
	{
		this.config = i.getConfig();

		this.config.options().copyDefaults(true);

		i.saveConfig();

		this.populate();
	}

	public void populate()
	{
		this.mines = Sets.newHashSet();

		ConfigurationSection section = null;

		Set<String> strings;

		if (this.config == null ||
			(section = this.config.getConfigurationSection("mines")) == null ||
			(strings = section.getKeys(false)) == null) return;

		for (String string : strings)
		{
			MineWrapper wrapper = new MineWrapper(string);

			for (String regionName : this.config.getConfigurationSection(Strings.format("mines.{0}.regions", string)).getKeys(false))
			{
				Region region = Hooks.REGIONS.region(regionName);

				if (region == null) continue;

				List<WeightedItemPair> items = Lists.newArrayList();

				for (String item : this.config.getStringList(Strings.format("mines.{0}.regions.{1}", string, regionName)))
				{
					items.add(MaterialUtils.weight(Integer.parseInt(StringUtils.findOption(item, "weight")), MaterialUtils.pair(StringUtils.findOption(item, "id"))));
				}

				if (items.isEmpty()) continue;

				wrapper.addMine(region, items);
			}

			if (wrapper.getMines().isEmpty()) continue;

			TaskMinereset.get().put(wrapper);

			this.mines.add(wrapper);
		}
	}

	private Set<MineWrapper> mines;
	public Set<MineWrapper> getMines() { return this.mines; }

}