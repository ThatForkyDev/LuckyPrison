package com.ulfric.control.modules;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleDelentity extends SimpleModule {

	public ModuleDelentity()
	{
		super("del-entity", "Entity deleter module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("delentity", new CommandDelentity());

		this.addListener(new Listener()
		{
			@EventHandler
			public void onClickEntity(PlayerInteractAtEntityEvent event)
			{
				Entity entity = event.getRightClicked();

				switch (entity.getType())
				{
					case COMPLEX_PART:
					case FISHING_HOOK:
					case UNKNOWN:
					case WEATHER:
					case PLAYER:
						return;

					default:
						break;
				}

				Player player = event.getPlayer();

				if (!player.hasMetadata("_ulf_delentity")) return;

				entity.remove();
			}
		});
	}

	private class CommandDelentity extends SimpleCommand
	{
		private CommandDelentity()
		{
			this.withEnforcePlayer();

			this.withArgument("near", new ExactArg("--n", "--near"));
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			if (this.hasObject("near"))
			{
				List<Entity> entities = player.getNearbyEntities(1.5, 1.5, 1.5);

				if (CollectUtils.isEmpty(entities))
				{
					// TODO error

					return;
				}

				// TODO success

				Entity entity = entities.get(0);

				switch (entity.getType())
				{
					case COMPLEX_PART:
					case FISHING_HOOK:
					case UNKNOWN:
					case WEATHER:
					case PLAYER:
						return;

					default:
						break;
				}

				entity.remove();

				return;
			}

			if (Metadata.removeIfPresent(player, "_ulf_delentity"))
			{
				Locale.send(player, "control.delentity_disabled");

				return;
			}

			Metadata.applyNull(player, "_ulf_delentity");

			Locale.send(player, "control.delentity_enabled");
		}
	}

}