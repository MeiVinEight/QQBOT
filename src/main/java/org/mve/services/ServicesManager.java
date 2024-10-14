package org.mve.services;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.Event;
import org.mve.acm.type.SearchingType;
import org.mve.invoke.common.JavaVM;
import top.mrxiaom.overflow.BotBuilder;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.function.Consumer;

public class ServicesManager
{
	private static final String PROPERTIES_FILENAME = "services.properties";
	private static final String PROPERTY_KEY_FORWARD_WEBSOCKET_HOST = "forward.websocket.host";
	private static final String PROPERTY_KEY_FORWARD_WEBSOCKET_TOKEN = "forward.websocket.token";
	private static final String PROPERTY_KEY_REVERSE_WEBSOCKET_PORT = "reverse.websocket.port";
	private static final String PROPERTY_KEY_REVERSE_WEBSOCKET_TOKEN = "reverse.websocket.token";
	private static final Properties PROPERTIES = new Properties();
	public static final Bot BOT;

	public static <E extends Event, Y extends Consumer<E>> void service(Y consumer)
	{
		SearchingType searching = new SearchingType(Consumer.class, 0);
		searching.search(consumer.getClass());
		Class<E> clazz = (Class<E>) searching.generic;
		ServicesManager.BOT.getEventChannel().subscribeAlways(clazz, consumer);
	}

	static
	{
		// Load properties file
		try (FileInputStream in = new FileInputStream(PROPERTIES_FILENAME))
		{
			PROPERTIES.load(in);
		}
		catch (Throwable t)
		{
			// Weak throw all exception
			JavaVM.exception(t);
		}

		// Create bot
		Bot bot = null;
		// Try forward websocket
		if (PROPERTIES.containsKey(PROPERTY_KEY_FORWARD_WEBSOCKET_HOST))
		{
			String host = PROPERTIES.getProperty(PROPERTY_KEY_FORWARD_WEBSOCKET_HOST);
			String token = PROPERTIES.getProperty(PROPERTY_KEY_FORWARD_WEBSOCKET_TOKEN);
			BotBuilder builder = BotBuilder.positive(host);
			if (token != null) builder.token(token);
			bot = builder.connect();
		}

		// Try reverse websocket
		if (bot == null && PROPERTIES.containsKey(PROPERTY_KEY_REVERSE_WEBSOCKET_PORT))
		{
			int port = Integer.parseInt(PROPERTIES.getProperty(PROPERTY_KEY_REVERSE_WEBSOCKET_PORT));
			String token = PROPERTIES.getProperty(PROPERTY_KEY_REVERSE_WEBSOCKET_TOKEN);
			BotBuilder builder = BotBuilder.reversed(port);
			if (token != null) builder.token(token);
			bot = builder.connect();
		}

		BOT = bot;

		// Not found or connect
		if (BOT == null)
		{
			throw new NullPointerException("Service not found");
		}

		Runtime.getRuntime().addShutdownHook(new Thread(ServicesManager.BOT::close));
	}
}
