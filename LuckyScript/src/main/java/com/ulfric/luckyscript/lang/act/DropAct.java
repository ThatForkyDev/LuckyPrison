package com.ulfric.luckyscript.lang.act;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

@SuppressWarnings("unchecked")
public class DropAct extends Action<ItemStack> {

	public DropAct(String context)
	{
		super(context);
	}

	private boolean player;
	private boolean naturally;
	private boolean round;
	private Vector shift;
	private Pair<String, String>[] meta;

	@Override
	protected ItemStack parse(String context)
	{
		ItemStack item = Assert.notNull(ItemUtils.fromString(context));

		context = context.toLowerCase();

		this.player = context.contains("--player");

		this.naturally = context.contains("--natural");

		this.round = context.contains("--round");

		String shift = StringUtils.findOption(context, "shift");

		if (shift != null)
		{
			this.shift = LocationUtils.vectorFromString(shift);
		}

		String meta = StringUtils.findOption(context, "meta");

		if (meta != null)
		{
			String[] metas = meta.split("\\,");

			this.meta = new Pair[metas.length];

			for (int x = 0; x < metas.length; x++)
			{
				String metastr = metas[x];

				String[] parts = metastr.split("\\=");

				String other = null;

				if (parts.length > 1)
				{
					other = parts[1];
				}

				this.meta[x] = Tuples.newPair(Strings.space(parts[0]), other);
			}
		}

		return item;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		Location location = (this.player ? player : object).getLocation();

		if (this.round) LocationUtils.round(location);

		if (this.shift != null)
		{
			location = location.add(this.shift);
		}

		Item item;

		if (!this.naturally)
		{
			item = location.getWorld().dropItem(location, this.getValue());
		}
		else
		{
			item = location.getWorld().dropItemNaturally(location, this.getValue());
		}

		if (this.meta == null) return;

		for (Pair<String, String> meta : this.meta)
		{
			Object value = null;

			String right = meta.getB();
			if (right != null)
			{
				if (right.equals(Strings.PLAYER))
				{
					value = player;
				}
				else
				{
					value = right;
				}
			}

			Metadata.apply(item, meta.getA(), value);
		}
	}

}