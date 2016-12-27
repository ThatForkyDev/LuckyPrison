package com.intellectualcrafters.plot.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.object.comment.CommentInbox;
import com.intellectualcrafters.plot.object.comment.InboxOwner;
import com.intellectualcrafters.plot.object.comment.InboxPublic;
import com.intellectualcrafters.plot.object.comment.InboxReport;
import com.intellectualcrafters.plot.object.comment.PlotComment;

public final class CommentManager {

	public static final HashMap<String, CommentInbox> inboxes = new HashMap<>();

	private CommentManager() {}

	public static void sendTitle(PlotPlayer player, Plot plot)
	{
		if (!Settings.Enabled_Components.COMMENT_NOTIFIER || !plot.isOwner(player.getUUID()))
		{
			return;
		}
		TaskManager.runTaskLaterAsync(() ->
									  {
										  Collection<CommentInbox> boxes = inboxes.values();
										  AtomicInteger count = new AtomicInteger(0);
										  AtomicInteger size = new AtomicInteger(boxes.size());
										  for (CommentInbox inbox : inboxes.values())
										  {
											  inbox.getComments(plot, new RunnableVal<List<PlotComment>>() {
												  @Override
												  public void run(List<PlotComment> value)
												  {
													  int total;
													  if (value != null)
													  {
														  int num = 0;
														  for (PlotComment comment : value)
														  {
															  if (comment.timestamp > getTimestamp(player, inbox.toString()))
															  {
																  num++;
															  }
														  }
														  total = count.addAndGet(num);
													  }
													  else
													  {
														  total = count.get();
													  }
													  if ((size.decrementAndGet() == 0) && (total > 0))
													  {
														  AbstractTitle.sendTitle(player, "", C.INBOX_NOTIFICATION.s().replaceAll("%s", String.valueOf(total)));
													  }
												  }
											  });
										  }
									  }, 20);
	}

	public static long getTimestamp(PlotPlayer player, String inbox)
	{
		return player.getMeta("inbox:" + inbox, player.getLastPlayed());
	}

	public static void addInbox(CommentInbox inbox)
	{
		inboxes.put(inbox.toString().toLowerCase(), inbox);
	}

	public static void registerDefaultInboxes()
	{
		addInbox(new InboxReport());
		addInbox(new InboxPublic());
		addInbox(new InboxOwner());
	}
}
