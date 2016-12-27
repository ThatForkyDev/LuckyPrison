package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal2;
import com.intellectualcrafters.plot.object.RunnableVal3;
import com.plotsquared.general.commands.Argument;
import com.plotsquared.general.commands.Command;

/**
 * SubCommand class
 *
 * @Deprecated In favor of normal Command class
 * @see Command(Command, boolean)
 */
public abstract class SubCommand extends Command {
	protected SubCommand()
	{
		super(MainCommand.getInstance(), true);
	}

	protected SubCommand(Argument... arguments)
	{
		this();
		this.setRequiredArguments(arguments);
	}

	@Override
	public void execute(PlotPlayer player, String[] args, RunnableVal3<Command, Runnable, Runnable> confirm, RunnableVal2<Command, CommandResult> whenDone)
	{
		this.onCommand(player, args);
	}

	public abstract boolean onCommand(PlotPlayer player, String... args);

	public boolean sendMessage(PlotPlayer player, C message, Object... args)
	{
		message.send(player, args);
		return true;
	}
}
