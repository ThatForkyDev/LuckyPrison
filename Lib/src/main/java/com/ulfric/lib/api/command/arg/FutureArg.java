package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;

final class FutureArg implements ArgStrategy<Timestamp> {


	@Override
	public Timestamp match(String string)
	{
		long time = Milliseconds.fromSeconds(TimeUtils.parseSeconds(string));

		return (time <= 0 ? null : Timestamp.future(time));
	}


}
