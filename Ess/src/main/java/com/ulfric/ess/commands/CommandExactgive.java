package com.ulfric.ess.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandExactgive extends SimpleCommand {

	public CommandExactgive()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer);

		this.withIndexUnusedArgs();
	}

	@Override
	public void run()
	{
		String unused = this.getUnusedArgs();

		if (StringUtils.isEmpty(unused))
		{
			Locale.sendError(this.getSender(), "ess.exactgive_specify_item_str");

			return;
		}

		ItemStack item = null;

		try
		{
			item = ItemUtils.fromString(this.getUnusedArgs());
		}
		catch (Throwable throwable) { }

		if (item == null)
		{
			Locale.sendError(this.getSender(), "ess.exactgive_specify_item");

			return;
		}

		Player player = (Player) this.getObject("player");

		if (player == null) return;

		player.getInventory().addItem(item);
	}

}