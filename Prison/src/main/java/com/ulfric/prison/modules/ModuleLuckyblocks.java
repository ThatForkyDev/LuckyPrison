package com.ulfric.prison.modules;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.ScriptHook.Script;
import com.ulfric.lib.api.inventory.EnchantUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.WeightedWrapper;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;
import com.ulfric.prison.entity.LuckyBlocks;
import com.ulfric.prison.lang.Permissions;

public class ModuleLuckyblocks extends SimpleModule {

	public static volatile boolean hooked;

	public ModuleLuckyblocks()
	{
		super("luckyblocks", "LuckyBlocks feature module", "Packet", "1.0.0-REL");

		this.withConf();

		this.addListener(new Listener()
		{
			@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
			public void onBreak(BlockBreakEvent event)
			{
				Player player = event.getPlayer();

				if (!player.getGameMode().equals(GameMode.SURVIVAL)) return;

				if (EnchantUtils.getLevel(player.getItemInHand(), Enchantment.SILK_TOUCH) > 0) return;

				Block block = event.getBlock();

				if (!LuckyBlocks.isLuckyBlock(block)) return;

				Pair<Integer, Set<WeightedWrapper<Script>>> pair;
				if (LuckyBlocks.isSuperLucky(block))
				{
					if (!player.hasPermission(Permissions.SUPER_BLOCK))
					{
						Locale.sendTimelock(player, Locale.asError(player, "prison.premium_block"), Milliseconds.fromSeconds(2.5));

						event.setCancelled(true);

						return;
					}

					pair = ModuleLuckyblocks.this.drops.get(LuckyBlockScriptType.SUPER);
				}
				else
				{
					pair = ModuleLuckyblocks.this.drops.get(LuckyBlockScriptType.NORMAL);
				}

				event.setCancelled(true);

				if (pair == null) return;

				WeightedWrapper<Script> scriptWrapper = RandomUtils.getWeighted(pair.getB(), pair.getA());

				if (scriptWrapper == null) return;

				Script script = scriptWrapper.getValue();

				if (script == null) return;

				// TODO: Was this useful?
				//event.setNotify(false);

				block.setType(Material.AIR, false);

				script.run(player, block);
			}
		});
	}

	Map<LuckyBlockScriptType, Pair<Integer, Set<WeightedWrapper<Script>>>> drops;

	@Override
	public void postEnable()
	{
		this.drops = Maps.newEnumMap(LuckyBlockScriptType.class);

		Set<WeightedWrapper<Script>> luckyBlockDrops = Sets.newHashSet();
		int luckyWeight = 0;

		int superWeight = 0;
		Set<WeightedWrapper<Script>> superLuckyDrops = Sets.newHashSet();

		for (String string : this.getConf().getValueAsStringList("luckyblocks"))
		{
			Integer iweight = Numbers.parseInteger(StringUtils.findOption(string, "weight"));

			if (iweight == null)
			{
				Bukkit.getLogger().warning("Bad string (weight) in LuckyBlocks config: " + string);

				continue;
			}

			int weight = iweight.intValue();

			LuckyBlockScriptType type = LuckyBlockScriptType.valueOf(Optional.ofNullable(StringUtils.findOption(string, "type")).orElse("normal").toUpperCase());

			boolean normalOnly = type.equals(LuckyBlockScriptType.NORMALONLY);

			Script script = Hooks.SCRIPT.getScript(StringUtils.findOption(string, "id"));
			if (script == null)
			{
				Bukkit.getLogger().info("NULL SCRIPT: " + StringUtils.findOption(string, "id"));
			}
			WeightedWrapper<Script> wrapper = new WeightedWrapper<>(weight, Hooks.SCRIPT.getScript(StringUtils.findOption(string, "id")));

			if (type.equals(LuckyBlockScriptType.NORMAL) || normalOnly)
			{
				luckyBlockDrops.add(wrapper);
				luckyWeight += weight;

				if (normalOnly) continue;
			}

			superLuckyDrops.add(wrapper);
			superWeight += weight;
		}

		this.drops.put(LuckyBlockScriptType.NORMAL, Tuples.newPair(luckyWeight, luckyBlockDrops));
		this.drops.put(LuckyBlockScriptType.SUPER, Tuples.newPair(superWeight, superLuckyDrops));
	}

	public enum LuckyBlockScriptType
	{
		NORMAL,
		NORMALONLY,
		SUPER;
	}

}
