package com.ulfric.prison.listeners;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.block.BlockPattern;
import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.server.Events;
import com.ulfric.prison.enchantments.EnchantmentAutosell;
import com.ulfric.prison.enchantments.EnchantmentBlasting;
import com.ulfric.prison.enchantments.EnchantmentFlight;
import com.ulfric.prison.enchantments.EnchantmentMagic;
import com.ulfric.prison.enchantments.EnchantmentNeverbreaking;
import com.ulfric.prison.enchantments.EnchantmentSpeedyGonzales;
import com.ulfric.prison.enchantments.LoadableEnchantment;
import com.ulfric.prison.enchantments.PrisonEnchantment;
import com.ulfric.prison.enchantments.loader.EnchantmentLoader;
import com.ulfric.prison.enchantments.loader.EnchantmentType;
import com.ulfric.prison.entity.LuckyBlocks;
import com.ulfric.prison.lang.Meta;
import com.ulfric.uspigot.event.player.PlayerItemStackHeldEvent;

public class ListenerEnchantments implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDamage(PlayerItemDamageEvent event)
	{
		ItemStack item = event.getItem();

		Player player = event.getPlayer();

		if (!item.containsEnchantment(EnchantmentNeverbreaking.get()))
		{
			if (!item.equals(player.getItemInHand())) return; // armor

			int maxDura = item.getType().getMaxDurability();

			if ((maxDura-item.getDurability()) >= maxDura/9) return;

			Locale.sendWarning(player, "prison.break_soon");

			return;
		}

		event.setCancelled(true);

		ItemMeta meta = item.getItemMeta();

		meta.spigot().setUnbreakable(true);

		item.setItemMeta(meta);

		player.updateInventory();
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemSwitch(PlayerItemStackHeldEvent event)
	{
		Player player = event.getPlayer();

		ItemStack item = event.getNewItem();

		ItemStack former = event.getCurrentItem();

		if (!ItemUtils.isEmpty(former) && former.hasItemMeta())
		{
			ItemMeta meta = former.getItemMeta();

			if (meta.hasEnchants())
			{
				Set<LoadableEnchantment> enchs = EnchantmentLoader.getEnchants(EnchantmentType.EFFECT);

				for (Enchantment ench : meta.getEnchants().keySet())
				{
					if (!(ench instanceof LoadableEnchantment)) continue;

					if (!enchs.contains(ench)) continue;

					LoadableEnchantment loadableEnchantment = ((LoadableEnchantment) ench);
					
					if (loadableEnchantment.getEffect().equals(PotionEffectType.NIGHT_VISION) && player.hasMetadata(Meta.NIGHT_VISION)) continue;
					player.removePotionEffect(loadableEnchantment.getEffect());
				}

				if (meta.hasEnchant(EnchantmentFlight.get()) && !player.hasMetadata("_ulf_allowfly"))
				{
					player.setAllowFlight(false);
				}

				if (meta.hasEnchant(EnchantmentSpeedyGonzales.get()))
				{
					player.setWalkSpeed(0.25F);
				}

				int level = meta.getEnchantLevel(EnchantmentAutosell.get());

				if (level > 0)
				{
					EnchantmentAutosell.get().getMainTask().addPlayer(player.getUniqueId());
				}
			}
		}

		if (ItemUtils.isEmpty(item)) return;

		if (!item.hasItemMeta()) return;

		ItemMeta meta = item.getItemMeta();

		if (!meta.hasEnchants()) return;

		if (meta.hasEnchant(EnchantmentFlight.get()))
		{
			player.setAllowFlight(true);
		}

		if (meta.hasEnchant(EnchantmentSpeedyGonzales.get()))
		{
			player.setWalkSpeed(0.35F);
		}

		Set<LoadableEnchantment> enchs = EnchantmentLoader.getEnchants(EnchantmentType.EFFECT);

		for (Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet())
		{
			Enchantment ench = enchantment.getKey();

			if (!(ench instanceof LoadableEnchantment)) continue;

			if (!enchs.contains(ench)) continue;

			((LoadableEnchantment) ench).apply(player, enchantment.getValue()-1);
		}
	}


	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();

		if (block.hasMetadata("_ulf_fakebreak")) return;

		Player player = event.getPlayer();

		ItemStack item = player.getItemInHand();

		if (ItemUtils.isEmpty(item)) return;

		boolean hasNeverbreaking = item.containsEnchantment(EnchantmentNeverbreaking.get());

		short maxDurability = item.getType().getMaxDurability();

		if (!hasNeverbreaking && maxDurability > 1 && item.getDurability() >= maxDurability)
		{
			Locale.sendError(player, "prison.tool_broken");
			event.setCancelled(true);
			player.updateInventory();
			return;
		}
		Location location = block.getLocation();
		World world = block.getWorld();

		Set<Block> blockSet = Sets.newHashSet();

		BlockPattern pattern;

		for (LoadableEnchantment ench : EnchantmentLoader.getEnchants(EnchantmentType.PATTERN))
		{
			if (!item.containsEnchantment(ench)) continue;

			pattern = ench.getPattern();

			if (pattern == null)
			{
				String patternName = ench.getPatternName();

				if (patternName == null) continue;

				pattern = BlockPattern.getPattern(patternName + item.getEnchantmentLevel(ench));

				if (pattern == null) continue;
			}

			for (Location vector : pattern.getLocations(location))
			{
				blockSet.add(vector.getBlock());
			}
		}

		if (player.hasMetadata("Qqq") && (pattern = BlockPattern.getPattern("qqq")) != null)
		{
			for (Location vector : pattern.getLocations(location))
			{
				Block lblock = vector.getBlock();

				if (LuckyBlocks.isLuckyBlock(lblock)) continue;

				blockSet.add(vector.getBlock());
			}
		}

		if (item.containsEnchantment(EnchantmentBlasting.get()))
		{
			final int bx = location.getBlockX();
			final int bz = location.getBlockZ();
			final int by = location.getBlockY();

			int level = item.getEnchantmentLevel(EnchantmentBlasting.get());
			int radius = Math.min(level + 2, 6);
			int max = (int) (level + Math.round((radius) / 1.75));

			List<Block> blocks = Lists.newArrayList();

			for (int x = bx; x+1 < bx+radius; x++)
			for (int y = by-1; y+1 < by+radius; y++)
			for (int z = bz; z+1 < bz+radius; z++)
			{
				Block lblock = world.getBlockAt(x, y, z);

				if (LuckyBlocks.isLuckyBlock(lblock)) continue;

				blocks.add(lblock);
			}

			Collections.shuffle(blocks);

			int check = max++ > blocks.size() ? blocks.size() : max;

			Iterator<Block> iter = blocks.iterator();
			while (check > 0 && iter.hasNext())
			{
				Block lblock = iter.next();

				if (LuckyBlocks.isLuckyBlock(lblock)) continue;

				blockSet.add(lblock);

				check--;
			}

			player.playEffect(block.getLocation(), Effect.EXPLOSION_LARGE, null);

			if (!Hooks.DATA.getPlayerDataAsBoolean(player.getUniqueId(), "prison.sound.explosions"))
			{
				player.playSound(block.getLocation(), Sound.EXPLODE, 1.25F, 1F);
			}
		}

		if (maxDurability > 1)
		{
			// Based on current minecraft logic, the way durability is handled is that for each block broken the chance of an item's
			// durability decreasing by one is 1 / (1 + unbreaking_level), where unbreaking_level is the level of the unbreaking
			// enchantment on the item.  This means that the number of uses is roughly equal to (unbreaking_level + 1) * max_durability
			// Due to the law of large numbers we want to calculate this chance for every single block broken, not once for all blocks
			// in the set.
			int unbreakingLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
			int durabilityCost = 0;
			// Once for the main block being broken.
			durabilityCost += RandomUtils.nextInt(unbreakingLevel + 1) == 0 ? 1 : 0;
			for (Block lblock : blockSet)
			{
				durabilityCost += RandomUtils.nextInt(unbreakingLevel + 1) == 0 ? 1 : 0;
				this.playBreakable(lblock, player);
			}
			short newDurability = (short) (item.getDurability() + durabilityCost);
			if (!hasNeverbreaking) {
				item.setDurability(newDurability);
			}
			player.updateInventory();
		}

		int level = item.getEnchantmentLevel(EnchantmentMagic.get());

		if (level <= 0) return;

		if (!RandomUtils.randomPercentage(20)) return;

		player.giveExp(level);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		// Check if we're in an anvil, and that they're clicking an anvil slot
		if (event.getView().getTopInventory() instanceof AnvilInventory && event.getRawSlot() == event.getSlot())
		{
			// Block interaction with the center slot if there's a fake enchantment.
			if ((event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) && event.getRawSlot() == 1)
			{
				if (hasFakeEnchantment(event.getView().getItem(0)) || (hasFakeEnchantment(event.getCursor())) )
				{
					event.setResult(Event.Result.DENY);
				}

				return;
			}

			// Handle clicking on the result slot
			if (event.getRawSlot() != 2) return;

			ItemStack item = event.getCurrentItem();
			ItemStack trueItem = event.getView().getItem(0).clone();

			if (ItemUtils.isEmpty(trueItem)) return;

			if (!item.hasItemMeta()) return;

			ItemMeta meta = item.getItemMeta();

			if (!meta.hasDisplayName()) return;

			ItemMeta trueMeta = trueItem.getItemMeta();

			trueMeta.setDisplayName(meta.getDisplayName());
			trueItem.setItemMeta(trueMeta);
			event.setCurrentItem(trueItem);

			return;
		}

		// Handle shift clicking into slots one and two
		if (!(event.getView().getTopInventory() instanceof AnvilInventory)) return;

		if (!event.getClick().isShiftClick()) return;

		if (ItemUtils.isEmpty(event.getView().getItem(0))) return;

		if (!ItemUtils.isEmpty(event.getView().getItem(1))) return;

		if (!(this.hasFakeEnchantment(event.getView().getItem(0)) || this.hasFakeEnchantment(event.getCurrentItem()))) return;

		event.setResult(Event.Result.DENY);
	}

	// Drags can be the same as placing a single item.  People listening for InventoryClickEvent often think that
	// it's broken because if you move your mouse at all after you click it becomes a drag.
	// We need to handle drags the same way that we do for clicks.
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event)
	{
		for (Entry<Integer, ItemStack> placed : event.getNewItems().entrySet())
		{
			if (placed.getKey() != 1) continue;

			if (!(this.hasFakeEnchantment(event.getView().getItem(0)) || this.hasFakeEnchantment(placed.getValue()))) continue;

			event.setResult(Result.DENY);

			return;
		}
	}

	// When item put into frame
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event)
	{
		if (!(event.getRightClicked() instanceof ItemFrame)) return;

		ItemFrame frame = (ItemFrame) event.getRightClicked();
		Player player = event.getPlayer();

		if (!ItemUtils.isEmpty(frame.getItem()))
			return; // Frame has an item already, no worries

		if (ItemUtils.isEmpty(player.getItemInHand()))
			return; // Player has an empty hand, no worries

		ItemStack former = event.getPlayer().getItemInHand();

		// Player had an item, remove enchants if existing

		if (!ItemUtils.isEmpty(former)) remove:
				{
					if (!former.hasItemMeta()) break remove;

					ItemMeta meta = former.getItemMeta();

					if (!meta.hasEnchants()) break remove;

					Set<LoadableEnchantment> enchs = EnchantmentLoader.getEnchants(EnchantmentType.EFFECT);

					for (Enchantment ench : meta.getEnchants().keySet())
					{
						if (!(ench instanceof LoadableEnchantment)) continue;

						if (!enchs.contains(ench)) continue;

						LoadableEnchantment loadableEnchantment = ((LoadableEnchantment) ench);

						if (loadableEnchantment.getEffect().equals(PotionEffectType.NIGHT_VISION) && player.hasMetadata(Meta.NIGHT_VISION))
							continue;
						player.removePotionEffect(loadableEnchantment.getEffect());
					}

					if (meta.hasEnchant(EnchantmentFlight.get()) && !player.hasMetadata("_ulf_allowfly"))
					{
						player.setAllowFlight(false);
					}

					if (meta.hasEnchant(EnchantmentSpeedyGonzales.get()))
					{
						player.setWalkSpeed(0.25F);
					}
				}
	}

	private boolean hasFakeEnchantment(ItemStack item)
	{
		if (ItemUtils.isEmpty(item)) return false;

		if (!item.hasItemMeta()) return false;

		for (Enchantment enchantment : item.getItemMeta().getEnchants().keySet())
		{
			if (this.isFakeEnchantment(enchantment)) return true;
		}

		return false;
	}

	private boolean isFakeEnchantment(Enchantment enchantment)
	{
		//TODO: Come up with a better method (Ideally something in Lib) for determining if an enchantment is fake
		return enchantment instanceof PrisonEnchantment || enchantment instanceof LoadableEnchantment;
	}

	private void playBreakable(Block breakable, Player player)
	{
		if (LuckyBlocks.isLuckyBlock(breakable) || !BlockUtils.isSmashable(breakable)) return;

		Metadata.applyNull(breakable, "_ulf_fakebreak");

		Events.call(new BlockBreakEvent(breakable, player));

		Metadata.remove(breakable, "_ulf_fakebreak");
	}

}
