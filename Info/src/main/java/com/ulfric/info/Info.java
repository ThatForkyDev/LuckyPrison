package com.ulfric.info;

import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.Plugin;

public class Info extends Plugin {

	@Override
	public void load()
	{
		for (String link : this.getDescription().getCommands().keySet())
		{
			this.addCommand(link, (sender, command, label, args) -> {
				Locale.send(sender, "info." + link);
				return true;
			});
		}
	}

}
