package com.intellectualcrafters.plot.commands;

import java.util.UUID;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.object.RunnableVal2;
import com.intellectualcrafters.plot.object.RunnableVal3;
import com.intellectualcrafters.plot.util.ByteArrayUtilities;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.general.commands.Command;
import com.plotsquared.general.commands.CommandDeclaration;

@CommandDeclaration(
		command = "grant",
		category = CommandCategory.CLAIMING,
		usage = "/plot grant <check|add> [player]",
		permission = "plots.grant",
		requiredType = RequiredType.NONE)
public class Grant extends Command {

	public Grant()
	{
		super(MainCommand.getInstance(), true);
	}

	@Override
	public void execute(PlotPlayer player, String[] args, RunnableVal3<Command, Runnable, Runnable> confirm, RunnableVal2<Command, CommandResult> whenDone) throws CommandException
	{
		this.checkTrue(args.length >= 1 && args.length <= 2, C.COMMAND_SYNTAX, this.getUsage());
		String arg0 = args[0].toLowerCase();
		switch (arg0)
		{
			case "add":
			case "check":
				if (!Permissions.hasPermission(player, "plots.grant." + arg0))
				{
					C.NO_PERMISSION.send(player, "plots.grant." + arg0);
					return;
				}
				if (args.length > 2)
				{
					break;
				}
				UUID uuid = args.length == 2 ? UUIDHandler.getUUIDFromString(args[1]) : player.getUUID();
				if (uuid == null)
				{
					C.INVALID_PLAYER.send(player, args[1]);
					return;
				}
				MainUtil.getPersistentMeta(uuid, "grantedPlots", new RunnableVal<byte[]>() {
					@Override
					public void run(byte[] array)
					{
						if ("check".equals(arg0))
						{ // check
							int granted = array == null ? 0 : ByteArrayUtilities.bytesToInteger(array);
							C.GRANTED_PLOTS.send(player, granted);
						}
						else
						{ // add
							int amount = 1 + (array == null ? 0 : ByteArrayUtilities.bytesToInteger(array));
							boolean replace = array != null;
							DBFunc.addPersistentMeta(uuid, "grantedPlots", ByteArrayUtilities.integerToBytes(amount), replace);
						}
					}
				});
		}
		C.COMMAND_SYNTAX.send(player, this.getUsage());
	}
}
