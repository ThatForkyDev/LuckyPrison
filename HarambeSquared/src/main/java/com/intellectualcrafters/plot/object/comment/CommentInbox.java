package com.intellectualcrafters.plot.object.comment;

import java.util.List;

import com.intellectualcrafters.plot.database.DBFunc;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;

public interface CommentInbox {

	boolean canRead(Plot plot, PlotPlayer player);

	boolean canWrite(Plot plot, PlotPlayer player);

	boolean canModify(Plot plot, PlotPlayer player);

	/**
	 * <br>
	 * The `whenDone` parameter should be executed when it's done fetching the comments.
	 * The value should be set to List of comments
	 *
	 * @param plot
	 * @param whenDone
	 * @return
	 */
	boolean getComments(Plot plot, RunnableVal<List<PlotComment>> whenDone);

	boolean addComment(Plot plot, PlotComment comment);

	default void removeComment(Plot plot, PlotComment comment)
	{
		DBFunc.removeComment(plot, comment);
	}

	default void clearInbox(Plot plot)
	{
		DBFunc.clearInbox(plot, this.toString());
	}
}
