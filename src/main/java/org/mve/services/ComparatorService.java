package org.mve.services;

import net.mamoe.mirai.event.AbstractEvent;

import java.util.Comparator;

public class ComparatorService<T extends AbstractEvent> implements Comparator<Service<T>>
{
	@Override
	public int compare(Service<T> o1, Service<T> o2)
	{
		return o1.compareTo(o2);
	}
}
