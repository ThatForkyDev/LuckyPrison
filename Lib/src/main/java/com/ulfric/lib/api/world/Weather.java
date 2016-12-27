package com.ulfric.lib.api.world;

import org.bukkit.WeatherType;

public final class Weather {

	static IWeather impl = IWeather.EMPTY;

	private Weather()
	{
	}

	public static WeatherType parse(String string)
	{
		return impl.parse(string);
	}

	public static WeatherType parse(int value)
	{
		return impl.parse(value);
	}

	public static WeatherType parse(boolean value)
	{
		return impl.parse(value);
	}

	protected interface IWeather {
		IWeather EMPTY = new IWeather() {
		};

		default WeatherType parse(String string)
		{
			return null;
		}

		default WeatherType parse(int value)
		{
			return null;
		}

		default WeatherType parse(boolean value)
		{
			return null;
		}
	}

}
