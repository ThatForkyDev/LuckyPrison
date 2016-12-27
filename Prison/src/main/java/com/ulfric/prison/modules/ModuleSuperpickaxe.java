package com.ulfric.prison.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleSuperpickaxe extends SimpleModule {

	public ModuleSuperpickaxe()
	{
		super("super-pick-axe", "Adds the /superpickaxe command", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true)
			public void onDamage(BlockDamageEvent event)
			{
				if (event.getInstaBreak()) return;

				if (event.getBlock().getType().equals(Material.BEDROCK)) return;

				if (!ItemUtils.is(event.getItemInHand(), Material.DIAMOND_PICKAXE)) return;

				if (!event.getPlayer().hasMetadata("_ulf_spa")) return;

				event.setInstaBreak(true);
			}
		});

		this.addCommand("superpickaxe", new CommandSuperpickaxe());
	}

	private class CommandSuperpickaxe extends SimpleCommand
	{
		public CommandSuperpickaxe()
		{
			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			if (Metadata.removeIfPresent(player, "_ulf_spa"))
			{
				Locale.sendSuccess(player, "prison.superpickaxe_disabled");

				return;
			}

			Metadata.applyNull(player, "_ulf_spa");

			Locale.sendSuccess(player, "prison.superpickaxe_enabled");
		}
	}

}