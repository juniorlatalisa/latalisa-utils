package br.dev.juniorlatalisa.builders;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

/**
 * @author juniorlatalisa
 */
public class ListBuilder<E> implements Builder<List<E>> {

	protected ListBuilder(List<E> source) {
		this.source = source;
	}

	public ListBuilder() {
		this(new LinkedList<>());
	}

	private List<E> source;

	@Override
	public List<E> build() {
		return new ArrayList<>(source);
	}

	public ListBuilder<E> add(E e) {
		source.add(e);
		return this;
	}

	public ListBuilder<E> remove(E e) {
		source.remove(e);
		return this;
	}

	public ListBuilder<E> addAll(Collection<? extends E> c) {
		source.addAll(c);
		return this;
	}

	public ListBuilder<E> addAll(int index, Collection<? extends E> c) {
		source.addAll(index, c);
		return this;
	}

	public ListBuilder<E> removeAll(Collection<? extends E> c) {
		source.removeAll(c);
		return this;
	}

	public ListBuilder<E> clear() {
		source.clear();
		return this;
	}

	public ListBuilder<E> set(int index, E element) {
		source.set(index, element);
		return this;
	}

	public ListBuilder<E> add(int index, E element) {
		source.add(index, element);
		return this;
	}

	public ListBuilder<E> remove(int index) {
		source.remove(index);
		return this;
	}

	public static <E> ListBuilder<E> builder() {
		return new ListBuilder<>();
	}

	@SafeVarargs
	public static <E> ListBuilder<E> builder(E element, E... more) {
		return new ListBuilder<>(build(element, more));
	}

	public static <E> ListBuilder<E> builder(Collection<E> elements) {
		return new ListBuilder<>(new ArrayList<>(elements));
	}

	@SafeVarargs
	public static <E> List<E> build(E element, E... more) {
		List<E> source = new ArrayList<>(more.length + 1);
		source.add(element);
		source.addAll(List.of(more));
		return source;
	}

	public static <E> List<E> lazy(Supplier<E[]> constructor) {
		final Object sync = constructor;
		return new AbstractList<E>() {
			private E[] source = null;

			private E[] getSource() {
				synchronized (sync) {
					if (source == null) {
						source = constructor.get();
					}
					return source;
				}
			}

			@Override
			public E get(int index) {
				return getSource()[index];
			}

			@Override
			public int size() {
				return getSource().length;
			}
		};
	}

	public static <E> BinaryOperator<List<E>> binaryOperator() {
		return (acumulador, novo) -> {
			(acumulador == null ? acumulador = new ArrayList<>() : acumulador).addAll(novo == null ? List.of() : novo);
			return acumulador;
		};
	}
}