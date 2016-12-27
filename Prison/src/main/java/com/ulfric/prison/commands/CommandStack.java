package com.ulfric.prison.commands;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandStack extends SimpleCommand {


	public CommandStack()
	{
		this.withEnforcePlayer();

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "prison.stack.others");

		this.blanks = Maps.newEnumMap(Material.class);

		this.blanks.put(Material.COAL, new ItemStack(Material.COAL));
		this.blanks.put(Material.IRON_INGOT, new ItemStack(Material.IRON_INGOT));
		this.blanks.put(Material.GOLD_INGOT, new ItemStack(Material.GOLD_INGOT));
		this.blanks.put(Material.DIAMOND, new ItemStack(Material.DIAMOND));
		this.blanks.put(Material.EMERALD, new ItemStack(Material.EMERALD));
		this.blanks.put(Material.REDSTONE, new ItemStack(Material.REDSTONE));
		this.blanks.put(Material.QUARTZ, new ItemStack(Material.QUARTZ));
		this.blanks.put(Material.SLIME_BALL, new ItemStack(Material.QUARTZ));
		this.blanks.put(Material.WHEAT, new ItemStack(Material.WHEAT));
		// TODO
		//this.blanks.put(Material.INK_SACK, MaterialUtils.pair("lapis").toItem());
	}

	private Map<Material, ItemStack> blanks;

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");
		PlayerInventory inventory = target.getInventory();

		if (InventoryUtils.isEmpty(inventory))
		{
			Locale.sendError(this.getPlayer(), "system.inventory_empty");

			return;
		}

		Map<Material, Integer> map = Maps.newEnumMap(Material.class);

		for (ItemStack item : inventory)
		{
			if (item == null) continue;

			Material type = item.getType();
			ItemStack match = this.blanks.get(type);

			if (match == null) continue;

			if (!match.isSimilar(item)) continue;

			Integer amount = map.get(type);

			if (amount == null)
			{
				map.put(type, item.getAmount());

				continue;
			}

			map.put(type, map.get(type) + item.getAmount());
		}

		Set<Entry<Material, Integer>> entries = map.entrySet();

		if (entries.isEmpty())
		{
			Locale.sendError(this.getPlayer(), "prison.stack_none");

			return;
		}

		int total = 0;

		for (Entry<Material, Integer> entry : map.entrySet())
		{
			int amount = entry.getValue();

			if (amount < 9) continue;

			amount /= 9;

			ItemStack item = this.blanks.get(entry.getKey());

			int bigamount = amount * 9;

			total += bigamount;

			item.setAmount(bigamount);

			inventory.removeItem(item);

			inventory.addItem(new ItemStack(MaterialUtils.compress(item), amount));
		}

		if (total == 0)
		{
			Locale.sendError(this.getPlayer(), "prison.stack_not_enough");

			return;
		}

		Locale.sendSuccess(this.getPlayer(), "prison.stack_made", total);
	}


}