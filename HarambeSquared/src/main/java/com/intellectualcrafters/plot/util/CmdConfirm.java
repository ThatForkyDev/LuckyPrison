package com.intellectualcrafters.plot.util;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.CmdInstance;
import com.intellectualcrafters.plot.object.PlotPlayer;

public final class CmdConfirm {

	private CmdConfirm() {}

	public static CmdInstance getPending(PlotPlayer player)
	{
		return player.getMeta("cmdConfirm");
	}

	public static void removePending(PlotPlayer player)
	{
		player.deleteMeta("cmdConfirm");
	}

	public static void addPending(PlotPlayer player, String commandStr, Runnable runnable)
	{
		removePending(player);
		MainUtil.sendMessage(player, C.REQUIRES_CONFIRM, commandStr);
		TaskManager.runTaskLater(() ->
								 {
									 CmdInstance cmd = new CmdInstance(runnable);
									 player.setMeta("cmdConfirm", cmd);
								 }, 1);
	}
}
