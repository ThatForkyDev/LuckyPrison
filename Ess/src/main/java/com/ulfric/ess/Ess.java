package com.ulfric.ess;

import com.ulfric.ess.commands.CommandBroadcast;
import com.ulfric.ess.commands.CommandButcher;
import com.ulfric.ess.commands.CommandBuycast;
import com.ulfric.ess.commands.CommandClearinventory;
import com.ulfric.ess.commands.CommandDelhome;
import com.ulfric.ess.commands.CommandDelwarp;
import com.ulfric.ess.commands.CommandEnchant;
import com.ulfric.ess.commands.CommandExactgive;
import com.ulfric.ess.commands.CommandFeed;
import com.ulfric.ess.commands.CommandFireball;
import com.ulfric.ess.commands.CommandFix;
import com.ulfric.ess.commands.CommandFly;
import com.ulfric.ess.commands.CommandForcegc;
import com.ulfric.ess.commands.CommandGamemode;
import com.ulfric.ess.commands.CommandGive;
import com.ulfric.ess.commands.CommandGod;
import com.ulfric.ess.commands.CommandHologram;
import com.ulfric.ess.commands.CommandKit;
import com.ulfric.ess.commands.CommandLagwatch;
import com.ulfric.ess.commands.CommandOnline;
import com.ulfric.ess.commands.CommandPermissiondebug;
import com.ulfric.ess.commands.CommandPing;
import com.ulfric.ess.commands.CommandPlayerreset;
import com.ulfric.ess.commands.CommandPlayertime;
import com.ulfric.ess.commands.CommandPlayerweather;
import com.ulfric.ess.commands.CommandReboot;
import com.ulfric.ess.commands.CommandSay;
import com.ulfric.ess.commands.CommandServerStats;
import com.ulfric.ess.commands.CommandSetwarp;
import com.ulfric.ess.commands.CommandShock;
import com.ulfric.ess.commands.CommandSign;
import com.ulfric.ess.commands.CommandSigncmd;
import com.ulfric.ess.commands.CommandSlay;
import com.ulfric.ess.commands.CommandSpawnmob;
import com.ulfric.ess.commands.CommandSpeed;
import com.ulfric.ess.commands.CommandTeleport;
import com.ulfric.ess.commands.CommandTeleporthandshake;
import com.ulfric.ess.commands.CommandTeleporthere;
import com.ulfric.ess.commands.CommandTeleportpos;
import com.ulfric.ess.commands.CommandTime;
import com.ulfric.ess.commands.CommandTop;
import com.ulfric.ess.commands.CommandVanish;
import com.ulfric.ess.commands.CommandWarp;
import com.ulfric.ess.commands.CommandWeather;
import com.ulfric.ess.commands.CommandWorld;
import com.ulfric.ess.configuration.ConfigurationEss;
import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.hook.EssImpl;
import com.ulfric.ess.listeners.ListenerConnection;
import com.ulfric.ess.listeners.ListenerInteract;
import com.ulfric.ess.listeners.ListenerSign;
import com.ulfric.ess.modules.ModuleAliases;
import com.ulfric.ess.modules.ModuleEnderpearlcooldown;
import com.ulfric.ess.modules.ModuleHomes;
import com.ulfric.ess.modules.ModuleKits;
import com.ulfric.ess.modules.ModuleMobdisabler;
import com.ulfric.ess.modules.ModuleMotd;
import com.ulfric.ess.modules.ModuleSpawnpoint;
import com.ulfric.ess.modules.ModuleTabmenu;
import com.ulfric.ess.modules.ModuleUnlimited;
import com.ulfric.ess.tasks.TaskReboot;
import com.ulfric.ess.tasks.TaskUpdatestatus;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;

public class Ess extends Plugin {

	private static Ess i;
	public static Ess get() { return Ess.i; }

	@Override
	public void load()
	{
		Ess.i = this;

		this.withSubModule(ModuleMotd.get());
		this.withSubModule(ModuleSpawnpoint.get());
		this.withSubModule(ModuleKits.get());
		this.withSubModule(new ModuleHomes());
		this.withSubModule(new ModuleMobdisabler());
		this.withSubModule(new ModuleUnlimited());
		this.withSubModule(new ModuleKits());
		this.withSubModule(new ModuleTabmenu());
		this.withSubModule(new ModuleEnderpearlcooldown());
		this.withSubModule(new ModuleAliases());

		this.registerHook(Hooks.ESS, EssImpl.INSTANCE);
	}

	@Override
	public void enable()
	{
		ConfigurationEss.get(); // Load the config

		ConfigurationStore.get(); // load storage

		TaskUpdatestatus.get().start();

		TaskReboot.get().start();

		// TEMPORARY
		this.addCommand("gamemode", new CommandGamemode());
		this.addCommand("time", new CommandTime());
		this.addCommand("day", new CommandTime.CommandDay());
		this.addCommand("night", new CommandTime.CommandNight());
		this.addCommand("ptime", new CommandPlayertime());
		this.addCommand("pday", new CommandPlayertime.CommandDay());
		this.addCommand("pnight", new CommandPlayertime.CommandNight());
		this.addCommand("weather", new CommandWeather());
		this.addCommand("sun", new CommandWeather.CommandSun());
		this.addCommand("rain", new CommandWeather.CommandRain());
		this.addCommand("pweather", new CommandPlayerweather());
		this.addCommand("psun", new CommandPlayerweather.CommandSun());
		this.addCommand("prain", new CommandPlayerweather.CommandRain());
		this.addCommand("preset", new CommandPlayerreset());
		this.addCommand("shock", new CommandShock());
		this.addCommand("teleport", new CommandTeleport());
		this.addCommand("teleporthere", new CommandTeleporthere());
		this.addCommand("teleportpos", new CommandTeleportpos());
		this.addCommand("teleportask", new CommandTeleporthandshake.Ask());
		this.addCommand("teleportaccept", new CommandTeleporthandshake.Accept());
		this.addCommand("teleportqueue", new CommandTeleporthandshake.Que());
		this.addCommand("spawnmob", new CommandSpawnmob());
		this.addCommand("slay", new CommandSlay());
		this.addCommand("feed", new CommandFeed());
		this.addCommand("butcher", new CommandButcher());
		this.addCommand("clearinventory", new CommandClearinventory());
		this.addCommand("ping", new CommandPing());
		this.addCommand("say", new CommandSay());
		this.addCommand("buycast", new CommandBuycast());
		this.addCommand("hologram", new CommandHologram());
		this.addCommand("give", new CommandGive());
		this.addCommand("speed", new CommandSpeed());
		this.addCommand("world", new CommandWorld());
		this.addCommand("kit", new CommandKit());
		this.addCommand("warp", new CommandWarp());
		this.addCommand("setwarp", new CommandSetwarp());
		this.addCommand("god", new CommandGod());
		this.addCommand("vanish", new CommandVanish());
		this.addCommand("top", new CommandTop());
		this.addCommand("sign", new CommandSign());
		this.addCommand("online", new CommandOnline());
		this.addCommand("fly", new CommandFly());
		this.addCommand("signcmd", new CommandSigncmd());
		this.addCommand("reboot", new CommandReboot());
		this.addCommand("exactgive", new CommandExactgive());
		this.addCommand("serverstats", new CommandServerStats());
		this.addCommand("lagwatch", new CommandLagwatch());
		this.addCommand("forcegc", new CommandForcegc());
		this.addCommand("permissiondebug", new CommandPermissiondebug());
		this.addCommand("broadcast", new CommandBroadcast());
		this.addCommand("enchant", new CommandEnchant());
		this.addCommand("delhome", new CommandDelhome());
		this.addCommand("delwarp", new CommandDelwarp());
		this.addCommand("fix", new CommandFix());
		this.addCommand("fireball", new CommandFireball());
		//this.addCommand("enderchest", new CommandEnderchest());

		this.addListener(new ListenerInteract());
		this.addListener(new ListenerSign());
		this.addListener(new ListenerConnection());
		// TEMPORARY
	}

	@Override
	public void disable()
	{
		if (TaskUpdatestatus.get().isRunning())
		{
			TaskUpdatestatus.get().annihilate();
		}

		ConfigurationEss.get().annihilate();

		ConfigurationStore.get().annihilate();

		Ess.i = null;
	}

}
