package com.ulfric.prison.modules;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;

public class ModuleClearlag extends SimpleModule implements Runnable {

	public ModuleClearlag()
	{
		super("clearlag", "Prevents lag on the server", "Packet", "1.0.0-REL");

		this.addCommand("clearlag", new CommandClearlag());
		this.addCommand("nextclear", new CommandNextclear());
	}

	private class CommandClearlag extends SimpleCommand
	{
		@Override
		public void run()
		{
			ModuleClearlag.this.manualRun();
		}
	}

	private class CommandNextclear extends SimpleCommand
	{
		@Override
		public void run()
		{
			Locale.send(this.getSender(), "prison.clearlag_next", TimeUtils.millisecondsToString(ModuleClearlag.this.timestamp.timeTill()));
		}
	}

	private int task;

	@Override
	public void postEnable()
	{
		this.timestamp = Timestamp.future(Milliseconds.fromMinutes(4));

		this.task = Tasks.runRepeating(this, Ticks.fromMinutes(3)).getTaskId();
	}

	@Override
	public void postDisable()
	{
		Tasks.cancel(this.task);
	}

	private Timestamp timestamp;

	@Override
	public void run()
	{
		Tasks.runLater(() ->
		{
			this.timestamp.setFuture(Milliseconds.fromMinutes(3));

			this.manualRun();
		}, Ticks.fromMinutes(1));
	}

	public void manualRun()
	{
		int entities = 0;
		int chunks = 0;

		for (Entity entity : EntityUtils.getAllEntities())
		{
			if (entity instanceof Player ||
				entity instanceof EnderDragon) continue;

			else if (entity instanceof ArmorStand)
			{
				ArmorStand stand = (ArmorStand) entity;

				if (!stand.isVisible()) continue;

				boolean cont = false;

				for (ItemStack item : stand.getEquipment().getArmorContents())
				{
					if (ItemUtils.isEmpty(item)) continue;

					cont = true;

					break;
				}

				if (cont) continue;
			}

			else if (entity instanceof Projectile)
			{
				if (entity.getTicksLived() <= 100) continue;
			}

			else if (entity instanceof Item)
			{
				ItemStack stack = ((Item) entity).getItemStack();

				if (EnchantUtils.getWeight(stack) >= 5) continue;

				Material material = stack.getType();
				if (material.equals(Material.CHEST) || (material.equals(Material.GOLDEN_APPLE) && stack.getDurability() == 1)) continue;

				ItemMeta meta;
				if (stack.hasItemMeta() && ((meta = stack.getItemMeta()).hasDisplayName() || meta.hasLore())) continue;
			}

			else if (entity instanceof Tameable)
			{
				Tameable tameable = (Tameable) entity;

				if (tameable.isTamed()) continue;
			}

			else if (entity instanceof Monster)
			{
				Monster monster = (Monster) entity;

				if (monster.getTarget() != null && monster.getHealth() < monster.getMaxHealth()) continue;
			}

			else if (entity instanceof ItemFrame)
			{
				ItemFrame frame = (ItemFrame) entity;

				if (!ItemUtils.isEmpty(frame.getItem())) continue;
			}

			else if (entity instanceof StorageMinecart)
			{
				StorageMinecart minecart = (StorageMinecart) entity;

				if (!InventoryUtils.isEmpty(minecart.getInventory())) continue;
			}

			else if (entity instanceof FishHook) continue;

			if (entity.getPassenger() != null)
			{
				if (entity.getPassenger() instanceof Player) continue;
			}

			if (entity.hasMetadata("_ulf_temporary")) continue;

			if (Hooks.PETS.isPet(entity)) continue;

			entity.remove();
			entities++;
		}

		for (World world : Bukkit.getWorlds())
		{
			for (Chunk chunk : world.getLoadedChunks())
			{
				if (!chunk.unload(true, true)) continue;

				chunks++;
			}
		}

		Locale.sendMass("prison.clearlag", entities, chunks);
	}

}