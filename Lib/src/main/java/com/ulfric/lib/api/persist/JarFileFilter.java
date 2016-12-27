package com.ulfric.lib.api.persist;

import java.io.File;
import java.io.FilenameFilter;

public final class JarFileFilter implements FilenameFilter {

	private final String name;

	public JarFileFilter(String name)
	{
		this.name = name.toLowerCase() + ".jar";
	}

	@Override
	public boolean accept(File dir, String name)
	{
		return name.toLowerCase().equals(this.name);
	}

}
