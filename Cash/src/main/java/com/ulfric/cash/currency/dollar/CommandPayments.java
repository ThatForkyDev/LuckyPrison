package com.ulfric.cash.currency.dollar;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.chat.ChatBook;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Timestamp;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;

public class CommandPayments extends SimpleCommand {

	public CommandPayments()
	{
		this.withArgument("page", ArgStrategy.INTEGER, 0);

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "cash.payments.other");

		this.cache = Maps.newHashMap();
	}

	private Map<UUID, Pair<Timestamp, ChatBook>> cache;

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");
		UUID uuid = target.getUniqueId();

		List<String> data = Hooks.DATA.getPlayerDataAsStringList(uuid, "currency.history");

		if (CollectUtils.isEmpty(data))
		{
			Locale.sendError(this.getPlayer(), "cash.payments_none");

			return;
		}

		CommandSender sender = this.getSender();

		final int pageNumber = Math.max(0, (int) this.getObject("page")-1);

		Pair<Timestamp, ChatBook> cache = this.cache.get(uuid);

		if (cache != null && Milliseconds.toMinutes(cache.getA().timeSince()) <= 10)
		{
			cache.getB().send(sender, pageNumber);

			return;
		}

		Locale.send(this.getPlayer(), "cash.payments_fetching", data.size());

		Tasks.runAsync(() ->
		{
			ChatBook book = Chat.newChatBook("cash.payments_footer", "/payments", 7);

			ListIterator<String> iter = data.listIterator(data.size());
			while (iter.hasPrevious())
			{
				String value = iter.previous();

				book.addMessage(Chat.newStringMessage(Strings.format("{0} {1} {2} {3}",
						Strings.space(StringUtils.findOption(value, "ti")),
						StringUtils.formatMoneyFully(Numbers.parseDouble(StringUtils.findOption(value, "ch")), true),
						StringUtils.formatMoneyFully(Numbers.parseDouble(StringUtils.findOption(value, "ne")), true),
						Strings.space(StringUtils.findOption(value, "re")))));
			}

			CommandPayments.this.cache.put(target.getUniqueId(), Tuples.newPair(Timestamp.now(), book));

			book.send(this.getSender(), pageNumber);
		});
	}

}