package com.ulfric.prison.enchantments;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Sets;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.prison.modules.ModuleSell;

public final class EnchantmentAutosell extends PrisonEnchantment {

	private static final EnchantmentAutosell INSTANCE = new EnchantmentAutosell();
	public static EnchantmentAutosell get() { return EnchantmentAutosell.INSTANCE; }
	private final Task task = new Task();

	private EnchantmentAutosell()
	{
		super(200, "AUTOSELL");
	}

	@Override
	public EnchantmentTarget getItemTarget()
	{
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel()
	{
		return 3;
	}

	public Task getMainTask()
	{
		return this.task;
	}

	public final class Task implements Runnable
	{
		private final Set<UUID> uuids = Sets.newHashSet();
		private int counter = 1;

		public void addPlayer(UUID uuid)
		{
			this.uuids.add(uuid);
		}

		public void removePlayer(UUID uuid)
		{
			this.uuids.remove(uuid);
		}

		public void start()
		{
			Tasks.runRepeating(this, 20 * 9);
		}

		@Override
		public void run()
		{
			Iterator<UUID> iterator = this.uuids.iterator();

			while (iterator.hasNext())
			{
				UUID next = iterator.next();

				Player player = Bukkit.getPlayer(next);

				if (player == null)
				{
					iterator.remove();

					continue;
				}

				ItemStack item = player.getItemInHand();

				if (!ItemUtils.isEmpty(item))
				{
					int level = item.getEnchantmentLevel(EnchantmentAutosell.get());

					if (level > 0)
					{
						if (level < this.counter) continue;

						ModuleSell.get().sellall(player);

						return;
					}
				}

				iterator.remove();
			}

			if (this.counter++ <= 3) return;

			this.counter = 1;
		}
	}

}