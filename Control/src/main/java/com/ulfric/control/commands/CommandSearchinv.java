package com.ulfric.control.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;

public class CommandSearchinv extends SimpleCommand {

	public CommandSearchinv()
	{
		this.withEnforcePlayer();

		this.withArgument("material", ArgStrategy.MATERIAL);
		this.withArgument("amount", ArgStrategy.POSITIVE_INTEGER);

		this.withIndexUnusedArgs();
	}

	@Override
	public void run()
	{
		ItemPair material = (ItemPair) this.getObject("material");

		ItemStack item = null;

		if (material == null)
		{
			item = ItemUtils.fromString(this.getUnusedArgs());
		}
		else
		{
			Integer amount = (Integer) this.getObject("amount");

			int amt = amount == null ? 1 : amount.intValue();

			item = material.toItem(amt);
		}

		Assert.notNull(item);

		List<String> had = Lists.newArrayList();

		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (!player.getInventory().containsAtLeast(item, 1)) continue;

			had.add(player.getName());
		}

		this.getSender().sendMessage(ChatColor.GREEN + "FOUND: " + ChatColor.GRAY + StringUtils.mergeNicely(had));
	}

}