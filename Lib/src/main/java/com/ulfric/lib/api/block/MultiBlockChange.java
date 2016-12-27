package com.ulfric.lib.api.block;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.task.Tasks;
import org.bukkit.Location;

import java.util.Map;
import java.util.Set;

public final class MultiBlockChange implements Runnable {

	private final int capacity;
	private final Map<Location, ItemPair> blocks;
	private long delay;

	public MultiBlockChange(int capacity)
	{
		this.capacity = capacity;
		this.blocks = Maps.newHashMapWithExpectedSize(capacity);
	}

	public Set<Map.Entry<Location, ItemPair>> getBlocks()
	{
		return this.blocks.entrySet();
	}

	public boolean isEmpty()
	{
		return this.blocks.isEmpty();
	}

	public boolean isFull()
	{
		return this.blocks.size() >= this.capacity;
	}

	private void setBlocks()
	{
		for (Map.Entry<Location, ItemPair> entry : this.blocks.entrySet())
		{
			ItemPair item = entry.getValue();

			entry.getKey().getBlock().setTypeAndData(item.getType(), item.getData().byteValue(), false);
		}

		this.blocks.clear();

		this.delay -= 2;
	}

	@Override
	public void run()
	{
		this.delay += 2;
		Tasks.runLater(this::setBlocks, this.delay);
	}

	public void addBlock(Location location, ItemPair pair)
	{
		this.blocks.put(location, pair);

		if (!this.isFull()) return;

		this.setBlocks();
	}

}
