package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;

public class CommandOpeninv extends SimpleCommand {

	public CommandOpeninv()
	{
		this.withEnforcePlayer();

		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		/*Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		PlayerInventory targetInventory = target.getInventory();
		int size = targetInventory.getSize();

		Inventory inventory = Bukkit.createInventory(player, size + 27, target.getName() + "'s Inventory");

		for (int x = 9, y = 0; x < size; x++, y++)
		{
			ItemStack item = targetInventory.getItem(x);

			if (ItemUtils.isEmpty(item)) continue;

			inventory.setItem(y, item);
		}

		

		player.openInventory(inventory);*/

		this.getPlayer().openInventory(((Player) this.getObject("player")).getInventory());
	}

}