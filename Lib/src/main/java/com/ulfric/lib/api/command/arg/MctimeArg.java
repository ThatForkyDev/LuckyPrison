package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.world.Clock;
import com.ulfric.lib.api.world.TimeType;

final class MctimeArg implements ArgStrategy<TimeType> {

	@Override
	public TimeType match(String string)
	{
		TimeType type;

		try
		{
			long parsed = Long.parseLong(string);

			type = Clock.parse(parsed);
		}
		catch (NumberFormatException exception)
		{
			type = Clock.parse(string.toUpperCase());
		}

		return type;
	}

}
