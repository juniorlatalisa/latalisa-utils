package br.dev.juniorlatalisa.builders;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author juniorlatalisa
 */
public class SetBuilder<E> implements Builder<Set<E>> {

	protected SetBuilder(Set<E> source) {
		this.source = source;
	}

	public SetBuilder() {
		this(new HashSet<>());
	}

	private Set<E> source;

	@Override
	public Set<E> build() {
		return new HashSet<>(source);
	}

	public SetBuilder<E> add(E e) {
		source.add(e);
		return this;
	}

	public SetBuilder<E> remove(E e) {
		source.remove(e);
		return this;
	}

	public SetBuilder<E> addAll(Collection<? extends E> c) {
		source.addAll(c);
		return this;
	}

	public SetBuilder<E> removeAll(Collection<? extends E> c) {
		source.removeAll(c);
		return this;
	}

	public SetBuilder<E> clear() {
		source.clear();
		return this;
	}

	@SafeVarargs
	public static <E> SetBuilder<E> builder(E element, E... more) {
		return new SetBuilder<E>(build(element, more));
	}

	public static <E> SetBuilder<E> builder(Collection<E> elements) {
		return new SetBuilder<E>(new HashSet<E>(elements));
	}

	@SafeVarargs
	public static <E> Set<E> build(E element, E... more) {
		Set<E> source = new HashSet<E>(more.length + 1);
		source.add(element);
		for (E e : more) {
			source.add(e);
		}
		return source;
	}
}