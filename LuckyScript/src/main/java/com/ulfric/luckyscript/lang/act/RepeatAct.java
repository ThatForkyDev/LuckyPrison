package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.math.Numbers;
import com.ulfric.luckyscript.lang.annotation.Script;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class RepeatAct extends Action<Integer> implements LookaheadAction {

	public RepeatAct(String context)
	{
		super(context);
	}

	@Override
	protected Integer parse(String context)
	{
		Integer value = Numbers.parseInteger(context);

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