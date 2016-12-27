package com.ulfric.lib.api.persist;

@SuppressWarnings("unchecked")
public class DirtyDataPiece<T> implements DataPiece<T> {


	private final String path;
	private final ConfigFile owner;
	private T value;

	protected DirtyDataPiece(String path, ConfigFile file)
	{
		this.path = path;
		this.owner = file;
		this.value = (T) file.getConf().get(path);
	}

	@Override
	public String getPath()
	{
		return this.path;
	}

	@Override
	public ConfigFile getOwner()
	{
		return this.owner;
	}

	@Override
	public void write()
	{
		this.owner.getConf().set(this.path, this.value);

		this.owner.save();
	}

	@Override
	public void set(T value)
	{
		this.value = value;
	}

	@Override
	public T get()
	{
		return this.value;
	}


}
