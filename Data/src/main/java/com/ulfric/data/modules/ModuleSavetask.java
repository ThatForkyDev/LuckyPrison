package com.ulfric.data.modules;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.ulfric.data.coll.DataColl;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.ModuleTask;
import com.ulfric.lib.api.module.ModuleTask.StartType;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.task.ATask;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;

public class ModuleSavetask extends SimpleModule {

	public ModuleSavetask()
	{
		super("save-task", "Save data task", "Packet", "1.0.0-REL");

		this.withConf();
	}

	@Override
	public void onFirstEnable()
	{
		this.addCommand("savedata", new CommandSavedata());

		this.addTask(new ModuleTask(new ATask()
		{
			private final Lock lock = new ReentrantLock();

			@Override
			public void start()
			{
				super.start();

				this.setTaskId(Tasks.runRepeatingAsync(this, ModuleSavetask.this.getConf().getValue("delay-in-ticks", Ticks.fromMinutes(30))).getTaskId());
			}
			@Override
			public void run()
			{
				if (!this.lock.tryLock()) return;

				Locale.sendMass("data.saving");

				DataColl.impl().annihilate();

				Locale.sendMass("data.saved");

				this.lock.unlock();
			}
		}, StartType.AUTOMATIC));
	}

	private class CommandSavedata extends SimpleCommand
	{
		@Override
		public void run()
		{
			Locale.send(this.getSender(), "data.saving");

			DataColl.impl().annihilate();

			Locale.sendSuccess(this.getSender(), "data.saved");
		}
	}

}