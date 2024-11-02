package org.mve.selenium;

import org.jetbrains.annotations.NotNull;
import org.mve.text.Hexadecimal;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;

public class TemporarySHA1 implements OutputType<File>
{
	public final File folder;

	public TemporarySHA1(String folderPath)
	{
		this.folder = new File(folderPath);
		if (!this.folder.exists())
		{
			boolean ignored = this.folder.mkdirs();
		}
	}

	@NotNull
	@Override
	public File convertFromBase64Png(@NotNull String base64Png)
	{
		return this.temporary(OutputType.BYTES.convertFromBase64Png(base64Png));
	}

	@NotNull
	@Override
	public File convertFromPngBytes(@NotNull byte[] png)
	{
		return  temporary(png);
	}

	private File temporary(byte[] data)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] hash = digest.digest(data);
			String fileName = (new String(Hexadecimal.format(hash))) + ".PNG";
			File tempFile = new File(folder, fileName);
			Files.write(tempFile.toPath(), data);
			return tempFile;
		}
		catch (Throwable t)
		{
			throw new WebDriverException(t);
		}
	}
}
