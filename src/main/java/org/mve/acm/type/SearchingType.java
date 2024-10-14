package org.mve.acm.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

public class SearchingType
{
	public final Class<?> type;
	public final int index;
	public Class<?> generic = null;

	public SearchingType(Class<?> type, int index)
	{
		this.type = type;
		this.index = index;
	}

	public int search(Class<?> clazz)
	{
		if (clazz == null) return -1;
		if (clazz == this.type) return index;

		// Foreach interfaces
		Type[] genericInterfaces = clazz.getGenericInterfaces();
		genericInterfaces = Arrays.copyOf(genericInterfaces, genericInterfaces.length + 1);
		genericInterfaces[genericInterfaces.length - 1] = clazz.getSuperclass();
		for (int i = 0; i < genericInterfaces.length && this.generic == null; i++)
		{
			Type genericType = genericInterfaces[i];
			// Get interface type
			Class<?> type;
			if (genericType instanceof ParameterizedType)
			{
				type = (Class<?>) ((ParameterizedType) genericType).getRawType();
			}
			else
			{
				type = (Class<?>) genericType;
			}
			int idx = this.search(type);
			if (idx > -1)
			{
				if (genericType instanceof ParameterizedType)
				{
					ParameterizedType parameterizedType = (ParameterizedType) genericType;
					Type typeArg = parameterizedType.getActualTypeArguments()[idx];
					if (typeArg instanceof Class)
					{
						this.generic = (Class<?>) typeArg;
						return -1;
					}
					else if (typeArg instanceof ParameterizedType)
					{
						this.generic = (Class<?>) ((ParameterizedType) typeArg).getRawType();
						return -1;
					}
					else
					{
						return which(clazz, typeArg.getTypeName());
					}
				}
				else
				{
					this.generic = Object.class;
					return -1;
				}
			}
		}

		return -1;
	}

	public int which(Class<?> clazz, String genericName)
	{
		TypeVariable<? extends Class<?>>[] typeParameters = clazz.getTypeParameters();
		for (int i = 0; i < typeParameters.length; i++)
		{
			if (typeParameters[i].getName().equals(genericName)) return i;
		}
		return -1;
	}
}
