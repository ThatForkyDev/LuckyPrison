package com.ulfric.prison.modules;

import com.ulfric.prison.scoreboard.ScoreboardPanel;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleNameplate extends SimpleModule {

	public ModuleNameplate()
	{
		super("nameplate", "Nameplate colors module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("nameplate", new Command());
	}

	private class Command extends SimpleCommand
	{
		@Override
		public void run()
		{
			new PanelNameplate(this.getPlayer()).doNothing();
		}
	}

	private class PanelNameplate extends Panel
	{
		private PanelNameplate(Player player)
		{
			super("nameplate", ChatColor.BOLD + "Nameplate", 18, player);
		}

		private void doNothing() { }

		@SuppressWarnings("deprecation")
		@Override
		public void onOpen(Player player)
		{
			ItemBuilder builder = new ItemBuilder();

			this.addItem(builder.withType(Material.BARRIER).withName("NONE").build());

			builder.withType(Material.INK_SACK);

			for (ChatColor color : ChatColor.values())
			{
				if (color.isFormat() || color == ChatColor.WHITE || color == ChatColor.RESET) continue;

				// TODO is toLowerCase needed?
				if (!player.hasPermission("nameplate.color." + color.name().toLowerCase().replace("_", Strings.BLANK))) continue;

				this.addItem(builder.withName(color + WordUtils.capitalize(color.name().replace('_', ' '))).withDurability(ItemUtils.dyeFromChat(color).getDyeData()).build());
			}
		}

		@Override
		public void onClick(Player player, ItemStack item, int slot)
		{
			if (ItemUtils.is(item, Material.BARRIER))
			{
				Hooks.DATA.removePlayerData(player.getUniqueId(), "nameplate.color");

				ScoreboardPanel.INSTANCE.updateAll(player);
				player.closeInventory();
			}
			else if (ItemUtils.is(item, Material.INK_SACK))
			{
				String name = ChatColor.stripColor(item.getItemMeta().getDisplayName().replace(' ', '_')).toUpperCase();
				ChatColor color = ChatColor.valueOf(name);
				Hooks.DATA.setPlayerData(player.getUniqueId(), "nameplate.color", color.getChar());

				ScoreboardPanel.INSTANCE.updateAll(player);
				player.closeInventory();
			}
		}
	}
}
