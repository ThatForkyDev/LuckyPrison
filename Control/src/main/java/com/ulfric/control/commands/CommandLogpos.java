package com.ulfric.control.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.Files;
import com.ulfric.control.Control;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.location.LocationUtils;

public class CommandLogpos extends SimpleCommand {

	@Override
	public void run()
	{
		StringBuilder builder = new StringBuilder();
		for (Player player : Bukkit.getOnlinePlayers())
		{
			builder.append(player.getName());
			builder.append('|');
			builder.append(LocationUtils.toString(player.getLocation(), true));
			builder.append('\n');
		}

		File file = new File(Control.get().getDataFolder(), "logpos.log");

		try
		{
			Files.write(builder, file, Charset.defaultCharset());
		}
		catch (IOException exception) { exception.printStackTrace(); }
	}

}