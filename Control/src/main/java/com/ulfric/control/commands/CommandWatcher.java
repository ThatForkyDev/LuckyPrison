package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.control.lang.Meta;
import com.ulfric.control.tasks.TaskWatcher;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandWatcher extends SimpleCommand {

	public CommandWatcher()
	{
		this.withEnforcePlayer();

		this.withArgument("int", ArgStrategy.POSITIVE_INTEGER);
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		TaskWatcher task = (TaskWatcher) Metadata.getValueAsTask(player, Meta.WATCHER_RUN);

		if (!this.hasObjects())
		{
			if (task == null)
			{
				Locale.sendError(player, "control.watcher_amount_err");

				return;
			}

			Locale.sendError(player, "control.watcher_end", task.getTeleports());

			task.annihilate();

			Metadata.remove(player, Meta.WATCHER_RUN);

			return;
		}

		int delay = Math.min(Math.max(3, (int) this.getObject("int")), 120);

		if (task != null)
		{
			task.annihilate();

			int oldDelay = task.getDelay();

			task.setDelay(delay);

			task.start();

			Locale.sendSuccess(player, "control.watcher_change", oldDelay, delay);

			return;
		}

		task = new TaskWatcher(player, delay);

		task.start();

		Metadata.apply(player, Meta.WATCHER_RUN, task);

		Locale.sendSuccess(player, "control.watcher", delay);
	}

}