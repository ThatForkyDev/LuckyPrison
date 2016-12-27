package com.ulfric.ess.modules;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.google.common.io.Files;
import com.ulfric.ess.commands.CommandHome;
import com.ulfric.ess.commands.CommandSethome;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.RegionHook.Region;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.Tasks;

public class ModuleHomes extends SimpleModule {

	public ModuleHomes()
	{
		super("homes", "Homes feature module", "Packet", "1.0.0-REL");

		this.addCommand("home", new CommandHome());
		this.addCommand("sethome", new CommandSethome());
	}

	@Override
	public void runTest(Object data)
	{
		if (!Hooks.REGIONS.isModuleEnabled()) return;

		Tasks.runAsync(() ->
		{
			StringBuilder builder = new StringBuilder();

			for (OfflinePlayer player : Bukkit.getOfflinePlayers())
			{
				UUID uuid = player.getUniqueId();

				if (!this.safe(Hooks.REGIONS.at(LocationUtils.fromString(Hooks.DATA.getPlayerDataAsString(uuid, "data.lastlocation")))))
				{
					builder.append(player.getName());
					builder.append('\n');

					continue;
				}

				List<Location> homes = Hooks.DATA.getPlayerDataAsStringList(uuid, "homes").stream().map(home -> LocationUtils.fromString(home.split(" ")[1])).collect(Collectors.toList());

				for (Location location : homes)
				{
					if (!this.safe(Hooks.REGIONS.at(location)))
					{
						builder.append(player.getName());
						builder.append('\n');

						break;
					}
				}
			}

			try
			{
				Files.write(builder, new File("homes.txt"), Charset.defaultCharset());
			}
			catch (Exception exception) { exception.printStackTrace(); }
		});
	}

	private boolean safe(List<Region> regions)
	{
		if (CollectUtils.isEmpty(regions)) return false;

		for (Region region : regions)
		{
			if (region.isGlobal()) continue;

			return true;
		}

		return false;
	}

}