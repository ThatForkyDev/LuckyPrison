package com.ulfric.ess.modules;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.ulfric.ess.commands.CommandSetspawn;
import com.ulfric.ess.commands.CommandSpawn;
import com.ulfric.ess.configuration.ConfigurationEss;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.location.LocationProxy;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerFirstJoinEvent;
import com.ulfric.lib.api.world.Worlds;

public class ModuleSpawnpoint extends SimpleModule {

	private static final ModuleSpawnpoint INSTANCE = new ModuleSpawnpoint();
	public static ModuleSpawnpoint get() { return ModuleSpawnpoint.INSTANCE; }

	private ModuleSpawnpoint()
	{
		super("spawnpoint", "Spawnpoint module, used for managing where the spawn is", "Packet", "1.0.0-REL");

		this.addCommand("spawn", new CommandSpawn());
		this.addCommand("setspawn", new CommandSetspawn());

		this.addListener(new Listener()
		{
			@EventHandler
			public void onJoin(PlayerFirstJoinEvent event)
			{
				try
				{
					User user = Hooks.PERMISSIONS.user(event.getPlayer());

					if (user.getRankLadderGroup("mines") == null)
					{
						Group[] groups = user.getGroups();
						user.setGroup(Hooks.PERMISSIONS.group("a"));
						for (Group group : groups)
						{
							user.add(group);
						}
					}
				}
				catch (Throwable t) { t.printStackTrace(); }

				LocationProxy proxy = ModuleSpawnpoint.this.spawn;

				if (proxy == null) return;

				proxy.cloneTeleport(event.getPlayer());
			}

			@EventHandler
			public void onRespawn(PlayerRespawnEvent event)
			{
				event.setRespawnLocation(ModuleSpawnpoint.this.spawn.getLocation());
			}
		});
	}

	@Override
	public void postEnable()
	{
		String spawn = ConfigurationEss.get().conf().getString("spawn");

		if (StringUtils.isBlank(spawn))
		{
			this.spawn = LocationUtils.proxy(Worlds.main().getSpawnLocation());

			return;
		}

		this.spawn = LocationUtils.proxyFromString(spawn);

		/*if (this.spawn != null) return;

		this.spawn = LocationUtils.proxy(Worlds.main().getSpawnLocation());*/
	}

	@Override
	public void postDisable()
	{
		ConfigurationEss.get().conf().set("spawn", LocationUtils.toString(this.spawn.getLocation()));
	}

	private LocationProxy spawn;
	public LocationProxy getSpawn() { return this.spawn; }
	public void setSpawn(Location spawn)
	{
		spawn.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

		this.spawn = LocationUtils.proxy(spawn);
	}

}