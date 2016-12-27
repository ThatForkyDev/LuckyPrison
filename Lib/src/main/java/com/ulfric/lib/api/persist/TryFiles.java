package com.ulfric.lib.api.persist;

import java.io.File;
import java.io.Writer;
import java.util.List;

public final class TryFiles {

	static ITryFiles impl = ITryFiles.EMPTY;

	private TryFiles()
	{
	}

	public static List<String> readLines(File file)
	{
		return impl.readLines(file);
	}

	public static boolean addLine(File file, String line)
	{
		return impl.addLine(file, line);
	}

	public static boolean addLine(Writer writer, String line)
	{
		return impl.addLine(writer, line);
	}

	public static boolean copy(File file, File dest)
	{
		return impl.copy(file, dest);
	}

	protected interface ITryFiles {
		ITryFiles EMPTY = new ITryFiles() {
		};

		default List<String> readLines(File file)
		{
			return null;
		}

		default boolean addLine(File file, String line)
		{
			return false;
		}

		default boolean copy(File file, File dest)
		{
			return false;
		}

		default boolean addLine(Writer writer, String line)
		{
			return false;
		}
	}

}
