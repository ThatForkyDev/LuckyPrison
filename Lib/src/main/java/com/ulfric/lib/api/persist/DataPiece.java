package com.ulfric.lib.api.persist;

public interface DataPiece<T> {


	String getPath();

	ConfigFile getOwner();

	void write();

	void set(T value);

	T get();


}