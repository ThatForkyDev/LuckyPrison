package com.ulfric.control.entity;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.control.entity.enums.ViolationType;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;

class Violation {

	protected Violation(Player owner)
	{
		this.owner = owner;

		this.violations = Maps.newEnumMap(ViolationType.class);

		this.tasks = Maps.newEnumMap(ViolationType.class);
	}

	private Player owner;
	public Player getOwner() { return this.owner; }

	public void dump()
	{
		this.total = 0;
		this.tasks.clear();
		this.violations.clear();
	}

	private int task;
	private float total;
	protected float getTotal() { return this.total; }
	private boolean incrementTotal()
	{
		if (this.task != 0)
		{
			Bukkit.getScheduler().cancelTask(this.task);
		}

		this.task = Tasks.runLater(() -> { this.total = 0; this.task = 0; }, Ticks.fromSeconds(75)).getTaskId();

		return (this.total++ > 16);
	}

	public boolean increment(ViolationType type)
	{
		Float olevel = this.violations.get(type);

		if (olevel == null)
		{
			this.violations.put(type, type.getTotalWorth());

			this.tasks.put(type, Tasks.runLater(() -> this.violations.remove(type), type.getSecondsTillReset()).getTaskId());

			return false;
		}

		float level = olevel.floatValue() + type.getTotalWorth();

		this.violations.replace(type, level);

		if (this.tasks.containsKey(type))
		{
			Bukkit.getScheduler().cancelTask(this.tasks.get(type));
		}

		this.tasks.put(type, Tasks.runLater(() -> { this.violations.remove(type); this.tasks.remove(type); }, type.getSecondsTillReset()).getTaskId());

		if (level < type.getMaxLevel()) return false;

		return this.incrementTotal();
	}

	private Map<ViolationType, Integer> tasks;

	private Map<ViolationType, Float> violations;

}