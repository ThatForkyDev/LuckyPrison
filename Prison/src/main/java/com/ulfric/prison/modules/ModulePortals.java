package com.ulfric.prison.modules;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.world.Worlds;

public class ModulePortals extends SimpleModule {

	public ModulePortals()
	{
		super("portals", "Links nether portals to warps", "Packet", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler
			public void onPortal(PlayerPortalEvent event)
			{
				event.useTravelAgent(false);

				Location to = event.getTo();

				if (to == null) return;

				World world = to.getWorld();

				if (world.equals(Worlds.main()))
				{
					event.setTo(Hooks.ESS.getSpawnpoint().getLocation());

					return;
				}

				event.setTo(Hooks.ESS.warp("nether").getLocation());
			}
		});
	}

}