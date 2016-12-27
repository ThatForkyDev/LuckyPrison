package com.ulfric.prison.modules;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.DataHook.IPlayerData;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.SimpleModule;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ModuleCoupons extends SimpleModule {

	public ModuleCoupons()
	{
		super("coupons", "Coupon codes module", "Packet", "1.0.0-REL");

		this.withConf();
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("givecoupon", new CommandGivecoupon());
		this.addCommand("coupons", new CommandCoupons());
		this.addCommand("markread", new CommandMarkread());
	}

	// Note, we read and write from
	// the config as we need / use
	// coupons. This is to prevent
	// re-use in the event of a data
	// loss.

	private class CommandGivecoupon extends SimpleCommand
	{
		private CommandGivecoupon()
		{
			this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
			this.withArgument("amt", ArgStrategy.POSITIVE_INTEGER, "prison.givecoupon_amt_required");
		}

		@Override
		public void run()
		{
			// FUCK SAFETY, I WANT MONEY

			OfflinePlayer player = (OfflinePlayer) this.getObject("player");

			String amt = this.getObject("amt").toString();

			List<String> coupons = ModuleCoupons.this.getConf().getConf().getStringList(amt);

			Hooks.DATA.addToPlayerDataCollection(player.getUniqueId(), "prison.coupons.unclaimed", coupons.remove(RandomUtils.nextInt(coupons.size())));

			ModuleCoupons.this.setConfObject(amt, coupons);

			if (!player.isOnline()) return;

			Locale.send(player.getPlayer(), "prison.coupon_granted", '$' + amt);
		}
	}

	private class CommandCoupons extends SimpleCommand
	{
		@Override
		public void run()
		{
			Player player = this.getPlayer();
			UUID uuid = player.getUniqueId();

			List<String> list = Hooks.DATA.getPlayerDataAsStringList(uuid, "prison.coupons.claimed");

			if (!CollectUtils.isEmpty(list))
			{
				StringBuilder builder = new StringBuilder();

				for (String coupon : list)
				{
					builder.append(ChatColor.RED + "- " + coupon);
				}

				Locale.send(player, "prison.coupon_list_claimed", builder.toString());
			}

			list = Hooks.DATA.getPlayerDataAsStringList(uuid, "prison.coupons.unclaimed");

			if (CollectUtils.isEmpty(list))
			{
				Locale.send(player, "prison.coupons_none");

				return;
			}

			ComponentBuilder message = new ComponentBuilder(Locale.getMessage(player, "prison.coupons_header"));

			for (String coupon : list)
			{
				message.append("\n");

				message.append("- ");

				message.color(ChatColor.GRAY);

				message.append(coupon);

				message.color(ChatColor.GREEN);

				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 TextComponent.fromLegacyText(ChatColor.AQUA + "Clip to copy")));

				message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, coupon));

				message.append(" ");

				message.append("/");

				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/markread " + coupon));

				message.color(ChatColor.DARK_RED);

				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 TextComponent.fromLegacyText(ChatColor.RED + "Mark as read")));
			}

			player.sendMessage(message.create());
		}
	}

	private class CommandMarkread extends SimpleCommand
	{
		private CommandMarkread()
		{
			this.withArgument("str", ArgStrategy.ENTERED_STRING, "system.error");
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			String str = (String) this.getObject("str");

			IPlayerData data = Hooks.DATA.getPlayerData(player.getUniqueId());

			List<String> coupons = data.getStringList("prison.coupons.unclaimed");

			if (CollectUtils.isEmpty(coupons) || !coupons.contains(str)) return;

			coupons.remove(str);

			data.set("prison.coupons.unclaimed", coupons);

			coupons = data.getStringList("prison.coupons.claimed");

			if (coupons == null)
			{
				coupons = Lists.newArrayList();
			}

			coupons.add(str);

			data.set("prison.coupons.claimed", coupons);
		}
	}

}
