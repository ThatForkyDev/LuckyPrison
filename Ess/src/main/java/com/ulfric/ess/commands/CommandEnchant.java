package com.ulfric.ess.commands;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.inventory.Enchant;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandEnchant extends SimpleCommand {

	public CommandEnchant()
	{
		this.withEnforcePlayer();

		this.withArgument("ench", ArgStrategy.ENCHANT, "ess.ench_needed");

		this.withArgument("roman", new ExactArg("--roman"));
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();
		ItemStack item = player.getItemInHand();

		if (ItemUtils.isEmpty(item))
		{
			Locale.sendError(player, "ess.ench_item_needed");

			return;
		}

		Enchant enchant = (Enchant) this.getObject("ench");

		enchant.apply(item, this.hasObject("roman"));

		player.setItemInHand(item);
	}

}