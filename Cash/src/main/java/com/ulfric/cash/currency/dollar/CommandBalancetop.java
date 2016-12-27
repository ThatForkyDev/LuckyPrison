package com.ulfric.cash.currency.dollar;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.chat.ChatBook;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.task.Tasks;

public class CommandBalancetop extends SimpleCommand {


	public CommandBalancetop()
	{
		this.withArgument("page", ArgStrategy.INTEGER, 1);
	}

	private volatile long lastUpdated;

	private Map<String, Integer> rankings;
	private String totalBalance;
	private ChatBook book;

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		int index = Math.max((int) this.getObject("page"), 1);

		if (this.rankings != null && this.book != null && System.currentTimeMillis() - this.lastUpdated < 300000)
		{
			this.send(sender, index);

			return;
		}

		this.rankings = Maps.newHashMap();
		this.book = Chat.newChatBook("cash.balancetop_footer", "/balancetop", 7);

		this.lastUpdated = System.currentTimeMillis();
		OfflinePlayer[] offline = Bukkit.getOfflinePlayers();
		Collection<? extends Player> online = Bukkit.getOnlinePlayers();

		Locale.send(sender, "cash.balancetop_fetch", offline.length + online.size());

		Tasks.runAsync(() ->
		{
			TreeMap<Double, String> map = Maps.newTreeMap(new Comparator<Double>()
			{

				@Override
				public int compare(Double o1, Double o2)
				{
					return o2.compareTo(o1);
				}

			});

			for (OfflinePlayer player : offline)
			{
				map.put(Money.getBalance(player.getUniqueId()), player.getName());
			}

			for (Player player : online)
			{
				map.put(Money.getBalance(player.getUniqueId()), player.getName());
			}


			double total = 0;

			int x = 0;
			for (Entry<Double, String> entry : map.entrySet())
			{
				double balance = entry.getKey();

				total += balance;

				String fbalance = StringUtils.formatNumber(balance);

				String value = ChatColor.GREEN + "$" + fbalance + (balance >= 1000 ? Strings.formatF("&6 ({0}{1})", fbalance.split(",")[0], StringUtils.formatShortWordNumber(balance)) : Strings.BLANK);

				String player = entry.getValue();

				this.rankings.put(player, x++);

				this.book.addMessage(Chat.newStringMessage(Strings.formatF("&6{0}) {1} {2}", x, player, value)));
			}

			this.totalBalance = StringUtils.formatMoneyFully(total);

			this.send(sender, index);
		});
	}

	private void send(CommandSender sender, int page)
	{
		

		Locale.send(sender, "cash.balancetop_header");
		Locale.send(sender, "cash.balancetop_total", this.totalBalance);
		if (sender instanceof Player)
		{
			Integer rank = this.rankings.get(sender.getName());

			Locale.send(sender, "cash.balancetop_rank", rank == null ? "UNKNOWN" : rank+1);
		}
		this.book.send(sender, page-1);
	}


}