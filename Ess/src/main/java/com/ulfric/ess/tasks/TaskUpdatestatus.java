package com.ulfric.ess.tasks;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.entity.StatusSign;
import com.ulfric.lib.api.block.SignUtils;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;

public class TaskUpdatestatus extends ATask {

	private static final TaskUpdatestatus INSTANCE = new TaskUpdatestatus();
	public static TaskUpdatestatus get() { return TaskUpdatestatus.INSTANCE; }

	private TaskUpdatestatus() { }

	@Override
	public void start()
	{
		super.start();

		this.setTaskId(Tasks.runRepeatingAsync(this, Ticks.fromSeconds(5), Ticks.fromMinutes(5)).getTaskId());
	}

	@Override
	public void run()
	{
		Set<StatusSign> statussigns = ConfigurationStore.get().getSigns();

		if (statussigns.isEmpty()) return;

		for (StatusSign sign : statussigns)
		{
			this.updateSign(sign);
		}
	}

	public void updateSign(StatusSign sign)
	{
		UUID uuid = sign.getUniqueId();

		String[] lines = new String[3];

		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

		lines[0] = (ChatColor.UNDERLINE + player.getName());

		lines[1] = (Chat.color(Hooks.PERMISSIONS.user(uuid).getGroups()[0].getSuffix()));

		Timestamp time = sign.getTime();

		time.setTime(player.getLastPlayed());

		long timeSince = time.timeSince();

		if (timeSince > (Milliseconds.HOUR*24))
		{
			timeSince /= Milliseconds.HOUR;
			timeSince *= Milliseconds.HOUR;
		}

		lines[2] = (player.isOnline() ? ChatColor.DARK_GREEN + "ONLINE" : ChatColor.RED + TimeUtils.millisecondsToString(timeSince, true) + " ago");

		Tasks.run(() -> SignUtils.setLines(sign.getSign(), lines));
	}

}