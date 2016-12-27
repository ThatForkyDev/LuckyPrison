package com.ulfric.prison.achievements;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerDamagePlayerEvent;
import com.ulfric.prison.achievement.Achievement;
import com.ulfric.prison.achievement.AchievementCategory;
import com.ulfric.prison.achievement.AchievementListener;

public class PrisonAchievementsFightModule extends SimpleModule {

	public PrisonAchievementsFightModule()
	{
		super("prison-achievements-fight", "Fight achievements for Prison", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		ItemBuilder builder = new ItemBuilder()
					.withType(Material.DIAMOND_SWORD);

		AchievementCategory physical = AchievementCategory.builder()
														  .setName("fight")
														  .setItem(builder.withColoredName("&7PvP").build())
														  .build();

		Achievement.builder()
				.setName("ownerslap")
				.setFormattedName("OWNERSLAP!")
				.setCategory(physical)
				.setMin(1)
				.setItem(builder.withType(Material.SKULL_ITEM).withDurability((short) 3).withOwner("Packet").withColoredName("&7OWNERSLAP!").withLore("", "Get damaged by the Owner").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(PlayerDamagePlayerEvent event)
					{
						if (!event.getPlayer().getUniqueId().equals(Strings.PACKETS_UUID)) return;
		
						this.get().increment(event.getDamaged(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("pvp")
				.setFormattedName("PvP Pro Everywhere I Go")
				.setCategory(physical)
				.setMin(1)
				.setItem(builder.withType(Material.IRON_SWORD).withColoredName("&7PvP Pro Everywhere I Go").withLore("", "Kill another player").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(PlayerDeathEvent event)
					{
						Player player = event.getEntity().getKiller();

						if (player == null) return;

						this.get().increment(player, 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("ironman")
				.setFormattedName("Iron Man")
				.setCategory(physical)
				.setMin(3000)
				.setItem(builder.withType(Material.IRON_BLOCK).withColoredName("&7Iron Man").withLore("", "Kill 3,000 iron golems").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(EntityDeathEvent event)
					{
						if (!event.getEntityType().equals(EntityType.IRON_GOLEM)) return;

						Player player = event.getEntity().getKiller();

						if (player == null) return;

						this.get().increment(player, 1);
					}
				})
				.build();
	}

}