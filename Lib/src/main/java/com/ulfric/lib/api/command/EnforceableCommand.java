package com.ulfric.lib.api.command;

@FunctionalInterface
interface EnforceableCommand {

	boolean enforce();
}
