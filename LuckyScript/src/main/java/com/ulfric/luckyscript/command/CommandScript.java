package com.ulfric.luckyscript.command;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.luckyscript.lang.Script;
import com.ulfric.luckyscript.lang.ScriptArg;

public class CommandScript extends SimpleCommand {

	public CommandScript()
	{
		this.withArgument("script", ScriptArg.INSTANCE, "script.script_needed");
	}

	@Override
	public void run()
	{
		Script script = (Script) this.getObject("script");

		Player player = this.getPlayer();

		script.run(player, PlayerUtils.getTargetBlock(player, 6));

		player.sendMessage("script ran");
	}

}