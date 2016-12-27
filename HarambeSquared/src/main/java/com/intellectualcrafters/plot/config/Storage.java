package com.intellectualcrafters.plot.config;

import java.io.File;

public class Storage extends Config {

	public static String PREFIX = "";

	public static void save(File file)
	{
		save(file, Storage.class);
	}

	public static void load(File file)
	{
		load(file, Storage.class);
	}

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	@Config.Comment("MySQL section")
	public static final class MySQL {
		@Config.Comment("Should MySQL be used?")
		public static boolean USE;
		public static String HOST = "localhost";
		public static String PORT = "3306";
		public static String USER = "root";
		public static String PASSWORD = "password";
		public static String DATABASE = "plot_db";
	}

	@SuppressWarnings("UtilityClassWithoutPrivateConstructor")
	@Config.Comment("SQLite section")
	public static final class SQLite {
		@Config.Comment("Should SQLite be used?")
		public static boolean USE = true;
		@Config.Comment("The file to use")
		public static String DB = "storage";
	}
}
