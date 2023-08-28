package br.dev.juniorlatalisa.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ComparatorBuilder<T> {

	public ComparatorBuilder() {
		this(new LinkedList<>());
	}

	protected ComparatorBuilder(List<Comparator<T>> comparators) {
		this.comparators = comparators;
	}

	private final List<Comparator<T>> comparators;

	public Comparator<T> build() {
		return (value1, value2) -> compare(value1, value2, List.copyOf(comparators));
	}

	public Comparator<T> buildReverseOrder() {
		return Collections.reverseOrder(build());
	}

	protected static <T> int compare(T value1, T value2, List<Comparator<T>> comparators) {
		// Null será movido para o começo.
		if (value1 == null) {
			return -1;
		}
		if (value2 == null) {
			return 1;
		}
		int value;
		for (Comparator<T> comparator : comparators) {
			if ((value = comparator.compare(value1, value2)) != 0) {
				return value;
			}
		}
		return value1.equals(value2) ? 0 : -1;
	}

	public ComparatorBuilder<T> add(Comparator<T> comparator) {
		this.comparators.add(comparator);
		return this;
	}

	public ComparatorBuilder<T> add(Collection<Comparator<T>> comparators) {
		this.comparators.addAll(comparators);
		return this;
	}

	public <C extends Comparable<C>> ComparatorBuilder<T> add(Function<T, C> function) {
		this.comparators.add(createComparator(function));
		return this;
	}

	public <C> ComparatorBuilder<T> add(Function<T, C> function, Comparator<C> comparator) {
		this.comparators.add(createComparator(function, comparator));
		return this;
	}

	protected <C extends Comparable<C>> Comparator<T> createComparator(Function<T, C> function) {
		return createComparator(function, Comparable::compareTo);
	}

	protected <C> Comparator<T> createComparator(Function<T, C> function, Comparator<C> comparator) {
		return (value1, value2) -> compare(value1 == null ? null : function.apply(value1),
				value2 == null ? null : function.apply(value2), List.of(comparator));
	}

	@SafeVarargs
	public static <E> ComparatorBuilder<E> builder(Comparator<E> element, Comparator<E>... more) {
		return new ComparatorBuilder<>(getSource(element, more));
	}

	public static <E> ComparatorBuilder<E> builder(Collection<Comparator<E>> elements) {
		return new ComparatorBuilder<>(new ArrayList<>(elements));
	}

	@SafeVarargs
	public static <E> Comparator<E> build(Comparator<E> element, Comparator<E>... more) {
		return new ComparatorBuilder<>(getSource(element, more)).build();
	}

	@SafeVarargs
	protected static <E> List<Comparator<E>> getSource(Comparator<E> element, Comparator<E>... more) {
		List<Comparator<E>> source = new ArrayList<>(more.length + 1);
		source.add(element);
		if (more.length > 0) {
			source.addAll(List.of(more));
		}
		return source;
	}

	public static <E, C extends Comparable<C>> ComparatorBuilder<E> builder(Function<E, C> function) {
		return new ComparatorBuilder<E>().add(function);
	}

	public static <E, C extends Comparable<C>> Comparator<E> build(Function<E, C> function) {
		return new ComparatorBuilder<E>().createComparator(function);
	}

	public static <E, C> ComparatorBuilder<E> builder(Function<E, C> function, Comparator<C> comparator) {
		return new ComparatorBuilder<E>().add(function, comparator);
	}

	public static <E, C> Comparator<E> build(Function<E, C> function, Comparator<C> comparator) {
		return new ComparatorBuilder<E>().createComparator(function, comparator);
	}
}