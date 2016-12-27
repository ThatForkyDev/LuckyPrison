package com.ulfric.lib.api.command.arg;

final class BooleanArg implements ArgStrategy<Boolean> {


	@Override
	public Boolean match(String string)
	{
		switch (string.toLowerCase())
		{
			case "true":
			case "allow":
			case "yes":
				return true;

			case "false":
			case "deny":
			case "no":
				return false;

			default:
				return null;
		}
	}


}
