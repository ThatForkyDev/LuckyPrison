package com.ulfric.actions.listeners;

import com.ulfric.lib.api.module.SimpleModule;

public final class LoggerModule extends SimpleModule {

	public LoggerModule()
	{
		super("logger", "Logging management module", "Packet", "1.0.0-REL");

		this.addListener(new ListenerShutdown());
		this.addListener(new ListenerInventory());
		this.addListener(new ListenerInteract());
		this.addListener(new ListenerConnection());
		this.addListener(new ListenerChat());
	}

}