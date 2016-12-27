package com.intellectualcrafters.plot.util.helpmenu;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.util.StringMan;
import com.plotsquared.general.commands.Argument;
import com.plotsquared.general.commands.Command;

public class HelpObject {

	private final String _rendered;

	public HelpObject(Command command, String label)
	{
		this._rendered = StringMan.replaceAll(C.HELP_ITEM.s(),
											  "%usage%", command.getUsage().replaceAll("\\{label\\}", label),
											  "[%alias%]", command.getAliases().isEmpty() ? "" : '(' + StringMan.join(command.getAliases(), "|") + ')',
											  "%desc%", command.getDescription(),
											  "%arguments%", this.buildArgumentList(command.getRequiredArguments()),
											  "{label}", label);
	}

	@Override
	public String toString()
	{
		return this._rendered;
	}

	private String buildArgumentList(Argument... arguments)
	{
		if (arguments == null)
		{
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for (Argument<?> argument : arguments)
		{
			builder.append('[').append(argument.getName()).append(" (").append(argument.getExample()).append(")],");
		}
		return arguments.length > 0 ? builder.substring(0, builder.length() - 1) : "";
	}
}
