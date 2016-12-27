package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class FlyAct extends Action<Boolean> {

	public FlyAct(String context)
	{
		super(context);
	}

	@Override
	protected Boolean parse(String context)
	{
		return context.equalsIgnoreCase("true");
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		player.setFlying(this.getValue());
	}

}