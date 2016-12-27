package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.luckyscript.lang.annotation.Script;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class SyncAct extends Action<Object> implements SpecialAction {

	public SyncAct(String context)
	{
		super(context);
	}

	@Override
	protected String parse(String context) { return null; }

	@Override
	@Script(canRun = false)
	public void run(Player player, LocatableMetadatable object)
	{
		throw new UnsupportedOperationException();
	}

}