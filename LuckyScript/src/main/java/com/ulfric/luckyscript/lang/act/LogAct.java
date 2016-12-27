package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.luckyscript.LuckyScript;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class LogAct extends Action<String> {

	public LogAct(String context)
	{
		super(context);
	}

	@Override
	protected String parse(String context) { return Chat.color(context); }

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		LuckyScript.get().log(this.getValue().replace(Strings.PLAYER, player.getName()));
	}

}