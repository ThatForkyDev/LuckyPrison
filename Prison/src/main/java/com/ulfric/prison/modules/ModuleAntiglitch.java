package com.ulfric.prison.modules;

import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleAntiglitch extends SimpleModule {

	public ModuleAntiglitch()
	{
		super("antiglitch", "Antiglitch and exploit module", "Packet", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true)
			public void onPlace(BlockPlaceEvent event)
			{
				Block block = event.getBlock();

				if (block.getY() < 128) return;

				if (!block.getWorld().getEnvironment().equals(Environment.NETHER)) return;

				event.setCancelled(true);

				Locale.sendError(event.getPlayer(), "prison.nether_build_height");
			}
		});
	}

}