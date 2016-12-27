package com.ulfric.control;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.commands.CommandAa;
import com.ulfric.control.commands.CommandAbuse;
import com.ulfric.control.commands.CommandAlert;
import com.ulfric.control.commands.CommandAtall;
import com.ulfric.control.commands.CommandAudit;
import com.ulfric.control.commands.CommandBan;
import com.ulfric.control.commands.CommandBanip;
import com.ulfric.control.commands.CommandBurn;
import com.ulfric.control.commands.CommandClearchat;
import com.ulfric.control.commands.CommandClearwarnings;
import com.ulfric.control.commands.CommandCloneinv;
import com.ulfric.control.commands.CommandCmute;
import com.ulfric.control.commands.CommandCmuteip;
import com.ulfric.control.commands.CommandDate;
import com.ulfric.control.commands.CommandDelpet;
import com.ulfric.control.commands.CommandDisconnect;
import com.ulfric.control.commands.CommandExpire;
import com.ulfric.control.commands.CommandHelpop;
import com.ulfric.control.commands.CommandHistory;
import com.ulfric.control.commands.CommandInspect;
import com.ulfric.control.commands.CommandIpof;
import com.ulfric.control.commands.CommandKick;
import com.ulfric.control.commands.CommandKickall;
import com.ulfric.control.commands.CommandKickip;
import com.ulfric.control.commands.CommandKnownips;
import com.ulfric.control.commands.CommandLockdown;
import com.ulfric.control.commands.CommandLogpos;
import com.ulfric.control.commands.CommandMute;
import com.ulfric.control.commands.CommandMuteip;
import com.ulfric.control.commands.CommandNameof;
import com.ulfric.control.commands.CommandOpenender;
import com.ulfric.control.commands.CommandOpeninv;
import com.ulfric.control.commands.CommandOslay;
import com.ulfric.control.commands.CommandPingavg;
import com.ulfric.control.commands.CommandRecent;
import com.ulfric.control.commands.CommandRespawn;
import com.ulfric.control.commands.CommandSearchinv;
import com.ulfric.control.commands.CommandSeen;
import com.ulfric.control.commands.CommandSeenip;
import com.ulfric.control.commands.CommandShowops;
import com.ulfric.control.commands.CommandSpam;
import com.ulfric.control.commands.CommandSpamfilter;
import com.ulfric.control.commands.CommandSpectate;
import com.ulfric.control.commands.CommandSudo;
import com.ulfric.control.commands.CommandUnban;
import com.ulfric.control.commands.CommandUnbanip;
import com.ulfric.control.commands.CommandUncmute;
import com.ulfric.control.commands.CommandUncmuteip;
import com.ulfric.control.commands.CommandUnmute;
import com.ulfric.control.commands.CommandUnmuteip;
import com.ulfric.control.commands.CommandUptime;
import com.ulfric.control.commands.CommandUuidof;
import com.ulfric.control.commands.CommandViolation;
import com.ulfric.control.commands.CommandWarn;
import com.ulfric.control.commands.CommandWarnings;
import com.ulfric.control.commands.CommandWatcher;
import com.ulfric.control.hook.ControlImpl;
import com.ulfric.control.listeners.ListenerChat;
import com.ulfric.control.listeners.ListenerConnection;
import com.ulfric.control.listeners.ListenerInteract;
import com.ulfric.control.listeners.ListenerMovement;
import com.ulfric.control.modules.ModuleDelentity;
import com.ulfric.control.modules.ModuleIPLimit;
import com.ulfric.control.modules.ModuleSnoop;
import com.ulfric.control.tasks.TaskWatcher;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.module.Plugin;

public class Control extends Plugin {

	private static Control i;
	public static Control get() { return Control.i; }

	@Override
	public void load()
	{
		Control.i = this;

		this.withSubModule(new ModuleSnoop());
		this.withSubModule(new ModuleDelentity());
		this.withSubModule(new ModuleIPLimit());
	}

	@Override
	public void preEnable()
	{
		PunishmentCache.poke();
	}

	@Override
	public void enable()
	{
		this.addListener(new ListenerConnection());
		this.addListener(new ListenerChat());
		this.addListener(new ListenerMovement());
		this.addListener(new ListenerInteract());

		this.addCommand("alert", new CommandAlert());
		this.addCommand("clearchat", new CommandClearchat());
		this.addCommand("uuidof", new CommandUuidof());
		this.addCommand("nameof", new CommandNameof());
		this.addCommand("ipof", new CommandIpof());
		this.addCommand("kick", new CommandKick());
		this.addCommand("kickip", new CommandKickip());
		this.addCommand("kickall", new CommandKickall());
		this.addCommand("ban", new CommandBan());
		this.addCommand("banip", new CommandBanip());
		this.addCommand("mute", new CommandMute());
		this.addCommand("muteip", new CommandMuteip());
		this.addCommand("cmute", new CommandCmute());
		this.addCommand("cmuteip", new CommandCmuteip());
		this.addCommand("unban", new CommandUnban());
		this.addCommand("unbanip", new CommandUnbanip());
		this.addCommand("unmute", new CommandUnmute());
		this.addCommand("unmuteip", new CommandUnmuteip());
		this.addCommand("uncmute", new CommandUncmute());
		this.addCommand("uncmuteip", new CommandUncmuteip());
		this.addCommand("spam", new CommandSpam());
		this.addCommand("sudo", new CommandSudo());
		this.addCommand("watcher", new CommandWatcher());
		this.addCommand("lockdown", new CommandLockdown());
		this.addCommand("spamfilter", new CommandSpamfilter());
		this.addCommand("expire", new CommandExpire());
		this.addCommand("seen", new CommandSeen());
		this.addCommand("seenip", new CommandSeenip());
		this.addCommand("knownips", new CommandKnownips());
		this.addCommand("recent", new CommandRecent());
		this.addCommand("inspect", new CommandInspect());
		this.addCommand("audit", new CommandAudit());
		this.addCommand("disconnect", new CommandDisconnect());
		this.addCommand("oslay", new CommandOslay());
		this.addCommand("aa", new CommandAa());
		this.addCommand("date", new CommandDate());
		this.addCommand("uptime", new CommandUptime());
		this.addCommand("violation", new CommandViolation());
		this.addCommand("atall", new CommandAtall());
		this.addCommand("respawn", new CommandRespawn());
		this.addCommand("burn", new CommandBurn());
		this.addCommand("cloneinv", new CommandCloneinv());
		this.addCommand("history", new CommandHistory());
		this.addCommand("abuse", new CommandAbuse());
		this.addCommand("delpet", new CommandDelpet());
		this.addCommand("logpos", new CommandLogpos());
		this.addCommand("pingavg", new CommandPingavg());
		this.addCommand("warn", new CommandWarn());
		this.addCommand("warnings", new CommandWarnings());
		this.addCommand("clearwarnings", new CommandClearwarnings());
		this.addCommand("showops", new CommandShowops());
		this.addCommand("spectate", new CommandSpectate());
		this.addCommand("helpop", new CommandHelpop());
		this.addCommand("openinv", new CommandOpeninv());
		this.addCommand("openender", new CommandOpenender());
		this.addCommand("searchinv", new CommandSearchinv());

		this.registerHook(Hooks.CONTROL, ControlImpl.INSTANCE);
	}

	@Override
	public void disable()
	{
		for (PunishmentCache cache : PunishmentCache.CACHES.values()) cache.annihilate();

		TaskWatcher.cancelAll();

		Control.i = null;
	}

}