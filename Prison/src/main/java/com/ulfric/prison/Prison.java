package com.ulfric.prison;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;
import com.ulfric.prison.achievement.AchievementModule;
import com.ulfric.prison.commands.CommandAlias;
import com.ulfric.prison.commands.CommandBonus;
import com.ulfric.prison.commands.CommandCraft;
import com.ulfric.prison.commands.CommandExplosions;
import com.ulfric.prison.commands.CommandGiveplotmerges;
import com.ulfric.prison.commands.CommandGiveplots;
import com.ulfric.prison.commands.CommandMine;
import com.ulfric.prison.commands.CommandMinebuddy;
import com.ulfric.prison.commands.CommandNightvision;
import com.ulfric.prison.commands.CommandProfile;
import com.ulfric.prison.commands.CommandRanks;
import com.ulfric.prison.commands.CommandResourcepack;
import com.ulfric.prison.commands.CommandStack;
import com.ulfric.prison.configuration.ConfigurationPrison;
import com.ulfric.prison.configuration.ConfigurationStore;
import com.ulfric.prison.crate.ModuleCrates;
import com.ulfric.prison.enchantments.loader.EnchantLoaderModule;
import com.ulfric.prison.hook.PrisonImpl;
import com.ulfric.prison.listeners.ListenerBlock;
import com.ulfric.prison.listeners.ListenerConnection;
import com.ulfric.prison.listeners.ListenerEnchantments;
import com.ulfric.prison.listeners.ListenerInventory;
import com.ulfric.prison.listeners.ListenerSign;
import com.ulfric.prison.modules.ModuleAntiglitch;
import com.ulfric.prison.modules.ModuleAutomessage;
import com.ulfric.prison.modules.ModuleBackpacks;
import com.ulfric.prison.modules.ModuleCandy;
import com.ulfric.prison.modules.ModuleCarrotblock;
import com.ulfric.prison.modules.ModuleClearlag;
import com.ulfric.prison.modules.ModuleCoupons;
import com.ulfric.prison.modules.ModuleCrafting;
import com.ulfric.prison.modules.ModuleExpsave;
import com.ulfric.prison.modules.ModuleGrenade;
import com.ulfric.prison.modules.ModuleListperks;
import com.ulfric.prison.modules.ModuleLuckyblocks;
import com.ulfric.prison.modules.ModuleMarriage;
import com.ulfric.prison.modules.ModuleNameplate;
import com.ulfric.prison.modules.ModuleNopickup;
import com.ulfric.prison.modules.ModulePextras;
import com.ulfric.prison.modules.ModulePortals;
import com.ulfric.prison.modules.ModuleRankup;
import com.ulfric.prison.modules.ModuleRecords;
import com.ulfric.prison.modules.ModuleRepair;
import com.ulfric.prison.modules.ModuleScrolls;
import com.ulfric.prison.modules.ModuleSell;
import com.ulfric.prison.modules.ModuleSilkspawners;
import com.ulfric.prison.modules.ModuleStaff;
import com.ulfric.prison.modules.ModuleSuperpickaxe;
import com.ulfric.prison.modules.ModuleVoid;
import com.ulfric.prison.scoreboard.ModuleScoreboard;
import com.ulfric.prison.tasks.TaskMinereset;
import com.ulfric.prison.tasks.TaskTimelock;

public class Prison extends Plugin {

	private static Prison i;
	public static Prison get() { return Prison.i; }

	@Override
	public void load()
	{
		Prison.i = this;

		this.registerHook(Hooks.PRISON, PrisonImpl.INSTANCE);

		this.withSubModule(new EnchantLoaderModule());
		this.withSubModule(new ModuleCrafting());
		this.withSubModule(new ModuleAntiglitch());
		this.withSubModule(new ModulePortals());
		this.withSubModule(new ModuleNopickup());
		this.withSubModule(new ModulePextras());
		this.withSubModule(new ModuleClearlag());
		this.withSubModule(ModuleRecords.get());
		this.withSubModule(ModuleSell.get());
		this.withSubModule(new ModuleCandy());
		this.withSubModule(new ModuleGrenade());
		this.withSubModule(new ModuleAutomessage());
		this.withSubModule(new ModuleListperks());
		this.withSubModule(new ModuleCrates());
		this.withSubModule(ModuleRankup.get());
		this.withSubModule(new ModuleBackpacks());
		this.withSubModule(new ModuleSilkspawners());
		this.withSubModule(new ModuleCarrotblock());
		this.withSubModule(new ModuleExpsave());
		this.withSubModule(new ModuleVoid());
		this.withSubModule(ModuleMarriage.get());
		this.withSubModule(AchievementModule.get());
		this.withSubModule(new ModuleRepair());
		this.withSubModule(new ModuleScrolls());
		this.withSubModule(new ModuleSuperpickaxe());
		this.withSubModule(new ModuleCoupons());
		this.withSubModule(new ModuleNameplate());
		this.withSubModule(ModuleScoreboard.INSTANCE);
		this.withSubModule(new ModuleStaff());

		/*MaterialUtils.setMaxStackSizeMutable(true);
		for (Material material : Material.values())
		{
			if (material.getMaxStackSize() < 64) continue;

			MaterialUtils.setMaxStackSize(material, 120);
		}
		MaterialUtils.setMaxStackSizeMutable(false);*/

		this.addListener(new ListenerConnection());
		this.addListener(new ListenerInventory());
		this.addListener(new ListenerBlock());
		this.addListener(new ListenerSign());
		this.addListener(new ListenerEnchantments());
		this.addListener(TaskMinereset.get());

		this.addCommand("minebuddy", new CommandMinebuddy());
		this.addCommand("profile", new CommandProfile());
		this.addCommand("bonus", new CommandBonus());
		this.addCommand("mine", new CommandMine());
		this.addCommand("resourcepack", new CommandResourcepack());
		this.addCommand("ranks", new CommandRanks());
		this.addCommand("alias", new CommandAlias());
		this.addCommand("nightvision", new CommandNightvision());
		this.addCommand("craft", new CommandCraft());
		this.addCommand("stack", new CommandStack());
		this.addCommand("giveplots", new CommandGiveplots());
		this.addCommand("giveplotmerges", new CommandGiveplotmerges());
		this.addCommand("explosions", new CommandExplosions());
	}

	@Override
	public void postEnable()
	{
		ConfigurationPrison.INSTANCE.poke();

		ConfigurationStore.INSTANCE.poke();

		if (TaskMinereset.get().isRunning())
		{
			TaskMinereset.get().annihilate();
		}
		TaskMinereset.get().start();

		if (ModuleLuckyblocks.hooked) return;

		this.withSubModule(new ModuleLuckyblocks());

		ModuleLuckyblocks.hooked = true;
	}

	@Override
	public void enable()
	{
		TaskTimelock.get().start();
	}

	@Override
	public void disable()
	{
		TaskMinereset.get().annihilate();
		TaskTimelock.get().annihilate();

		Prison.i = null;
	}

}