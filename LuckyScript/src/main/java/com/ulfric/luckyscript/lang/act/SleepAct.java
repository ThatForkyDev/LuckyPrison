package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.luckyscript.lang.annotation.Script;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class SleepAct extends Action<Long> implements SpecialAction {

	public SleepAct(String context)
	{
		super(context);
	}

	@Override
	protected Long parse(String context)
	{
		Long value = Numbers.parseLong(StringUtils.makeNumeric(context));

		if (value == null) throw new ScriptParseException(context);

		context = context.toLowerCase();

		if (context.contains("sec"))
		{
			value = Ticks.fromSeconds(value);
		}
		else if (context.contains("min"))
		{
			value = Ticks.fromMinutes(value);
		}

		return value;
	}

	@Override
	@Script(canRun = false)
	public void run(Player player, LocatableMetadatable object)
	{
		throw new UnsupportedOperationException();
	}

}