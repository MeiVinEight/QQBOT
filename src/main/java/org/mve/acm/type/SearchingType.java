package org.mve.acm.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * Search generic type for {@link SearchingType#type} in target clazz</br>
 * <blockquote><pre>
 *     public interface IFoo1<A, B, C> extends Consumer<B>
 *     {
 *     }
 *     public interface IFoo2<T> extends IFoo1<String, T, Class<?>>
 *     {
 *     }
 *     public abstract class AFoo implements IFoo2<Map<String, String>>
 *     {
 *     }
 * </pre></blockquote>
 * If use {@code new SearchingType(Consumer.class, 0).search(AFoo.class)}:</br>
 * 1. It will find target param's index in target type is 0 (constructor's 2nd param).</br>
 * 2. It will find the target generic param in IFoo1 is {@code "B"}, not a type, find index of
 * {@code "B"} in current type {@code IFoo1} is 1.</br>
 * 3. It will find the target generic param in IFoo2 is {@code "T"}, not a type, find index of
 * {@code "T"} in current type {@code IFoo2} is 0.</br>
 * 4. It will find the target generic param in AFoo is {@code Map<String, String>}, is a type,
 * set {@link SearchingType#generic} to {@code Map.class}.</br>
 */
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
		// If searching class is target generic class, return target generic index
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
			/*
			 * Search for index for generic parameters in subclass, -1 means not found
			 */
			int idx = this.search(type);
			if (idx > -1)
			{
				if (genericType instanceof ParameterizedType)
				{
					// Current subclass type has generic param
					ParameterizedType parameterizedType = (ParameterizedType) genericType;
					// Get generic arg at index
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
						// Generic arg not a type
						// Search for index for generic in current searching type
						return which(clazz, typeArg.getTypeName());
					}
				}
				else
				{
					// Current subclass type has no generic param
					// but this class has generic param required (idx != -1)
					// generic type is Object (Not declared generic type)
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
