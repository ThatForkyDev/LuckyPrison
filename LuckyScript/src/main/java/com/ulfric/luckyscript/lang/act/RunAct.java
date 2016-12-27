package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.luckyscript.lang.Script;
import com.ulfric.luckyscript.lang.Scripts;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class RunAct extends Action<Script> {

	public RunAct(String context)
	{
		super(context);
	}

	@Override
	protected Script parse(String context)
	{
		return Scripts.getScript(context);
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		this.getValue().run(player, object);
	}

}