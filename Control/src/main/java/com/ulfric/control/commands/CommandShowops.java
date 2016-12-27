package com.ulfric.control.commands;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandShowops extends SimpleCommand {

	@Override
	public void run()
	{
		Locale.send(this.getSender(), "control.showops", StringUtils.mergeNicely(Bukkit.getOperators().stream().map(OfflinePlayer::getName).collect(Collectors.toList())));
	}

}