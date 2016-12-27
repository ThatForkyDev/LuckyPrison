package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class SendAct extends Action<String> {

	public SendAct(String context)
	{
		super(context);
	}

	@Override
	public String parse(String context) { return Chat.color(context.replace(Strings.FAKE_LINEBREAK, "\n")); }

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		player.sendMessage(this.getValue().replace(Strings.PLAYER, player.getName()));
	}

}