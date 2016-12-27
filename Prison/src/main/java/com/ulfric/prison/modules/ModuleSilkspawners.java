package com.ulfric.prison.modules;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.lib.api.block.BlockUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;

public class ModuleSilkspawners extends SimpleModule {

	public ModuleSilkspawners()
	{
		super("silkspawners", "Silktouch spawners module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@SuppressWarnings("deprecation")
			@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
			public void onBreak(BlockBreakEvent event)
			{
				Block block = event.getBlock();

				if (!block.getType().equals(Material.MOB_SPAWNER)) return;

				Player player = event.getPlayer();

				ItemStack item = player.getItemInHand();

				if (EnchantUtils.getLevel(item, Enchantment.SILK_TOUCH) == 0) return;

				event.setCancelled(true);

				if (!player.hasPermission("prison.silktouch"))
				{
					Locale.sendError(player, "prison.silktouch_premium");

					return;
				}

				// TODO: Was this useful?
				//event.setNotify(false);

				CreatureSpawner spawner = (CreatureSpawner) block.getState();

				EntityType type = spawner.getSpawnedType();

				ItemBuilder builder = new ItemBuilder();
				builder.withType(Material.MOB_SPAWNER);
				builder.withDurability(type.getTypeId());
				builder.withLore(ChatColor.YELLOW + WordUtils.capitalizeFully(type.name().toLowerCase().replace('_', ' ')));

				block.setType(Material.AIR);

				block.getWorld().dropItem(block.getLocation(), builder.build());
			}

			@EventHandler(ignoreCancelled = true)
			public void onSpawn(CreatureSpawnEvent event)
			{
				if (!event.getSpawnReason().equals(SpawnReason.SPAWNER)) return;

				if (event.getLocation().getWorld().getEnvironment().equals(Environment.NETHER)) return;

				event.setCancelled(true);
			}

			@EventHandler(ignoreCancelled = true)
			public void onSpawnerPlace(BlockPlaceEvent event)
			{
				Block block = event.getBlock();

				if (!block.getType().equals(Material.MOB_SPAWNER)) return;

				ItemStack item = event.getItemInHand();

				ItemMeta meta;
				if (!item.hasItemMeta() || !(meta = item.getItemMeta()).hasLore()) return;

				CreatureSpawner spawner = (CreatureSpawner) block.getState();

				EntityType type = EntityUtils.parse(ChatColor.stripColor(meta.getLore().get(0)).replace(' ', '_'));

				if (type == null)
				{
					event.getPlayer().sendMessage("Bad type: " + meta.getLore().get(0));

					return;
				}

				spawner.setSpawnedType(type);
			}

			@EventHandler(ignoreCancelled = true)
			public void onSpawnerChange(PlayerInteractEvent event)
			{
				Block block = event.getClickedBlock();

				if (BlockUtils.isEmpty(block)) return;

				if (!block.getType().equals(Material.MOB_SPAWNER)) return;

				if (!ItemUtils.is(event.getPlayer().getItemInHand(), Material.MONSTER_EGG)) return;

				event.setCancelled(true);
			}
		});

		this.addCommand("spawner", new CommandSpawner());
	}

	private class CommandSpawner extends SimpleCommand
	{
		private CommandSpawner()
		{
			this.withEnforcePlayer();

			this.withArgument("type", ArgStrategy.ENTITY);
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			if (!this.hasObject("type"))
			{
				Locale.send(player, "prison.spawner_available_mobs",
						StringUtils.mergeNicely(Arrays.stream(EntityType.values())
							  .filter(this::isValidType)
							  .map(this::typeToString)
							  .filter(name -> player.hasPermission("prison.spawner." + name.replace(" ", Strings.BLANK)))
							  .map(WordUtils::capitalizeFully)
							  .collect(Collectors.toList())));

				return;
			}

			EntityType type = (EntityType) this.getObject("type");

			if (!this.isValidType(type))
			{
				Locale.sendError(player, "prison.spawner_entity_invalid", this.typeToString(type));

				return;
			}

			String name = type.name().toLowerCase().replace("_", Strings.BLANK);
			if (!player.hasPermission("prison.spawner." + name))
			{
				Locale.sendError(player, "prison.spawner_no_permission", name);

				return;
			}

			Block block = PlayerUtils.getTargetBlock(player, 6);

			if (BlockUtils.isEmpty(block) || !block.getType().equals(Material.MOB_SPAWNER))
			{
				Locale.sendError(player, "prison.spawner_not_in_range");

				return;
			}

			((CreatureSpawner) block.getState()).setSpawnedType(type);
		}

		private boolean isValidType(EntityType type)
		{
			return type.isAlive() && type.isSpawnable() && !type.equals(EntityType.PLAYER);
		}

		private String typeToString(EntityType type)
		{
			return type.name().toLowerCase().replace('_', ' ');
		}
	}

}
