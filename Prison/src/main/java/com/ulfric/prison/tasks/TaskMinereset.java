package com.ulfric.prison.tasks;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.block.MultiBlockChange;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.RegionHook.Region;
import com.ulfric.lib.api.inventory.WeightedItemPair;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.WeightedWrapper;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.lib.api.world.Worlds;
import com.ulfric.prison.entity.MineWrapper;

public class TaskMinereset extends ATask implements Listener {

	private static final TaskMinereset INSTANCE = new TaskMinereset();
	public static TaskMinereset get() { return TaskMinereset.INSTANCE; }

	private TaskMinereset() { }

	private final Map<Region, MineWrapper> map = Maps.newHashMap();
	public void put(MineWrapper wrapper)
	{
		for (Region region : wrapper.getMines().keySet())
		{
			this.map.put(region, wrapper);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onRegionBlock(BlockBreakEvent event)
	{
		List<Region> regions = Hooks.REGIONS.at(event.getBlock().getLocation());

		if (CollectUtils.isEmpty(regions)) return;

		for (Region region : regions)
		{
			MineWrapper wrapper = this.map.get(region);

			if (wrapper == null) continue;

			if (!this.touched.add(wrapper)) continue;

			break;
		}
	}

	@Override
	public void start()
	{
		super.start();

		this.setTaskId(Tasks.runRepeatingAsync(this, Ticks.fromSeconds(25)).getTaskId());
	}

	private final Lock lock = new ReentrantLock();

	private Set<MineWrapper> last = ImmutableSet.of();

	private Set<MineWrapper> touched = Sets.newHashSet();

	@Override
	public void run()
	{
		if (!this.lock.tryLock()) return;

		Set<MineWrapper> useable = this.touched;

		if (useable.isEmpty())
		{
			this.lock.unlock();

			return;
		}

		int cycle = Math.min(useable.size(), 6);
		Set<MineWrapper> wrappers = Sets.newHashSetWithExpectedSize(cycle);

		while (cycle >= 0)
		{
			cycle--;

			MineWrapper wrapper = RandomUtils.randomValueFromCollection(useable);

			if (this.last.contains(wrapper)) continue;

			wrappers.add(wrapper);
		}

		if (wrappers.isEmpty())
		{
			this.lock.unlock();

			return;
		}

		this.touched.removeAll(wrappers);

		this.last = wrappers;

		Locale.sendMass("prison.mine_reset", StringUtils.mergeNicely(CollectUtils.toStringCollect(wrappers)));

		for (MineWrapper wrapper : wrappers)
		{
			for (Entry<Region, WeightedWrapper<List<WeightedItemPair>>> entry : wrapper.getMines().entrySet())
			{
				Region region = entry.getKey();

				WeightedWrapper<List<WeightedItemPair>> items = entry.getValue();

				double maxY = region.getMax().getY() + 2;

				for (Player player : Bukkit.getOnlinePlayers())
				{
					Location location = player.getLocation();

					if (!region.contains(location)) continue;

					player.teleport(location.setY(maxY));
				}

				MultiBlockChange change = BlockUtils.newMultiChange(200);

				for (Vector blockVector : region)
				{
					WeightedItemPair pair = RandomUtils.getWeighted(items.getValue(), items.getWeight());

					change.addBlock(blockVector.toLocation(Worlds.main()), pair);
				}

				if (change.isEmpty()) continue;

				change.run();
			}
		}

		this.lock.unlock();
	}

}