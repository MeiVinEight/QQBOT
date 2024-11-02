package org.mve.text;

public class Hexadecimal
{
	public static final	char[] HEX_DIGITS = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

	public static char[] format(byte[] data)
	{
		char[] name = new char[data.length * 2];
		for (int i = 0; i < data.length; i++)
		{
			name[i * 2 + 0] = HEX_DIGITS[(data[i] >> 4) & 0xF];
			name[i * 2 + 1] = HEX_DIGITS[(data[i] >> 0) & 0xF];
		}
		return name;
	}
}
