package com.ulfric.chat.modules;

import java.util.Iterator;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ulfric.chat.lang.Meta;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModulePlotads extends SimpleModule {

	public ModulePlotads()
	{
		super("plotads", "Plot advertisement opt-out", "Packet", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
			public void onChat(AsyncPlayerChatEvent event)
			{
				Player player = event.getPlayer();

				if (player.hasPermission("chat.adbypass")) return;

				if (!new PlotadAlgorithm(player, event.getMessage().toLowerCase(), 0).getAsBoolean()) return;

				if (Hooks.DATA.getPlayerDataAsBoolean(player.getUniqueId(), Meta.PLOT_ADS))
				{
					event.setCancelled(true);

					Locale.sendError(player, "chat.enable_plot_ads");

					return;
				}

				Iterator<Player> iter = event.getRecipients().iterator();

				while (iter.hasNext())
				{
					if (!Hooks.DATA.getPlayerDataAsBoolean(iter.next().getUniqueId(), Meta.PLOT_ADS)) continue;

					iter.remove();
				}
			}
		});
	}

	private class PlotadAlgorithm implements BooleanSupplier
	{
		public PlotadAlgorithm(Player player, String message, float startingLevel)
		{
			this.player = player;

			this.message = message;

			this.startingLevel = startingLevel;
		}

		private final float startingLevel;

		private final Player player;

		private final String message;

		@Override
		public boolean getAsBoolean()
		{
			String name = this.player.getName().toLowerCase();
			String message = this.message;

			float level = this.startingLevel;

			for (Adcheck check : Adcheck.values())
			{
				level += check.match(name, message);

				if (level < 4) continue;

				break;
			}

			return level >= 4 ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	private enum Adcheck
	{
		PLOT("plot", 0.925F),
		SHOP("shop", 1.1F),
		SELL("sell", 0.91F),
		BUY("buy", 0.91F),
		PH("/p h", 1.9F),
		RAR("->", 0.4F),
		LAR("<-", 0.4F),
		LOOK1("l()()k", 1.44F),
		LOOK2("l{}{}k", 1.44F),
		DONATE("donate", 0.15F),
		CASINO("casino", 1.4F),

		NAME
		{
			@Override
			public float match(String name, String message)
			{
				return message.contains(name) ? 1.3F : 0F;
			}
		},

		ASCII
		{
			Pattern pattern = Pattern.compile("[^\\p{ASCII}]+");

			@Override
			public float match(String name, String message)
			{
				return this.pattern.matcher(message).find() ? 1.8F : 0F;
			}
		};

		private final String find;

		private final float worth;

		Adcheck()
		{
			this(Strings.BLANK, 0F);
		}

		Adcheck(String find, float worth)
		{
			this.find = find;

			this.worth = worth;
		}

		public float match(String name, String message)
		{
			return message.contains(this.find) ? this.worth : 0F;
		}
	}

}