package com.ulfric.chat.gui;

import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;

public class PanelChatsettings extends Panel {

	public static PanelChatsettings create(Player player)
	{
		return new PanelChatsettings(player);
	}

	private PanelChatsettings(Player player)
	{
		super("chatsettings", ChatColor.BOLD + "Chat Settings", 9, player);
	}

	@Override
	public void onOpen(Player player)
	{
		UUID uuid = player.getUniqueId();

		ItemStack item = new ItemStack(Material.INK_SACK, 1);

		this.addItem(this.createItem(item, uuid, Meta.SHOW_CHAT, Meta.Items.SHOW_CHAT),
					 this.createItem(item, uuid, Meta.SHOW_SELF_AS_BOLD, Meta.Items.SHOW_SELF_AS_BOLD),
					 this.createItem(item, uuid, Meta.SHOW_NICKNAMES, Meta.Items.SHOW_NICKNAMES),
					 this.createItem(item, uuid, Meta.NOTIFICATIONS, Meta.Items.NOTIFICATIONS),
					 this.createItem(item, uuid, Meta.AUCTIONS, Meta.Items.AUCTIONS),
					 this.createItem(item, uuid, Meta.TIPS, Meta.Items.TIPS),
					 this.createItem(item, uuid, Meta.PLOT_ADS, Meta.Items.PLOT_ADS),
					 this.createItem(item, uuid, Meta.MARRIAGE, Meta.Items.MARRIAGE),
					 this.createItem(item, uuid, Meta.JOINQUIT, Meta.Items.JOINQUIT));
	}

	@Override
	public void onClick(Player player, ItemStack item, int slot)
	{
		if (!ItemUtils.hasNameAndLore(item)) return;

		String a = ChatColor.GREEN + "ENABLED";
		String b = ChatColor.RED + "DISABLED";

		boolean flag = item.getDurability() == 8;

		String replace1 = flag ? a : b;
		String replace2 = flag ? b : a;

		ItemMeta meta = item.getItemMeta();
		meta.setLore(meta.getLore().stream().map(str -> str.replace(replace2, replace1)).collect(Collectors.toList()));
		item.setItemMeta(meta);

		if (!flag)
		{
			item.setDurability((short) 8);
		}
		else
		{
			item.setDurability((short) 10);
		}

		this.setItem(slot, item);
	}

	@Override
	public void onClose(Player player)
	{
		UUID uuid = player.getUniqueId();

		for (ItemStack item : this)
		{
			if (!ItemUtils.is(item, Material.INK_SACK)) continue;

			if (!item.hasItemMeta()) continue;

			ItemMeta meta = item.getItemMeta();

			if (!meta.hasDisplayName()) continue;

			String name = meta.getDisplayName();

			final boolean flag = item.getDurability() != 10;

			if (name.equals(Meta.Items.SHOW_CHAT))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.SHOW_CHAT, flag);
			}
			else if (name.equals(Meta.Items.SHOW_SELF_AS_BOLD))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.SHOW_SELF_AS_BOLD, flag);
			}
			else if (name.equals(Meta.Items.SHOW_NICKNAMES))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.SHOW_NICKNAMES, flag);
			}
			else if (name.equals(Meta.Items.NOTIFICATIONS))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.NOTIFICATIONS, flag);
			}
			else if (name.equals(Meta.Items.AUCTIONS))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.AUCTIONS, flag);
			}
			else if (name.equals(Meta.Items.TIPS))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.TIPS, flag);
			}
			else if (name.equals(Meta.Items.PLOT_ADS))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.PLOT_ADS, flag);
			}
			else if (name.equals(Meta.Items.MARRIAGE))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.MARRIAGE, flag);
			}
			else if (name.equals(Meta.Items.JOINQUIT))
			{
				Hooks.DATA.setPlayerData(uuid, Meta.JOINQUIT, flag);
			}
		}
	}

	private ItemStack createItem(ItemStack item, UUID uuid, String path, String name)
	{
		item = item.clone();
		boolean flag = !Hooks.DATA.getPlayerDataAsBoolean(uuid, path);

		item.setDurability(flag ? (short) 10 : (short) 8);

		ItemMeta meta = item.getItemMeta();

		meta.setDisplayName(name);

		meta.setLore(Lists.newArrayList(flag ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));

		item.setItemMeta(meta);

		return item;
	}

}