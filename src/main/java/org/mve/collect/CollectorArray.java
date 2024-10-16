package org.mve.collect;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * A collector tool to set.</br>
 * Use {@link LinkedList} for faster sequential writes.
 * @param <A> Collection element type
 * @param <T> Collection type
 */
public class CollectorArray<A, T extends Collection<A>> implements Collector<A, LinkedList<A>, T>
{
	/**
	 * Finally put all elements to this collection.
	 */
	private final T collection;

	public CollectorArray(T collection)
	{
		this.collection = collection;
	}

	@Override
	public Supplier<LinkedList<A>> supplier()
	{
		return LinkedList::new;
	}

	@Override
	public BiConsumer<LinkedList<A>, A> accumulator()
	{
		return Collection::add;
	}

	@Override
	public BinaryOperator<LinkedList<A>> combiner()
	{
		return (a, b) ->
		{
			a.addAll(b);
			return a;
		};
	}

	@Override
	public Function<LinkedList<A>, T> finisher()
	{
		return x ->
		{
			CollectorArray.this.collection.addAll(x);
			return CollectorArray.this.collection;
		};
	}

	@Override
	public Set<Characteristics> characteristics()
	{
		return Set.of();
	}
}
