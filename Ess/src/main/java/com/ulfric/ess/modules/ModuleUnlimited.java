package com.ulfric.ess.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.ulfric.ess.commands.CommandUnlimited;
import com.ulfric.ess.lang.Meta;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleUnlimited extends SimpleModule {

	public ModuleUnlimited()
	{
		super("unlimited", "Provides the unlimited command", "evilmidget38", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("unlimited", new CommandUnlimited());

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true)
			public void onBlockPlaceEvent(BlockPlaceEvent event)
			{
				ItemStack item = event.getItemInHand();

				Player player = event.getPlayer();

				if (!player.hasMetadata(Meta.UNLIMITED)) return;

				if (!player.hasPermission("ess.unlimited." + item.getType().name().toLowerCase())) return;

				player.setItemInHand(item.clone());
			}
		});
	}

}