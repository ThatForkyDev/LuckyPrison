package com.ulfric.lib;

import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class RestrictorModule extends SimpleModule {

	public RestrictorModule()
	{
		super("restrictor", "Pre-enable server restrictions", "Packet", "1.0.0-REL");

		this.addListener(new Listener() {
			@EventHandler
			public void onJoin(PlayerLoginEvent event)
			{
				if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;

				event.setResult(PlayerLoginEvent.Result.KICK_OTHER);

				event.setKickMessage(ChatColor.GOLD + "The server is still loading...");
			}
		});
	}

}
