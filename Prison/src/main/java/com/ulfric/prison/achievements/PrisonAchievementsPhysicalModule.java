package com.ulfric.prison.achievements;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.achievement.Achievement;
import com.ulfric.prison.achievement.AchievementCategory;
import com.ulfric.prison.achievement.AchievementListener;

public class PrisonAchievementsPhysicalModule extends SimpleModule {

	public PrisonAchievementsPhysicalModule()
	{
		super("prison-achievements-physical", "Physical achievements for Prison", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		ItemBuilder builder = new ItemBuilder()
					.withType(Material.ANVIL);

		AchievementCategory physical = AchievementCategory.builder()
														  .setName("physical")
														  .setItem(builder.withColoredName("&7Physical").build())
														  .build();

		Achievement.builder()
				.setName("wrongbed")
				.setFormattedName("The Wrong Side of The Bed")
				.setCategory(physical)
				.setMin(1)
				.setItem(builder.withType(Material.BED).withColoredName("&7The Wrong Side of The Bed").withLore("", "Place a bed in the Nether").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockPlaceEvent event)
					{
						if (!event.getBlock().getType().equals(Material.BED_BLOCK)) return;

						Player player = event.getPlayer();

						if (!player.getWorld().getEnvironment().equals(Environment.NETHER)) return;

						this.get().increment(player, 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("strawman")
				.setFormattedName("Strawman")
				.setCategory(physical)
				.setMin(1)
				.setItem(builder.withType(Material.HAY_BLOCK).withDurability((short) 1).withColoredName("&7Strawman").withLore("", "Place a hay bale").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockPlaceEvent event)
					{
						if (!event.getBlock().getType().equals(Material.HAY_BLOCK)) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("milkybrew")
				.setFormattedName("Milky Brew")
				.setCategory(physical)
				.setMin(1000)
				.setItem(builder.withType(Material.MILK_BUCKET).withColoredName("&7Milky Brew").withLore("", "Consume 1,000 milk buckets").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerItemConsumeEvent event)
					{
						if (!event.getItem().getType().equals(Material.MILK_BUCKET)) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement munchies = Achievement.builder()
				.setName("munchies")
				.setFormattedName("The Munchies")
				.setCategory(physical)
				.setMin(3)
				.setItem(builder.withType(Material.COOKIE).withColoredName("&7The Munchies").withLore("", "Consume three cookies").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerItemConsumeEvent event)
					{
						if (!event.getItem().getType().equals(Material.COOKIE)) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("notch")
				.setFormattedName("Notch's Munchies")
				.setCategory(physical)
				.setMin(3)
				.addParent(munchies)
				.setItem(builder.withType(Material.GOLDEN_APPLE).withDurability((short) 1).withColoredName("&7Notch's Munchies").withLore("", "Consume three notch apples").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerItemConsumeEvent event)
					{
						ItemStack item = event.getItem();
		
						if (!item.getType().equals(Material.GOLDEN_APPLE)) return;
		
						if (item.getDurability() != 1) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();
	}

}