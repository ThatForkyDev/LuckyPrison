package com.ulfric.prison.modules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.module.ModuleTask;
import com.ulfric.lib.api.module.ModuleTask.StartType;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;

public class ModuleAutomessage extends SimpleModule {

	public ModuleAutomessage()
	{
		super("automessage", "Automated help and tips module", "Packet", "1.0.0-REL");

		this.withConf();

		this.addTask(new ModuleTask(new TaskAutomessage(), StartType.AUTOMATIC));
	}

	private List<String> automessages;
	private int delay;

	@Override
	public void postEnable()
	{
		this.automessages = this.getConf()
							.getValueAsStringList("messages")
							.stream().map(str -> Chat.color(str).replace(Strings.FAKE_LINEBREAK, "\n").replace("<P>", "locale.automessage.prefix"))
							.collect(Collectors.toList());

		this.delay = this.getConf().getConf().getInt("delay");
	}

	private class TaskAutomessage extends ATask
	{
		@Override
		public void start()
		{
			super.start();

			this.setTaskId(Tasks.runRepeatingAsync(this, ModuleAutomessage.this.delay).getTaskId());
		}

		@Override
		public void run()
		{
			String message = RandomUtils.randomValueFromList(ModuleAutomessage.this.automessages);

			Matcher match;

			if (!message.contains("locale.") || !(match = Locale.pattern().matcher(message)).find())
			{
				Locale.sendMass(RandomUtils.randomValueFromList(ModuleAutomessage.this.automessages));

				return;
			}

			List<String> list = Lists.newArrayListWithExpectedSize(match.groupCount());

			do
			{
				list.add(match.group());
			}
			while (match.find());

			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (Hooks.DATA.getPlayerDataAsBoolean(player.getUniqueId(), "chat.tips")) continue;

				String clone = message;

				for (String replace : list)
				{
					clone = clone.replace(replace, Locale.getMessage(player, replace.substring(7)));
				}

				player.sendMessage(clone.replace(Strings.PLAYER, player.getName()));
			}
		}
	}

}