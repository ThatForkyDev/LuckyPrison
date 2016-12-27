package com.ulfric.lib.api.module;

import com.google.common.collect.Maps;
import com.ulfric.lib.Lib;
import com.ulfric.lib.LibConfiguration;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

public final class Modules {


	private static final Map<String, IModule> MODULES = Maps.newTreeMap();

	private Modules()
	{
	}

	public static IModule getModule(String name)
	{
		name = name.toLowerCase();

		for (Map.Entry<String, IModule> entry : MODULES.entrySet())
		{
			if (!entry.getKey().toLowerCase().equals(name)) continue;

			return entry.getValue();
		}

		return null;
	}

	public static Set<Map.Entry<String, IModule>> getModules()
	{
		return MODULES.entrySet();
	}

	public static void register(IModule module)
	{
		MODULES.put(module.getName(), module);
	}

	public static boolean canEnable(IModule module)
	{
		StringBuilder path = new StringBuilder("modules.");

		Stack<String> stack = new Stack<>();

		stack.push(".");

		stack.push(module.getName());

		IModule parent = module.getParentModule();
		while (parent != null)
		{
			if (!canEnable(parent)) return false;

			stack.push(".");

			stack.push(parent.getName());

			parent = parent.getParentModule();
		}

		while (!stack.isEmpty())
		{
			path.append(stack.pop());
		}

		path.append("enabled");

		String pathStr = path.toString().toLowerCase();

		if (!LibConfiguration.INSTANCE.getConf().contains(pathStr))
		{
			module.log("Setting up module {0}", module.getName());

			LibConfiguration.INSTANCE.getConf().set(pathStr, module.getModuleDefault());

			Lib.get().saveConfig();

			return module.getModuleDefault();
		}

		return LibConfiguration.INSTANCE.getConf().getBoolean(pathStr);
	}


}
