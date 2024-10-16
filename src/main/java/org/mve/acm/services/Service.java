package org.mve.acm.services;

import net.mamoe.mirai.event.AbstractEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public abstract class Service<T extends AbstractEvent> implements Comparable<Service<T>>
{
	public final String name;

	protected Service(String name)
	{
		this.name = name;
	}

	public abstract void service(T event, LinkedList<SingleMessage> contentList);

	@Override
	public int compareTo(@NotNull Service<T> o)
	{
		return this.name.compareTo(o.name);
	}
}
