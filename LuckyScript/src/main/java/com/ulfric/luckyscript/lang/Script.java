package com.ulfric.luckyscript.lang;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.ArrayUtils;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.RandomUtils;
import com.ulfric.lib.api.persist.TryFiles;
import com.ulfric.lib.api.reflect.Reflect;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;
import com.ulfric.luckyscript.lang.act.Action;
import com.ulfric.luckyscript.lang.act.AfterAct;
import com.ulfric.luckyscript.lang.act.AsyncAct;
import com.ulfric.luckyscript.lang.act.BlockiterAct;
import com.ulfric.luckyscript.lang.act.BreakAct;
import com.ulfric.luckyscript.lang.act.IterAction;
import com.ulfric.luckyscript.lang.act.LookaheadAction;
import com.ulfric.luckyscript.lang.act.RepeatAct;
import com.ulfric.luckyscript.lang.act.RollAct;
import com.ulfric.luckyscript.lang.act.SleepAct;
import com.ulfric.luckyscript.lang.act.SpecialAction;
import com.ulfric.luckyscript.lang.act.SyncAct;
import com.ulfric.luckyscript.lang.enums.LoadPriority;
import com.ulfric.luckyscript.lang.enums.LoadState;
import com.ulfric.luckyscript.lang.except.ScriptExecuteException;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class Script implements Named {


	protected Script(File file)
	{
		this.state = LoadState.UNLOADED;

		this.name = file.getName().replace(".yml", Strings.BLANK);

		List<String> strings = TryFiles.readLines(file);

		this.actions = Lists.newArrayListWithExpectedSize(strings.size());

		this.process = Lists.newArrayList();

		int line = 0;
		for (String string : strings)
		{
			line++;

			string = string.trim();

			if (string.isEmpty() || string.startsWith("#")) continue;

			if (string.startsWith(":"))
			{
				this.state = LoadState.DEPENDENCY;

				this.priority = LoadPriority.valueOf(StringUtils.findOption(string, "priority").toUpperCase());

				continue;
			}

			this.process.add(Tuples.newPair(line, string));
		}

		if (this.state.equals(LoadState.DEPENDENCY)) return;

		this.load();
	}

	private List<Pair<Integer, String>> process;

	protected void load()
	{
		this.state = LoadState.LOADING;
		int linenum = 0;
		try
		{
			Action<?> last = null;
			for (Pair<Integer, String> element : this.process)
			{
				linenum = element.getA();
				String line = element.getB();

				String[] split = line.split("\\s+");

				Assert.isTrue(split.length > 0, "Bad line: " + line);

				String first = split[0];

				Assert.isNotEmpty(first, "Null action in: " + line, "Empty action in: " + line);

				Constructor<? extends Action<?>> constr = Assert.notNull(Actions.getAction(first), "Bad constructor: " + first);
				String pass = split.length == 1 ? null : ArrayUtils.mergeToString(split, 1);
				Action<?> act = Reflect.newInstance(constr, pass);
				Assert.notNull(act, Strings.format("Could not instantiate constructor {0} which takes {1} arguments (first: {2}) for action {3} with arguments {4}", constr.getName(), constr.getParameterCount(), constr.getParameterTypes()[0].getName(), first, pass));

				if (last == null)
				{
					last = act;
				}
				else if (last instanceof LookaheadAction) annot:
				{
					last = act;

					Method method = Reflect.getMethod(act.getClass(), "run", Player.class, LocatableMetadatable.class);

					Assert.notNull(method, "Missing 'run' method!");

					com.ulfric.luckyscript.lang.annotation.Script canRun = method.getAnnotation(com.ulfric.luckyscript.lang.annotation.Script.class);

					if (canRun == null) break annot;

					Assert.isTrue(canRun.canRun(), "Lookahead action trying to run a nonrunnable action!");
				}

				act.setLine(linenum);
				this.actions.add(act);
			}

			this.state = LoadState.LOADED;
		}
		catch (Throwable throwable) { throw new ScriptParseException(Strings.format("Error parsing line {0} in script {1}", linenum, this.name), throwable); }
	}

	private final String name;
	@Override
	public String getName() { return this.name; }

	private LoadPriority priority;
	public LoadPriority getPriority() { return this.priority; }

	private LoadState state;
	public LoadState getState() { return this.state; }

	private List<Action<?>> actions;

	public void run(Player player, LocatableMetadatable object)
	{
		this.execute(player, object, new ScriptIter(this.actions.iterator()));
	}

	private void execute(Player player, LocatableMetadatable object, ScriptIter iterator)
	{
		if (!iterator.hasNext()) return;

		Action<?> next = iterator.next();

		try
		{
			if (next instanceof SpecialAction)
			{
				Action<?> post = next instanceof LookaheadAction ? iterator.next() : null;

				if (post != null)
				{
					if (next instanceof RepeatAct)
					{
						for (int x = 0; x < (Integer) next.getValue(); x++)
						{
							post.run(player, object);
						}
					}

					else if (next instanceof AfterAct)
					{
						Assert.isNotInstanceof(AfterAct.class, post);

						Tasks.runLater(() -> post.run(player, object), (Long) next.getValue());
					}
				}

				else if (next instanceof SleepAct)
				{
					Tasks.runLater(() -> this.execute(player, object, iterator), (Long) next.getValue());

					return;
				}

				else if (next instanceof AsyncAct)
				{
					Tasks.runAsync(() -> this.execute(player, object, iterator));

					return;
				}

				else if (next instanceof SyncAct)
				{
					Tasks.run(() -> this.execute(player, object, iterator));

					return;
				}

				else if (next instanceof RollAct)
				{
					RollAct roll = (RollAct) next;

					if (roll.getValue().matches(roll.getTarget(), RandomUtils.nextIntP1(roll.getRange())))
					{
						iterator.addChain();

						this.execute(player, object, iterator);

						return;
					}

					if (!roll.hasElseif()) return;

					while (iterator.hasNext())
					{
						if (!(iterator.next() instanceof BreakAct)) continue;

						break;
					}

					this.execute(player, object, iterator);

					return;
				}

				else if (next instanceof BreakAct)
				{
					iterator.useChain();
				}

				else if (next instanceof IterAction)
				{
					if (!(next instanceof BlockiterAct)) this.execute(player, object, iterator);

					/*BlockiterAct act = (BlockiterAct) next;

					act.run(player, object);*/

					int layers = 1;
					List<Action<?>> actions = Lists.newArrayList(next);
					while (iterator.hasNext())
					{
						Action<?> iterNext = iterator.next();

						if (iterNext instanceof IterAction)
						{
							if (iterNext instanceof BreakAct)
							{
								layers--;

								if (layers == 0) break;
							}
							else
							{
								layers++;
							}
						}

						actions.add(iterNext);

						continue;
					}

					this.runActionIter(actions, player, object);
				}

				else
				{
					throw new UnsupportedOperationException("Action instanceof SpecialAction without implementation!");
				}

				this.execute(player, object, iterator);

				return;
			}

			next.run(player, object);
		}
		catch (Throwable throwable) { throw new ScriptExecuteException("Error on line " + next.getLine(), throwable); }

		this.execute(player, object, iterator);
	}

	private void runActionIter(List<Action<?>> actions, Player player, LocatableMetadatable object)
	{
		Deque<Pair<List<LocatableMetadatable>, List<Action<?>>>> layered = new ArrayDeque<>();

		for (Action<?> action : actions)
		{
			if (action instanceof IterAction)
			{
				if (action instanceof BreakAct)
				{
					this.runSanatizedIter(layered.pop(), player);

					continue;
				}

				action.run(player, object);

				layered.push(Tuples.newPair(((IterAction) action).getObjects(), Lists.newArrayList()));

				continue;
			}

			layered.getFirst().getB().add(action);
		}

		if (layered.isEmpty()) return;

		for (Pair<List<LocatableMetadatable>, List<Action<?>>> action : layered)
		{
			this.runSanatizedIter(action, player);
		}
	}

	private void runSanatizedIter(Pair<List<LocatableMetadatable>, List<Action<?>>> actions, Player player)
	{
		for (LocatableMetadatable locatmet : actions.getA())
		{
			this.execute(player, locatmet, new ScriptIter(actions.getB().iterator()));
		}
	}


}