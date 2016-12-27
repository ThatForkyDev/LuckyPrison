package com.ulfric.lib.api.math.strategy;

import com.ulfric.lib.api.module.SimpleModule;

public final class MathSolverModule extends SimpleModule {

	public MathSolverModule()
	{
		super("mathsolver", "Math problem solving module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		MathStrategy.IMath.impl = new MathStrategy.IMath.IMathStrategy() {
			@Override
			public MathStrategy parse(String string)
			{
				string = string.toLowerCase();

				if (string.contains("less"))
				{
					return (string.contains("equal") ? MathStrategy.LESS_THAN_OR_EQUAL : MathStrategy.LESS_THAN);
				}
				else if (string.contains("greater"))
				{
					return (string.contains("equal") ? MathStrategy.GREATER_THAN_OR_EQUAL : MathStrategy.GREATER_THAN);
				}

				return string.contains("equal") ? MathStrategy.EQUAL : null;
			}
		};
	}

	@Override
	public void postDisable()
	{
		MathStrategy.IMath.impl = MathStrategy.IMath.IMathStrategy.EMPTY;
	}

}
