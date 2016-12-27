package com.intellectualcrafters.plot.object;

import javax.script.ScriptException;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.commands.DebugExec;
import com.intellectualcrafters.plot.commands.MainCommand;

public interface Expression<T> {

	T evaluate(T arg);

	static <U> Expression<U> constant(U value)
	{
		return arg -> value;
	}

	static Expression<Double> linearDouble(Double value)
	{
		return arg -> arg * value;
	}

	static Expression<Double> doubleExpression(String expression)
	{
		try
		{
			return constant(Double.parseDouble(expression));
		}
		catch (NumberFormatException ignore) {}
		if (expression.endsWith("*{arg}"))
		{
			try
			{
				return linearDouble(Double.parseDouble(expression.substring(0, expression.length() - 6)));
			}
			catch (NumberFormatException ignore) {}
		}
		return arg ->
		{
			DebugExec exec = (DebugExec) MainCommand.getInstance().getCommand(DebugExec.class);
			try
			{
				return (Double) exec.getEngine().eval(expression.replace("{arg}", String.valueOf(arg)));
			}
			catch (ScriptException e)
			{
				PS.debug("Invalid Expression: " + expression);
				e.printStackTrace();
			}
			return 0.0d;
		};
	}
}
