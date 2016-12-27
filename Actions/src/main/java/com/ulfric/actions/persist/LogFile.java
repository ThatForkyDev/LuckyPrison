package com.ulfric.actions.persist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Supplier;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.ulfric.actions.Actions;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.persist.TryFiles;
import com.ulfric.lib.api.time.TimeUtils;

public final class LogFile {

	private static final File FOLDER = new File(Actions.get().getDataFolder(), "logs");
	static
	{
		LogFile.FOLDER.mkdirs();
	}

	private static LogFile create(Player target)
	{
		File file = new File(LogFile.FOLDER, target.getUniqueId() + ".log");

		LogFile log = new LogFile(file);

		log.write("- CREATING NEW LOG FILE -");

		Metadata.apply(target, "actions.log", log);

		return log;
	}

	public static boolean shouldLog(Player player)
	{
		return GameMode.CREATIVE.equals(player.getGameMode()) || player.hasPermission("actions.log");
	}

	private LogFile(File file)
	{
		this.file = file;

		try
		{
			this.writer = new BufferedWriter(new FileWriter(file, true));
		}
		catch (IOException exception) { }

		Assert.notNull(this.writer);
	}

	private final File file;
	public File getFile() { return this.file; }

	private Writer writer;

	public static void exit(Player player, String line, Object... objects)
	{
		LogFile.log(player, Strings.format(line, objects), true);
	}

	public static void log(Player player, String line, Object... objects)
	{
		LogFile.log(player, Strings.format(line, objects), false);
	}

	public static void log(Player player, Supplier<String> callable)
	{
		LogFile.log(player, callable.get(), false);
	}

	private static void log(Player player, String line, boolean quit)
	{
		LogFile log = Metadata.getValue(player, "actions.log");

		if (log == null)
		{
			log = LogFile.create(player);
		}

		log.write(line);

		if (!quit) return;

		try
		{
			log.writer.close();
		}
		catch (IOException exception) { }

		Metadata.remove(player, "actions.log");
	}

	private void write(String line)
	{
		Assert.isTrue(TryFiles.addLine(this.writer, Strings.format("[{0}] {1}", TimeUtils.formatCurrentTime(), line)));
	}

}