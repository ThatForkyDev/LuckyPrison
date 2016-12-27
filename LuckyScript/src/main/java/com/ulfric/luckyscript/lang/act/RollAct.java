package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.math.strategy.MathStrategy;
import com.ulfric.luckyscript.lang.annotation.Script;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class RollAct extends Action<MathStrategy> implements SpecialAction {

	public RollAct(String context)
	{
		super(context);
	}

	private int target;
	public int getTarget() { return this.target; }

	private int range;
	public int getRange() { return this.range; }

	private boolean elseif;
	public boolean hasElseif() { return this.elseif; }

	@Override
	protected MathStrategy parse(String context)
	{
		context = context.toLowerCase();

		this.range = Numbers.parseInteger(StringUtils.findOption(context, "range"));

		this.target = Numbers.parseInteger(StringUtils.findOption(context, "target"));

		this.elseif = context.contains("--elseif");

		return Assert.notNull(MathStrategy.parse(StringUtils.findOption(context, "strategy")), "Strategy not found!");
	}

	@Override
	@Script(canRun = false)
	public void run(Player player, LocatableMetadatable object)
	{
		throw new UnsupportedOperationException();
	}

}