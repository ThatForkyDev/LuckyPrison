package com.ulfric.ess.commands;

import com.ulfric.lib.api.hook.Hooks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandFix extends SimpleCommand {

	public CommandFix()
	{
		this.withSubcommand(new Hand());
		this.withSubcommand(new All());
	}

	private class Hand extends SimpleSubCommand
	{
		public Hand()
		{
			super(CommandFix.this, "hand", "this", "h", "item");

			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			if (Hooks.COMBATTAG.isTagged(player))
			{
				Locale.sendError(player, "ess.repair_unable_incombat");
			}

			ItemStack stack = player.getItemInHand();

			if (ItemUtils.isEmpty(stack))
			{
				Locale.sendError(player, "ess.repair_air");

				return;
			}

			if (stack.getType().getMaxDurability() == 0)
			{
				Locale.sendError(player, "ess.repair_unable_named");

				return;
			}

			if (stack.getDurability() == 0)
			{
				Locale.sendError(player, "ess.repair_unable_repaired");

				return;
			}

			stack.setDurability((short) 0);

			Locale.sendSuccess(player, "ess.repair_hand_named", MaterialUtils.getName(MaterialUtils.pair(stack)));

			player.updateInventory();
		}
	}

	private class All extends SimpleSubCommand
	{
		public All()
		{
			super(CommandFix.this, "all", "inv", "i", "inventory");

			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			PlayerInventory inventory = player.getInventory();

			int count = 0;

			for (ItemStack stack : inventory)
			{
				if (ItemUtils.isEmpty(stack)) continue;

				if (stack.getType().getMaxDurability() == 0) continue;

				if (stack.getDurability() == 0) continue;

				stack.setDurability((short) 0);

				count++;
			}

			if (this.hasPermission(Permissions.FIX_ALL_ARMOR))
			{
				ItemStack[] armorContents = inventory.getArmorContents();

				for (ItemStack stack : armorContents)
				{
					if (ItemUtils.isEmpty(stack)) continue;

					if (stack.getType().getMaxDurability() == 0) continue; // /hat

					if (stack.getDurability() == 0) continue;

					stack.setDurability((short) 0);

					count++;
				}

				//inventory.setArmorContents(armorContents);
			}

			player.updateInventory();

			Locale.sendSuccess(player, "ess.repair_inventory", count);
		}
	}

	@Override
	public void run()
	{
		Locale.sendError(this.getSender(), "ess.repair_unknown");
	}

}