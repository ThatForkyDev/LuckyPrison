package com.ulfric.ess.commands;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.world.Weather;

public class CommandWeather extends SimpleCommand {


	public CommandWeather()
	{
		this.withArgument("type", ArgStrategy.WEATHER, Permissions.WEATHER_SET, "ess.weather_set_perm");
	}


	@Override
	public void run()
	{
		Player player = this.getPlayer();

		WeatherType type = (WeatherType) this.getObject("type");

		if (type == null)
		{
			Locale.send(player, "ess.weather", Weather.parse(player.getWorld().hasStorm()).name().toLowerCase());

			return;
		}

		this.getPlayer().getWorld().setStorm(type.equals(WeatherType.DOWNFALL));

		Locale.sendSuccess(player, "ess.weather_set", type.name().toLowerCase());
	}


	public static class CommandRain extends SimpleCommand
	{

		@Override
		public void run()
		{
			Commands.dispatch(this.getSender(), "weather downfall");
		}

	}

	public static class CommandSun extends SimpleCommand
	{

		@Override
		public void run()
		{
			Commands.dispatch(this.getSender(), "weather clear");
		}

	}


}