package com.ulfric.lib.api.persist;

public final class WriteOnSetDataPiece<T> extends DirtyDataPiece<T> {


	WriteOnSetDataPiece(String path, ConfigFile file)
	{
		super(path, file);
	}

	@Override
	public void set(T value)
	{
		super.set(value);

		this.write();
	}


}
