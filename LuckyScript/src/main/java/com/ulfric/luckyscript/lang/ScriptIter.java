package com.ulfric.luckyscript.lang;

import java.util.Iterator;

import com.ulfric.luckyscript.lang.act.Action;

public class ScriptIter implements Iterator<Action<?>> {


	public ScriptIter(Iterator<Action<?>> actions)
	{
		this.actions = actions;
	}

	private final Iterator<Action<?>> actions;

	private boolean continues = true;
	public boolean canContinue() { return this.continues; }
	public void setContinues(boolean continues) { this.continues = continues; }

	private int chains;
	public int getChains() { return this.chains; }
	public boolean isChained() { return this.chains > 0; }
	public void addChain() { this.chains++; }
	public void useChain()
	{
		this.chains--;

		if (this.chains > 0) return;

		this.setContinues(false);
	}

	@Override
	public void remove()
	{
		this.actions.remove();
	}

	@Override
	public boolean hasNext()
	{
		return this.continues && this.actions.hasNext();
	}

	@Override
	public Action<?> next()
	{
		return this.actions.next();
	}


}