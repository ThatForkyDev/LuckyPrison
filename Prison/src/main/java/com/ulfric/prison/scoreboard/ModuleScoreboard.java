package com.ulfric.prison.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.prison.lang.Permissions;

public class ModuleScoreboard extends SimpleModule {

	public static final ModuleScoreboard INSTANCE = new ModuleScoreboard();

	private ModuleScoreboard()
	{
		super("scoreboard", "Scoreboards module", "Packet", "1.0.0-SNAPSHOT");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			long delay = Ticks.fromSeconds(1.5);
			@EventHandler
			public void onJoin(PlayerJoinEvent event)
			{
				Player player = event.getPlayer();

				Tasks.runLater(() ->
				{
					ScoreboardPanel.INSTANCE.updateAll(player);

					if (!player.hasPermission(Permissions.STAFF_TAG)) return;

					Tasks.runAsync(() ->
					{
						for (Player lplayer : PlayerUtils.getOnlinePlayersExceptFor(player))
						{
							Scoreboard board = lplayer.getScoreboard();

							if (board.getObjectives().isEmpty()) break;

							Team staff = board.getTeam("staff");

							if (staff == null)
							{
								staff = board.registerNewTeam("staff");
							}

							staff.addEntry(player.getName());
						}
					});
				}, this.delay);
			}
		});
	}

	@Override
	public void postEnable()
	{
		ScoreboardUpdater.INSTANCE.start();
	}

}