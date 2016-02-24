package com.skymiracle.server.listener;

public interface Listener {

	Event checkEvent();

	void dealWithEvent(Event event);

	void initStart();

	public interface Event {

	}
}
