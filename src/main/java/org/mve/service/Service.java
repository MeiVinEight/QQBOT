package org.mve.service;

import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Service<T extends Event> implements Comparable<Service<T>>
{
	public final String name;

	protected Service(String name)
	{
		this.name = name;
	}

	public abstract void service(T event, List<SingleMessage> contentList);

	@Override
	public int compareTo(@NotNull Service<T> o)
	{
		return this.name.compareTo(o.name);
	}

	public void subject(T event, List<SingleMessage> contentList)
	{
		if (!this.name.isEmpty())
		{
			String content = contentList.remove(0).toString();
			content = content.substring(name.length()).stripLeading();
			if (!content.isEmpty()) contentList.add(0, new PlainText(content));
		}
		this.service(event, contentList);
	}
}
