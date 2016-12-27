package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.math.Numbers;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class DamageAct extends Action<Double> {

	public DamageAct(String context)
	{
		super(context);
	}

	@Override
	protected Double parse(String context)
	{
		Double value = Numbers.parseDouble(context);

		if (value == null) throw new ScriptParseException(context);

		return value;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		if (this.getValue() < 0)
		{
			player.setHealth(Math.min(player.getHealth() + -this.getValue(), player.getMaxHealth()));

			return;
		}

		player.damage(this.getValue());
	}

}