package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.module.IModule;
import com.ulfric.lib.api.module.Modules;

final class ModuleArg implements ArgStrategy<IModule> {

	@Override
	public IModule match(String string)
	{
		return Modules.getModule(string);
	}

}
