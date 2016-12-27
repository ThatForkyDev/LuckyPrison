package com.ulfric.luckyscript.lang.act;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.block.BlockPattern;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.inventory.MaterialUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class BlockiterAct extends Action<Object> implements IterAction {

	public BlockiterAct(String context)
	{
		super(context);
	}

	private boolean player;
	private int radius;
	private int heightRadius;
	private boolean shuffle;
	private int cap;
	private Set<ItemPair> ignore;
	private boolean ignoreAir;
	private boolean smashonly;
	private BlockPattern pattern;

	@Override
	protected Object parse(String context)
	{
		context = context.toLowerCase();

		this.objects = Lists.newArrayList();

		this.shuffle = context.contains("--shuffle");

		String radString = StringUtils.findOption(context, "radius");
		String patString;
		if (radString != null)
		{
			this.radius = Assert.notNull(Numbers.parseInteger(radString), "The parsed radius cannot be null!");

			String hradius = StringUtils.findOption(context, "hradius");

			if (hradius != null)
			{
				this.heightRadius = Assert.notNull(Numbers.parseInteger(hradius), "The parsed hradius cannot be null!");
			}
			else
			{
				this.heightRadius = this.radius;
			}
		}
		else if ((patString = StringUtils.findOption(context, "pattern")) != null)
		{
			this.pattern = Assert.notNull(BlockPattern.getPattern(patString));
		}
		else
		{
			Assert.isTrue(false, "No radius or pattern specified!");
		}

		this.player = context.contains(Strings.PLAYER);

		String ignore = StringUtils.findOption(context, "ignore");

		if (ignore != null)
		{
			this.ignore = Sets.newHashSet();

			String[] split = ignore.split("\\,");

			for (String part : split)
			{
				this.ignore.add(MaterialUtils.pair(part));
			}
		}

		this.ignoreAir = context.contains("--noair");

		this.smashonly = context.contains("--smashonly");

		String cap = StringUtils.findOption(context, "cap");

		if (cap == null) return null;

		this.cap = Assert.notNull(Numbers.parseInteger(cap), "The parsed cap cannot be null!");

		return null;
	}

	private List<LocatableMetadatable> objects;
	@Override
	public synchronized List<LocatableMetadatable> getObjects() { return this.objects; }

	@Override
	public synchronized void run(Player player, LocatableMetadatable object)
	{
		this.objects.clear();

		Location location = (this.player ? player : object).getLocation();

		if (this.pattern == null)
		{
			World world = location.getWorld();

			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();

			for (int i = -this.radius; i < this.radius; i++)
			{
				for (int j = -this.radius; j < this.radius; j++)
				{
					for (int k = -this.heightRadius; k < this.heightRadius; k++)
					{
						int lx = x + i;
						int ly = y + k;
						int lz = z + j;

						Block block = new Location(world, lx, ly, lz).getBlock();

						if ((this.ignoreAir && block.getType().equals(Material.AIR)) || ((this.smashonly && !BlockUtils.isSmashable(block)) || (this.ignore != null && this.ignore.contains(MaterialUtils.pair(block))))) continue;

						this.objects.add(block);
					}
				}
			}
		}
		else
		{
			for (Location vector : this.pattern.getLocations(location))
			{
				Block block = vector.getBlock();

				if ((this.ignoreAir && block.getType().equals(Material.AIR)) || ((this.smashonly && !BlockUtils.isSmashable(block)) || (this.ignore != null && this.ignore.contains(MaterialUtils.pair(block))))) continue;

				this.objects.add(block);
			}
		}

		if (this.shuffle)
		{
			Collections.shuffle(this.objects);
		}

		int size = this.objects.size();
		if (this.cap > 0 && size > this.cap)
		{
			int rot = size - this.cap;
			Iterator<LocatableMetadatable> iter = this.objects.iterator();
			for (int i = 0; i < rot; i++)
			{
				iter.next();

				iter.remove();
			}
		}
	}

}