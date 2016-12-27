package com.ulfric.prison.achievements;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.prison.achievement.Achievement;
import com.ulfric.prison.achievement.AchievementCategory;
import com.ulfric.prison.achievement.AchievementListener;
import com.ulfric.prison.entity.LuckyBlocks;

public class PrisonAchievementsMiningModule extends SimpleModule {

	public PrisonAchievementsMiningModule()
	{
		super("prison-achievements-mining", "Mining achievements for Prison", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		ItemBuilder builder = new ItemBuilder()
					.withType(Material.DIAMOND_PICKAXE);

		AchievementCategory mining = AchievementCategory.builder()
														  .setName("mining")
														  .setItem(builder.withColoredName("&7Mining").build())
														  .build();

		Achievement lucky1 = Achievement.builder()
				.setName("lucky1")
				.setFormattedName("Lucky I")
				.setCategory(mining)
				.setMin(5)
				.setItem(builder.withType(Material.SPONGE).withColoredName("&7Lucky I").withLore("", "Mine 5 LuckyBlocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						if (!LuckyBlocks.isLuckyBlock(event.getBlock())) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement lucky2 = Achievement.builder()
				.setName("lucky2")
				.setFormattedName("Lucky II")
				.setCategory(mining)
				.addParent(lucky1)
				.setMin(100)
				.setItem(builder.withColoredName("&7Lucky II").withLore("", "Mine 100 LuckyBlocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						if (!LuckyBlocks.isLuckyBlock(event.getBlock())) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("lucky3")
				.setFormattedName("Lucky III")
				.setCategory(mining)
				.addParent(lucky2)
				.setMin(1000)
				.setItem(builder.withColoredName("&7Lucky III").withLore("", "Mine 1,000 LuckyBlocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						if (!LuckyBlocks.isLuckyBlock(event.getBlock())) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement coal1 = Achievement.builder()
				.setName("coalminer1")
				.setFormattedName("Coal Miner I")
				.setCategory(mining)
				.setMin(100000)
				.setItem(builder.withType(Material.COAL_BLOCK).withColoredName("&7Coal Miner I").withLore("", "Mine 100,000 coal blocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						if (!PrisonAchievementsMiningModule.this.isCoal(event.getBlock())) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement coal2 = Achievement.builder()
				.setName("coalminer2")
				.setFormattedName("Coal Miner II")
				.setCategory(mining)
				.addParent(coal1)
				.setMin(1000000)
				.setItem(builder.withColoredName("&7Coal Miner II").withLore("", "Mine 1,000,000 coal blocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						if (!PrisonAchievementsMiningModule.this.isCoal(event.getBlock())) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("coalminer3")
				.setFormattedName("Coal Miner III")
				.setCategory(mining)
				.addParent(coal2)
				.setMin(5000000)
				.setItem(builder.withColoredName("&7Coal Miner III").withLore("", "Mine 5,000,000 coal blocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						if (!PrisonAchievementsMiningModule.this.isCoal(event.getBlock())) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("diglett")
				.setFormattedName("Diglett")
				.setCategory(mining)
				.setMin(1000000000)
				.setItem(builder.withType(Material.DIAMOND_PICKAXE).withColoredName("&7Diglett").withLore("", "Mine 1,000,000,000 blocks").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BlockBreakEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();
	}

	private boolean isCoal(Block block)
	{
		Material type = block.getType();

		return type == Material.COAL || type == Material.COAL_BLOCK;
	}

}