package com.ulfric.prison.achievements;

import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemFlag;

import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerMoveBlockEvent;
import com.ulfric.prison.achievement.Achievement;
import com.ulfric.prison.achievement.AchievementCategory;
import com.ulfric.prison.achievement.AchievementListener;

public class PrisonAchievementsMoveModule extends SimpleModule {

	public PrisonAchievementsMoveModule()
	{
		super("prison-achievements-move", "Movement achievements for Prison", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		ItemBuilder builder = new ItemBuilder()
					.withType(Material.POTION)
					.withDurability((short) 8258)
					.withFlag(ItemFlag.HIDE_ATTRIBUTES)
					.withFlag(ItemFlag.HIDE_POTION_EFFECTS);

		AchievementCategory movement = AchievementCategory.builder()
														  .setName("movement")
														  .setItem(builder.withColoredName("&7Movement").build())
														  .build();

		builder.withDurability((short) 0);

		Achievement tenThousandBlocks = Achievement.builder()
				.setName("10000blocks")
				.setFormattedName("Ten Thousand Steps")
				.setCategory(movement)
				.setMin(10000)
				.setItem(builder.withType(Material.DIRT).withColoredName("&7Ten Thousand Steps").withLore("", "Walk 10,000 blocks").build())
				.addListener(this.buildListener())
				.build();

		Achievement oneMillionBlocks = Achievement.builder()
				.setName("1000000blocks")
				.setFormattedName("Too Far Gone")
				.setCategory(movement)
				.addParent(tenThousandBlocks)
				.setMin(1000000)
				.setItem(builder.withColoredName("&7Too Far Gone").withLore("", "Walk 1,000,000 blocks").build())
				.addListener(this.buildListener())
				.build();

		Achievement.builder()
				.setName("1000000000blocks")
				.setFormattedName("Baby Steps")
				.setCategory(movement)
				.addParent(oneMillionBlocks)
				.setMin(1000000000)
				.setItem(builder.withColoredName("&7Baby Steps").withLore("", "Walk 1,000,000,000 blocks").build())
				.addListener(this.buildListener())
				.build();

		Achievement.builder()
				.setName("superman")
				.setFormattedName("Superman")
				.setCategory(movement)
				.setMin(1000)
				.setItem(builder.withType(Material.FEATHER).withColoredName("&7Superman").withLore("", "Start flying 1,000 times").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerToggleFlightEvent event)
					{
						if (!event.isFlying()) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("hotpotato")
				.setFormattedName("Hot Potato!")
				.setCategory(movement)
				.setMin(1000)
				.setItem(builder.withType(Material.BAKED_POTATO).withColoredName("&7Hot Potato!").withLore("", "Move 1,000 blocks while on fire").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerMoveBlockEvent event)
					{
						Player player = event.getPlayer();
		
						if (player.getFireTicks() == 0) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("ghastsahoy")
				.setFormattedName("Ghasts Ahoy!")
				.setCategory(movement)
				.setMin(1)
				.setItem(builder.withType(Material.GHAST_TEAR).withColoredName("&7Ghasts Ahoy!").withLore("", "Visit the Nether").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerChangedWorldEvent event)
					{
						Player player = event.getPlayer();
		
						if (!player.getWorld().getEnvironment().equals(Environment.NETHER)) return;
		
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();
	}

	private AchievementListener buildListener()
	{
		return new AchievementListener()
		{
			@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
			public void onEvent(PlayerMoveEvent event)
			{
				this.get().increment(event.getPlayer(), 1);
			}
		};
	}

}