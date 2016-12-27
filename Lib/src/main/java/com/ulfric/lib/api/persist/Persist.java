package com.ulfric.lib.api.persist;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public final class Persist {

	static IPersist impl = IPersist.EMPTY;

	private Persist()
	{
	}

	public static ConfigFile newConfig(File file)
	{
		return impl.newConfig(file);
	}

	public static ConfigFile newConfig(File file, boolean dirty)
	{
		return impl.newConfig(file, dirty);
	}

	public static <T> DataPiece<T> newData(String path, ConfigFile file)
	{
		return impl.newData(path, file);
	}

	public static <T> DirtyDataPiece<T> newDirtyData(String path, ConfigFile file)
	{
		return impl.newDirtyData(path, file);
	}

	public static <T> WriteOnSetDataPiece<T> newWriteOnSetData(String path, ConfigFile file)
	{
		return impl.newWriteOnSetData(path, file);
	}

	public static boolean save(FileConfiguration conf, File file)
	{
		return impl.save(conf, file);
	}

	public static File getFile(File dir, String name)
	{
		return impl.getFile(dir, name);
	}

	protected interface IPersist {
		IPersist EMPTY = new IPersist() {
		};

		default ConfigFile newConfig(File file)
		{
			return null;
		}

		default File getFile(File dir, String name)
		{
			return null;
		}

		default ConfigFile newConfig(File file, boolean dirty)
		{
			return null;
		}

		default <T> DataPiece<T> newData(String path, ConfigFile file)
		{
			return null;
		}

		default <T> DirtyDataPiece<T> newDirtyData(String path, ConfigFile file)
		{
			return null;
		}

		default <T> WriteOnSetDataPiece<T> newWriteOnSetData(String path, ConfigFile file)
		{
			return null;
		}

		default boolean save(FileConfiguration conf, File file)
		{
			return false;
		}
	}

}
