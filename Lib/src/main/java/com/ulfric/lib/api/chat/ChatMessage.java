package com.ulfric.lib.api.chat;

import com.ulfric.lib.api.time.Timestamp;
import com.ulfric.lib.api.tuple.Pair;

public final class ChatMessage extends Pair<Timestamp, String> {

	ChatMessage(Timestamp time, String message)
	{
		super(time, message);
	}

	public Timestamp getTime()
	{
		return this.getA();
	}

	public String getMessage()
	{
		return this.getB();
	}

}
