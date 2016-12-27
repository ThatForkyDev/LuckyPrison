package com.ulfric.actions.hook;

import java.util.function.Supplier;

import org.bukkit.entity.Player;

import com.ulfric.actions.persist.LogFile;
import com.ulfric.lib.api.hook.ActionsHook.IActionsHook;

public enum ActionsImpl implements IActionsHook {

	INSTANCE;

	@Override
	public void log(Player player, String message)
	{
		LogFile.log(player, message);
	}

	@Override
	public void log(Player player, Supplier<String> callable)
	{
		LogFile.log(player, callable);
	}

}