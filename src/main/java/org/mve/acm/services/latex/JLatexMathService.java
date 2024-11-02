package org.mve.acm.services.latex;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.acm.QQBOT;
import org.mve.mirai.Message;
import org.mve.service.Service;
import org.mve.text.Hexadecimal;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.LinkedList;

public class JLatexMathService extends Service<MessageEvent>
{
	public static final String SERVICE = "/latex";
	public static final File LATEX_PNG_FOLDER = new File("latex");
	public static final	char[] HEX_DIGITS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	public JLatexMathService()
	{
		super(JLatexMathService.SERVICE);
	}

	@Override
	public void service(MessageEvent event, LinkedList<SingleMessage> contentList)
	{
		String text = Message.message(contentList).contentToString();

		// Latex to image
		int style = TeXConstants.STYLE_DISPLAY;
		float fontSize = 24;
		Color fg = Color.BLACK;
		Color bg = null;
		try
		{
			BufferedImage image = (BufferedImage) TeXFormula.createBufferedImage(text, style, fontSize, fg, bg);
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", byteArray);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(byteArray.toByteArray());
			byte[] digest = md.digest();
			String fileName = (new String(Hexadecimal.format(digest))) + ".PNG";
			File latexFile = new File(LATEX_PNG_FOLDER, fileName);
			boolean ignoredBoolean = latexFile.getParentFile().mkdirs();
			try (FileOutputStream out = new FileOutputStream(latexFile))
			{
				out.write(byteArray.toByteArray());
				out.flush();
			}
			QQBOT.BOT.getLogger().verbose("LATEX PNG: " + latexFile.getPath());

			Image msg = Contact.uploadImage(event.getSubject(), latexFile);
			event.getSubject().sendMessage(msg);
		}
		catch (Throwable e)
		{
			QQBOT.BOT.getLogger().error(e);
			event.getSubject().sendMessage(e.getMessage());
		}
	}
}
