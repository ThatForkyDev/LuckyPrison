package com.ulfric.prison.achievements;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.prison.achievement.Achievement;
import com.ulfric.prison.achievement.AchievementCategory;
import com.ulfric.prison.achievement.AchievementListener;
import com.ulfric.prison.crate.CrateOpenEvent;
import com.ulfric.prison.modules.ModuleMarriage.MarriageEvent;
import com.ulfric.prison.modules.ModuleRecords.PlayerBreakRecordEvent;

public class PrisonAchievementsMiscModule extends SimpleModule {

	public PrisonAchievementsMiscModule()
	{
		super("prison-achievements-misc", "Misc achievements for Prison", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		ItemBuilder builder = new ItemBuilder()
					.withType(Material.SIGN);

		AchievementCategory misc = AchievementCategory.builder()
														  .setName("misc")
														  .setItem(builder.withColoredName("&7Misc").build())
														  .build();

		Achievement.builder()
				.setName("tieddown")
				.setFormattedName("Tied Down")
				.setCategory(misc)
				.setMin(1)
				.setItem(builder.withType(Material.GOLD_RECORD).withColoredName("&7Tied Down").withLore("", "Get married").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
					public void onEvent(MarriageEvent event)
					{
						this.get().increment(PlayerUtils.proxy(event.getUUID1()), 1);
						this.get().increment(PlayerUtils.proxy(event.getUUID2()), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("resourceful")
				.setFormattedName("Resourceful")
				.setCategory(misc)
				.setMin(1)
				.setItem(builder.withType(Material.EMPTY_MAP).withColoredName("&7Resourceful").withLore("", "Use the resource pack").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.MONITOR)
					public void onEvent(PlayerResourcePackStatusEvent event)
					{
						if (!event.getStatus().equals(Status.SUCCESSFULLY_LOADED)) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("creeper")
				.setFormattedName("Creeper")
				.setCategory(misc)
				.setMin(1)
				.setItem(builder.withType(Material.SKULL_ITEM).withDurability((short) 3).withColoredName("&7Creeper").withLore("", "Use the /profile <name> command").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
					public void onEvent(PlayerCommandPreprocessEvent event)
					{
						if (!event.getMessage().startsWith("/profile ")) return;

						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement breaker = Achievement.builder()
				.setName("recordbreaker")
				.setFormattedName("Record Breaker")
				.setCategory(misc)
				.setMin(1)
				.setItem(builder.withType(Material.BOOK).withDurability((short) 0).withColoredName("&7Record Breaker").withLore("", "Break a daily player record").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(PlayerBreakRecordEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("recordsmasher")
				.setFormattedName("Record Smasher")
				.setCategory(misc)
				.setMin(10)
				.addParent(breaker)
				.setItem(builder.withType(Material.BOOK).withColoredName("&7Record Smasher").withLore("", "Break ten daily player records").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(PlayerBreakRecordEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement locksmith = Achievement.builder()
				.setName("locksmith")
				.setFormattedName("Locksmith")
				.setCategory(misc)
				.setMin(1)
				.setItem(builder.withType(Material.TRIPWIRE_HOOK).withColoredName("&7Locksmith").withLore("", "Open 1 crate", ChatColor.YELLOW + "Reward: " + ChatColor.GRAY + "1x m8 Key").build())
				.addReward(player -> Commands.dispatch(Strings.format("givekey {0} m8", player.getName())))
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(CrateOpenEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();

		Achievement.builder()
				.setName("locksmith2")
				.setFormattedName("Locksmith II")
				.setCategory(misc)
				.setMin(10)
				.addParent(locksmith)
				.addReward(player -> Commands.dispatch(Strings.format("givekey {0} kappa", player.getName())))
				.setItem(builder.withType(Material.TRIPWIRE_HOOK).withColoredName("&7Locksmith II").withLore("", "Open 10 crates", ChatColor.YELLOW + "Reward: " + ChatColor.GRAY + "1x Kappa Key").build())
				.addListener(new AchievementListener()
				{
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onEvent(CrateOpenEvent event)
					{
						this.get().increment(event.getPlayer(), 1);
					}
				})
				.build();
	}

}