package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class MetaAct extends Action<Pair<String, String>[]> {

	public MetaAct(String context)
	{
		super(context);
	}

	private boolean remove;
	private boolean player;
	private long ticks;

	@Override
	protected Pair<String, String>[] parse(String context)
	{
		context = context.toLowerCase();

		String tags = StringUtils.findOption(context, "tags");

		Assert.notNull(tags, "No metadata tags found!");

		this.remove = context.contains("--remove");

		this.player = context.contains(Strings.PLAYER);

		String timeStr = StringUtils.findOption(context, "ticks");

		if (timeStr != null)
		{
			this.ticks = Numbers.parseLong(timeStr);
		}

		String[] split = tags.split("\\,");

		@SuppressWarnings("unchecked")
		Pair<String, String>[] tuples = new Pair[split.length];

		for (int x = 0; x < split.length; x++)
		{
			String part = split[x];

			String[] parts = part.split("\\=");

			if (parts.length == 1)
			{
				tuples[x] = Tuples.newPair(part, null);

				continue;
			}

			tuples[x] = Tuples.newPair(parts[0], parts[1]);
		}

		return tuples;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		if (this.remove)
		{
			for (Pair<String, String> path : this.getValue())
			{
				Metadata.remove(this.player ? player : object, path.getA());
			}

			return;
		}

		if (this.ticks == 0)
		{
			for (Pair<String, String> path : this.getValue())
			{
				Metadata.apply(this.player ? player : object, path.getA(), path.getB());
			}

			return;
		}

		for (Pair<String, String> path : this.getValue())
		{
			Metadata.applyTemp(this.player ? player : object, path.getA(), path.getB(), this.ticks);
		}
	}

}