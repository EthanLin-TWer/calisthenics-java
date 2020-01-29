package com.theladders.avital.cc;

public class Application {
	private final int x;

	public Application(int x) {
		this.x = x;
	}

	public int calculate(int y) {
		return x + y;
	}
}
