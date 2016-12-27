package com.ulfric.lib.api.module;

public class SimpleModule extends Module {

	public SimpleModule(String name, String description, String authors, String version)
	{
		super(name, description, authors, version);
	}

	@Override
	public final void onEnable()
	{
		this.log("Enabling {0}", this.getName());

		this.postEnable();
	}

	@Override
	public final void onDisable()
	{
		this.log("Disabling {0}", this.getName());

		this.postDisable();
	}

	public void postEnable()
	{
	}

	public void postDisable()
	{
	}

}
