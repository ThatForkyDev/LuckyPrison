package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.world.Weather;
import org.bukkit.WeatherType;

final class WeatherArg implements ArgStrategy<WeatherType> {

	@Override
	public WeatherType match(String string)
	{
		Integer number = Numbers.parseInteger(string);
		return number != null ? Weather.parse(number) : Weather.parse(string.toUpperCase());
	}
}
