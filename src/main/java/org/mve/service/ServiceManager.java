package org.mve.service;

import net.mamoe.mirai.event.Event;
import org.mve.type.SearchingType;

import java.util.ArrayList;
import java.util.List;

public class ServiceManager
{
	private final List<Service<Event>> services = new ArrayList<>();

	public void service(Service<?> service)
	{
		this.services.add((Service<Event>) service);
		this.services.sort(new ComparatorService<>());
	}

	public Service<Event> service(Class<? extends Event> type, String line)
	{
		if (line == null) return null;

		SearchingType searching = new SearchingType(Service.class, 0);
		// for (Service<Event> service : services)
		for (int i = this.services.size(); i --> 0;)
		{
			Service<Event> service = this.services.get(i);
			searching.reset().search(service.getClass());
			if (line.startsWith(service.name) && type == searching.generic) return service;
		}
		return null;
	}
}
