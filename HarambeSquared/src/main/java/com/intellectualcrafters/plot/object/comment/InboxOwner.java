package com.intellectualcrafters.plot.object.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.Permissions;
import com.intellectualcrafters.plot.util.TaskManager;

public class InboxOwner implements CommentInbox {

	@Override
	public boolean canRead(Plot plot, PlotPlayer player)
	{
		if (Permissions.hasPermission(player, "plots.inbox.read." + this))
		{
			if (plot.isOwner(player.getUUID()) || Permissions.hasPermission(player, "plots.inbox.read." + this + ".other"))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(Plot plot, PlotPlayer player)
	{
		if (plot == null)
		{
			return Permissions.hasPermission(player, "plots.inbox.write." + this);
		}
		return Permissions.hasPermission(player, "plots.inbox.write." + this) && (plot.isOwner(player.getUUID()) || Permissions
																																  .hasPermission(player, "plots.inbox.write." + this + ".other"));
	}

	@Override
	public boolean canModify(Plot plot, PlotPlayer player)
	{
		if (Permissions.hasPermission(player, "plots.inbox.modify." + this))
		{
			if (plot.isOwner(player.getUUID()) || Permissions.hasPermission(player, "plots.inbox.modify." + this + ".other"))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getComments(Plot plot, RunnableVal<List<PlotComment>> whenDone)
	{
		Optional<ArrayList<PlotComment>> comments = plot.getSettings().getComments(this.toString());
		if (comments.isPresent())
		{
			whenDone.value = comments.get();
			TaskManager.runTask(whenDone);
			return true;
		}
		DBFunc.getComments(plot, this.toString(), new RunnableVal<List<PlotComment>>() {
			@Override
			public void run(List<PlotComment> value)
			{
				whenDone.value = value;
				if (value != null)
				{
					for (PlotComment comment : value)
					{
						plot.getSettings().addComment(comment);
					}
				}
				else
				{
					plot.getSettings().setComments(new ArrayList<>());
				}
				TaskManager.runTask(whenDone);
			}
		});
		return true;
	}

	@Override
	public boolean addComment(Plot plot, PlotComment comment)
	{
		if (plot.owner == null)
		{
			return false;
		}
		plot.getSettings().addComment(comment);
		DBFunc.setComment(plot, comment);
		return true;
	}

	@Override
	public String toString()
	{
		return "owner";
	}
}
