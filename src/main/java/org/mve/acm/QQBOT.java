package org.mve.acm;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.events.MessageEvent;
import org.mve.acm.controller.CodeforcesController;
import org.mve.invoke.common.JavaVM;
import org.mve.service.ServiceManager;
import org.mve.sql.Database;
import top.mrxiaom.overflow.BotBuilder;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.function.Consumer;

public class QQBOT implements Consumer<MessageEvent>
{
	private static final String PROPERTIES_FILENAME = "bot.properties";
	private static final String PROPERTY_KEY_FORWARD_WEBSOCKET_HOST = "forward.websocket.host";
	private static final String PROPERTY_KEY_FORWARD_WEBSOCKET_TOKEN = "forward.websocket.token";
	private static final String PROPERTY_KEY_REVERSE_WEBSOCKET_PORT = "reverse.websocket.port";
	private static final String PROPERTY_KEY_REVERSE_WEBSOCKET_TOKEN = "reverse.websocket.token";
	private static final String PROPERTY_KEY_MYSQL_URL = "mysql.url";
	private static final String PROPERTY_KEY_MYSQL_USERNAME = "mysql.username";
	private static final String PROPERTY_KEY_MYSQL_PASSWORD = "mysql.password";
	private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/";

	public static final Properties PROPERTIES = new Properties();
	public static final String MYSQL_URL;
	public static final String MYSQL_USERNAME;
	public static final String MYSQL_PASSWORD;
	public static final Database DATABASE;
	public static final Bot BOT;

	private final ServiceManager service = new ServiceManager();

	public QQBOT()
	{
		this.service.service(new CodeforcesController());
	}

	@Override
	public void accept(MessageEvent messageEvent)
	{
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
		Runtime.getRuntime().addShutdownHook(new Thread(QQBOT.BOT::close));

		// Get mysql properties
		String mysqlUrl = DEFAULT_MYSQL_URL;
		if (PROPERTIES.containsKey(PROPERTY_KEY_MYSQL_URL)) mysqlUrl = PROPERTIES.getProperty(PROPERTY_KEY_MYSQL_URL);
		MYSQL_URL = mysqlUrl;
		String mysqlUsername = PROPERTIES.getProperty(PROPERTY_KEY_MYSQL_USERNAME);
		String mysqlPassword = PROPERTIES.getProperty(PROPERTY_KEY_MYSQL_PASSWORD);
		if (mysqlUrl == null || mysqlUsername == null || mysqlPassword == null)
		{
			throw new IllegalArgumentException("Wrong mysql properties");
		}
		MYSQL_USERNAME = mysqlUsername;
		MYSQL_PASSWORD = mysqlPassword;
		DATABASE = new Database(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
		try
		{
			// TODO Use property
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{
			BOT.getLogger().error(e);
		}
	}
}
