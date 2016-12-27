package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.math.Numbers;
import com.ulfric.luckyscript.lang.annotation.Script;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class AfterAct extends Action<Long> implements LookaheadAction {

	public AfterAct(String context)
	{
		super(context);
	}

	@Override
	protected Long parse(String context)
	{
		Long value = Numbers.parseLong(context);

		if (value == null) throw new ScriptParseException(context);

		return value;
	}

	@Override
	@Script(canRun = false)
	public void run(Player player, LocatableMetadatable object)
	{
		throw new UnsupportedOperationException();
	}

}