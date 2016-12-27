package com.ulfric.control.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ulfric.control.lang.Meta;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleSnoop extends SimpleModule {

	public ModuleSnoop()
	{
		super("snoop", "Command snooper module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("snoop", new CommandSnoop());

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
			public void onCommand(PlayerCommandPreprocessEvent event)
			{
				if (ModuleSnoop.this.count == 0) return;

				Locale.sendMassMeta(Meta.SNOOP_DOG, "control.snoopdogg", event.getPlayer().getName(), event.getMessage());
			}

			@EventHandler
			public void onLogout(PlayerQuitEvent event)
			{
				Player player = event.getPlayer();

				if (!Metadata.removeIfPresent(player, Meta.SNOOP_DOG)) return;

				ModuleSnoop.this.count--;
			}
		});
	}

	private volatile int count;

	private class CommandSnoop extends SimpleCommand
	{
		private CommandSnoop()
		{
			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			boolean value = Metadata.removeIfPresent(player, Meta.SNOOP_DOG);

			if (!value)
			{
				Metadata.applyNull(player, Meta.SNOOP_DOG);

				ModuleSnoop.this.count++;
			}
			else
			{
				ModuleSnoop.this.count--;
			}

			Locale.sendSuccess(player, "control.snoopdogg_toggle", Booleans.fancify(!value, player));
		}
	}

}