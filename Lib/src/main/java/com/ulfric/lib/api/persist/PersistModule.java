package com.ulfric.lib.api.persist;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.ulfric.lib.api.module.SimpleModule;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public final class PersistModule extends SimpleModule {

	public PersistModule()
	{
		super("persist", "Persistence utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new TryFilesModule());
	}

	@Override
	public void postEnable()
	{
		Persist.impl = new Persist.IPersist() {
			@Override
			public ConfigFile newConfig(File file)
			{
				return new ConfigFile(file);
			}

			@Override
			public File getFile(File dir, String name)
			{
				name = name.toLowerCase();

				String[] names = dir.list();
				if (names == null) return null;

				for (String fileName : names)
				{
					if (fileName.toLowerCase().equals(name))
					{
						return new File(dir, fileName);
					}
				}

				return null;
			}

			@Override
			public ConfigFile newConfig(File file, boolean dirty)
			{
				return new ConfigFile(file, dirty);
			}

			@Override
			public <T> DataPiece<T> newData(String path, ConfigFile file)
			{
				return new DirtyDataPiece<>(path, file);
			}

			@Override
			public <T> DirtyDataPiece<T> newDirtyData(String path, ConfigFile file)
			{
				return new DirtyDataPiece<>(path, file);
			}

			@Override
			public <T> WriteOnSetDataPiece<T> newWriteOnSetData(String path, ConfigFile file)
			{
				return new WriteOnSetDataPiece<>(path, file);
			}

			@Override
			public boolean save(FileConfiguration conf, File file)
			{
				try
				{
					conf.save(file);

					return true;
				}
				catch (IOException exception) { return false; }
			}
		};
	}

	private static final class TryFilesModule extends SimpleModule {

		TryFilesModule()
		{
			super("tryfiles", "TryFiles quicktry file utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			TryFiles.impl = new TryFiles.ITryFiles() {
				@Override
				public List<String> readLines(File file)
				{
					try
					{
						return Files.readLines(file, Charset.defaultCharset());
					}
					catch (IOException exception) {
						return ImmutableList.of();
					}
				}

				@Override
				public boolean addLine(File file, String line)
				{
					try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), Charsets.UTF_8)))
					{
						writer.append(StringUtils.chomp(line)).append('\n');
						return true;
					}
					catch (IOException exception) {
						return false;
					}
				}

				@Override
				public boolean copy(File file, File dest)
				{
					try
					{
						Files.copy(file, dest);
						return true;
					}
					catch (IOException e) {
						return false;
					}
				}

				@Override
				public boolean addLine(Writer writer, String line)
				{
					try
					{
						writer.append(StringUtils.chomp(line)).append('\n');
						return true;
					}
					catch (IOException exception) {
						return false;
					}
				}
			};
		}

		@Override
		public void postDisable()
		{
			TryFiles.impl = TryFiles.ITryFiles.EMPTY;
		}
	}

	@Override
	public void postDisable()
	{
		Persist.impl = Persist.IPersist.EMPTY;
	}
}
