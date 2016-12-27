package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class GiveAct extends Action<ItemStack> {

	public GiveAct(String context)
	{
		super(context);
	}

	@Override
	protected ItemStack parse(String context)
	{
		ItemStack item = ItemUtils.fromString(context);

		if (item == null) throw new ScriptParseException(context);

		return item;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		InventoryUtils.giveOrDrop(player, this.getValue());
	}

}