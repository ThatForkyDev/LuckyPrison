package com.ulfric.prison.scoreboard;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.hook.DataHook.IPlayerData;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.prison.lang.Permissions;
import com.ulfric.prison.scoreboard.element.BalanceElement;
import com.ulfric.prison.scoreboard.element.GodmodeElement;
import com.ulfric.prison.scoreboard.element.NextRankElement;
import com.ulfric.prison.scoreboard.element.RankElement;
import com.ulfric.prison.scoreboard.element.ScoreboardElement;
import com.ulfric.prison.scoreboard.element.VanishElement;

public enum ScoreboardPanel implements Iterable<ScoreboardElement> {

	INSTANCE;

	private ScoreboardPanel()
	{
		this.registerElement(new VanishElement());
		this.registerElement(new GodmodeElement());
		this.registerElement(new BalanceElement());
		this.registerElement(new RankElement());
		this.registerElement(new NextRankElement());
	}

	private final List<ScoreboardElement> elements = Lists.newArrayList();

	public void registerElement(ScoreboardElement element)
	{
		int place = Math.min(element.getIndex(), this.elements.size());

		this.elements.add(place, element);
	}

	public void updateAll(Player player)
	{
		Scoreboard scoreboardStale = player.getScoreboard();
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		boolean createStale = false;
		UUID uuid = player.getUniqueId();

		if (scoreboardStale == null || scoreboardStale == scoreboardManager.getMainScoreboard())
		{
			scoreboardStale = scoreboardManager.getNewScoreboard();

			if (scoreboardStale == null) return;

			player.setScoreboard(scoreboardStale);

			createStale = true;
		}

		final Scoreboard scoreboard = scoreboardStale;
		final boolean create = createStale;

		ScoreboardUpdater.submitSidebar(uuid, () ->
		{
			Objective objective = null;
			do
			{
				try
				{
					objective = scoreboard.registerNewObjective(String.valueOf(RandomUtils.nextInt(1000)), "luckyprison");
				}
				catch (IllegalArgumentException exception)
				{
					
				}
				catch (Exception exception)
				{
					return;
				}
			}
			while (objective == null);

			int count = 0;

			ListIterator<ScoreboardElement> iter = this.elements.listIterator(this.elements.size());
			while (iter.hasPrevious())
			{
				ScoreboardElement element = iter.previous();

				String result = element.apply(player);

				if (result == null || (result = result.trim()).isEmpty()) continue;

				objective.getScore(result).setScore(count++);

				String name = element.getName();

				if (name != null)
				{
					objective.getScore(Locale.getMessage(player, name)).setScore(count++);
				}

				objective.getScore(Chat.invisibleSpace(count)).setScore(count++);
			}

			Objective other = scoreboard.getObjective(DisplaySlot.SIDEBAR);
			if (other != null)
			{
				other.unregister();
			}

			objective.setDisplayName(ChatColor.GOLD + Strings.BLANK + ChatColor.BOLD + "LUCKY PRISON");

			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		});

		ScoreboardUpdater.submitTeam(uuid, () ->
		{
			String color = Strings.BLANK;

			if (create)
			{
				IPlayerData data = Hooks.DATA.getPlayerData(uuid);

				Team team = scoreboard.registerNewTeam("you");

				char colorChar = data.getChar("nameplate.color");

				if (colorChar != '\0')
				{
					ChatColor chatColor = ChatColor.getByChar(colorChar);

					if (chatColor != null)
					{
						color = chatColor.toString();
					}
				}

				team.setPrefix(Locale.getMessage(player, "prison.sb_you") + color);

				team.addEntry(player.getName());

				team = scoreboard.registerNewTeam("staff");

				team.setPrefix(Locale.getMessage(player, "prison.sb_staff"));

				Set<Player> staff = PlayerUtils.getOnlinePlayersWithPermission(Permissions.STAFF_TAG, true);

				if (!staff.isEmpty())
				{
					staff.remove(player);

					for (Player staffMember : staff)
					{
						team.addEntry(staffMember.getName());
					}
				}
			}

			if (color != null && !color.isEmpty())
			{
				for (Player onlinePlayer : PlayerUtils.getOnlinePlayersExceptFor(player))
				{
					Scoreboard board = onlinePlayer.getScoreboard();

					Team colorTeam = board.getTeam(color);

					if (colorTeam == null)
					{
						colorTeam = board.registerNewTeam(color);

						colorTeam.setPrefix(color);
					}

					colorTeam.addEntry(player.getName());
				}
			}

			if (player.hasPermission(Permissions.STAFF_TAG))
			{
				for (Player onlinePlayer : PlayerUtils.getOnlinePlayersExceptFor(player))
				{
					Scoreboard board = onlinePlayer.getScoreboard();

					Team staff = board.getTeam("staff");

					if (staff == null) continue;

					staff.addEntry(player.getName());
				}
			}
		});
	}

	@Override
	public Iterator<ScoreboardElement> iterator()
	{
		return this.elements.iterator();
	}

}