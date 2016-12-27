package com.ulfric.prison.modules;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.server.Events;
import com.ulfric.prison.entity.LuckyBlocks;

public class ModuleGrenade extends SimpleModule {

	private final ItemStack dpick = new ItemStack(Material.DIAMOND_PICKAXE);

	public ModuleGrenade()
	{
		super("grenade", "Grenade item module", "Packet", "1.0.0-REL");

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
			public void onThrow(ProjectileLaunchEvent event)
			{
				if (!event.getEntityType().equals(EntityType.THROWN_EXP_BOTTLE)) return;

				Projectile projectile = event.getEntity();

				if (!(projectile.getShooter() instanceof Player)) return;

				Player player = (Player) projectile.getShooter();

				
				ItemStack item = player.getItemInHand();

				ItemMeta meta;

				if (!item.hasItemMeta() || !(meta = item.getItemMeta()).hasDisplayName()) return;

				if (!meta.getDisplayName().equals(ChatColor.RED + "Grenade")) return;

				projectile.setVelocity(projectile.getVelocity().multiply(1.35).add(0, 0.15, 0));

				Metadata.tieToPlayer(projectile, player);

				Metadata.applyNull(projectile, "_ulf_grenade");
			}

			@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
			public void onProjectile(ProjectileHitEvent event)
			{
				if (!event.getEntityType().equals(EntityType.THROWN_EXP_BOTTLE)) return;

				Projectile projectile = event.getEntity();

				if (!projectile.hasMetadata("_ulf_grenade")) return;

				Player player = Metadata.getTied(projectile);

				Block block = projectile.getLocation().getBlock();

				World world = block.getWorld();
				int bx = block.getX();
				int by = block.getY();
				int bz = block.getZ();

				int radius = 7;
				int radhalf = (radius/2)+1;

				List<Block> blocks = Lists.newArrayList();

				for (int x = bx; x+1 < bx+radius; x++)
				for (int y = by-1; y < by+radhalf; y++)
				for (int z = bz; z+1 < bz+radius; z++)
				{
					blocks.add(world.getBlockAt(x, y, z));
				}

				Collections.shuffle(blocks);

				int check = (int) (blocks.size()/1.25);

				for (int x = 0; x < check; x++)
				{
					this.playBreakable(blocks.get(x), player);
				}

				player.playEffect(block.getLocation(), Effect.EXPLOSION_LARGE, null);
				player.playSound(block.getLocation(), Sound.EXPLODE, 1.5F, 1F);
			}

			private void playBreakable(Block breakable, Player player)
			{
				if (LuckyBlocks.isLuckyBlock(breakable) || !BlockUtils.isSmashable(breakable)) return;

				Metadata.applyNull(breakable, "_ulf_fakebreak");

				if (!Events.call(new BlockBreakEvent(breakable, player)).isCancelled())
				{
					breakable.setType(Material.AIR, false);

					ItemStack item = player.getItemInHand();

					if (item != null && item.getType() == Material.EXP_BOTTLE)
					{
						Collection<ItemStack> drops = breakable.getDrops(ModuleGrenade.this.dpick);

						if (!CollectUtils.isEmpty(drops))
						{
							InventoryUtils.giveOrDrop(player, drops.toArray(new ItemStack[drops.size()]));
						}
					}
				}

				Metadata.remove(breakable, "_ulf_fakebreak");
			}
		});
	}

}