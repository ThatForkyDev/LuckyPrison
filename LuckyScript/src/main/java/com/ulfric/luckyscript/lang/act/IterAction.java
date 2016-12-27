package com.ulfric.luckyscript.lang.act;

import java.util.List;

import com.ulfric.uspigot.metadata.LocatableMetadatable;

public interface IterAction extends SpecialAction {

	default List<LocatableMetadatable> getObjects()
	{
		throw new UnsupportedOperationException();
	}

}