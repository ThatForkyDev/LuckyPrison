package com.ulfric.prison.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.ulfric.lib.api.hook.EconHook.BalanceChangeEvent;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.prison.achievement.Achievement;
import com.ulfric.prison.achievement.AchievementCategory;
import com.ulfric.prison.achievement.AchievementListener;
import com.ulfric.prison.events.PlayerPurchaseEnchantmentEvent;
import com.ulfric.prison.events.PlayerRankupEvent;

public class PrisonAchievementsMoneyModule extends SimpleModule {

	public PrisonAchievementsMoneyModule()
	{
		super("prison-achievements-money", "Money achievements for Prison", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		ItemBuilder builder = new ItemBuilder()
					.withType(Material.PAPER);

		AchievementCategory physical = AchievementCategory.builder()
														  .setName("money")
														  .setItem(builder.withColoredName("&7Money").build())
														  .build();

		Achievement millionare = Achievement.builder()
				.setName("millionare")
				.setFormattedName("Millionare")
				.setCategory(physical)
				.setMin(1)
				.setItem(builder.withType(Material.SUGAR_CANE).withColoredName("&7Millionare").withLore("", "Get a million dollars").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BalanceChangeEvent event)
					{
						if (event.getNewBalance() < 1000000) return;

						this.get().increment(PlayerUtils.proxy(event.getUniqueId()), 1);
					}
				})
				.build();

		Achievement billionare = Achievement.builder()
				.setName("billionare")
				.setFormattedName("Billionare")
				.setCategory(physical)
				.setMin(1)
				.addParent(millionare)
				.setItem(builder.withType(Material.SUGAR_CANE).withAmount(2).withColoredName("&7Billionare").withLore("", "Get a billion dollars").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BalanceChangeEvent event)
					{
						if (event.getNewBalance() < 1000000000) return;
		
						this.get().increment(PlayerUtils.proxy(event.getUniqueId()), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("trillionare")
				.setFormattedName("Trillionare")
				.setCategory(physical)
				.setMin(1)
				.addParent(billionare)
				.setItem(builder.withType(Material.SUGAR_CANE).withAmount(3).withColoredName("&7Trillionare").withLore("", "Get a trillion dollars").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(BalanceChangeEvent event)
					{
						if (event.getNewBalance() < 1000000000000L) return;
		
						this.get().increment(PlayerUtils.proxy(event.getUniqueId()), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("movinonup")
				.setFormattedName("Movin' On Up")
				.setCategory(physical)
				.setMin(3)
				.setItem(builder.withType(Material.EXP_BOTTLE).withColoredName("&7Movin' On Up").withLore("", "Rankup three times").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(PlayerRankupEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();


		Achievement.builder()
				.setName("hogwarts")
				.setFormattedName("Hogwarts")
				.setCategory(physical)
				.setMin(1)
				.setItem(builder.withType(Material.ENCHANTMENT_TABLE).withAmount(1).withColoredName("&7Hogwarts").withLore("", "Add an enchant to your", "pickaxe (/warp enchant)").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(PlayerPurchaseEnchantmentEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();
	}

}