package org.mve.service;

import net.mamoe.mirai.event.Event;

import java.util.Comparator;

public class ComparatorService<T extends Event> implements Comparator<Service<T>>
{
	@Override
	public int compare(Service<T> o1, Service<T> o2)
	{
		return o1.compareTo(o2);
	}
}
