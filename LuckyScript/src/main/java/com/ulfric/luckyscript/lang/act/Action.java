package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.uspigot.metadata.LocatableMetadatable;

public abstract class Action<T> {

	protected Action(String context)
	{
		this.value = this.parse(context);
	}

	private int line;
	public int getLine() { return this.line; }
	public void setLine(int line) { this.line = line; }

	private final T value;
	public T getValue() { return this.value; }

	protected abstract T parse(String context);

	public abstract void run(Player player, LocatableMetadatable object);

}