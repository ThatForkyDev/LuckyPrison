package com.ulfric.chat.gui;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;

public class PanelChatcolor extends Panel {

	public static PanelChatcolor create(Player player)
	{
		return new PanelChatcolor(player);
	}

	private PanelChatcolor(Player player)
	{
		super("chatsettings", ChatColor.BOLD + "Chat Color", 18, player);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onOpen(Player player)
	{
		ItemBuilder builder = new ItemBuilder();
		builder.withType(Material.INK_SACK);

		for (ChatColor color : ChatColor.values())
		{
			if (!color.isColor()) continue;

			if (color.equals(ChatColor.RESET)) continue;

			// TODO is toLowerCase needed?
			if (!player.hasPermission("chat.color." + color.name().toLowerCase().replace("_", Strings.BLANK))) continue;

			this.addItem(builder.withName(color + WordUtils.capitalize(color.name().replace('_', ' '))).withDurability(ItemUtils.dyeFromChat(color).getDyeData()).build());
		}
	}

	@Override
	public void onClick(Player player, ItemStack item, int slot)
	{
		if (!ItemUtils.is(item, Material.INK_SACK)) return;

		ChatColor color = ChatColor.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName().replace(' ', '_')).toUpperCase());

		Hooks.DATA.setPlayerData(player.getUniqueId(), "chat.color", color.getChar());

		player.closeInventory();
	}

}