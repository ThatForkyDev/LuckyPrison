package com.ulfric.luckyscript.lang;

import java.io.File;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.luckyscript.LuckyScript;
import com.ulfric.luckyscript.command.CommandScript;
import com.ulfric.luckyscript.lang.Scripts.IScripts;
import com.ulfric.luckyscript.lang.enums.LoadPriority;
import com.ulfric.luckyscript.lang.enums.LoadState;

public class ScriptsModule extends SimpleModule {

	public ScriptsModule()
	{
		super("scripts", "Script loading and execution module", "Packet", "1.0.0-REL");

		this.addCommand("script", new CommandScript());
	}

	@Override
	public void postEnable()
	{
		Scripts.impl = new IScripts()
		{
			private Map<String, Script> scripts = Maps.newHashMap();
			private Map<LoadPriority, Set<Script>> priority;

			{
				this.init();
			}

			private final void init()
			{
				File folder = LuckyScript.get().getDataFolder();

				if (folder.mkdirs()) return;

				this.findFile(folder);

				this.priority = Maps.newEnumMap(LoadPriority.class);

				for (Script script : this.scripts.values())
				{
					if (!script.getState().equals(LoadState.DEPENDENCY)) continue;

					LoadPriority priority = script.getPriority();

					Set<Script> scripts = this.priority.get(priority);

					if (scripts != null)
					{
						scripts.add(script);

						continue;
					}

					scripts = Sets.newHashSet(script);

					this.priority.put(priority, scripts);
				}

				this.loadScript(LoadPriority.INSTANT);
			}

			private void loadScript(LoadPriority priority)
			{
				if (priority == null) return;

				Set<Script> scripts = this.priority.get(priority);

				if (scripts == null || scripts.isEmpty())
				{
					this.loadScript(LoadPriority.next(priority.getValue()));

					return;
				}

				for (Script script : scripts)
				{
					script.load();
				}

				this.loadScript(LoadPriority.next(priority.getValue()));
			}

			private void findFile(File file)
			{
				if (file.isDirectory())
				{
					for (File sub : file.listFiles())
					{
						this.findFile(sub);
					}

					return;
				}

				String name = file.getName();

				if (!name.endsWith(".lky") || name.startsWith("--")) return;

				this.scripts.put(name.substring(0, name.length()-4), new Script(file));
			}

			@Override
			public Script getScript(String name)
			{
				if (this.scripts == null) return null;

				return this.scripts.get(name);
			}
		};
	}

	@Override
	public void postDisable()
	{
		Scripts.impl = IScripts.EMPTY;
	}

}