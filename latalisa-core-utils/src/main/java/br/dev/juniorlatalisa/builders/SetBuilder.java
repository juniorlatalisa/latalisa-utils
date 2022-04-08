package br.dev.juniorlatalisa.builders;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

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
		return new SetBuilder<>(build(element, more));
	}

	public static <E> SetBuilder<E> builder(Collection<E> elements) {
		return new SetBuilder<>(new HashSet<>(elements));
	}

	@SafeVarargs
	public static <E> Set<E> build(E element, E... more) {
		Set<E> source = new HashSet<>(more.length + 1);
		source.add(element);
		source.addAll(List.of(more));
		return source;
	}

	public static <E> BinaryOperator<Set<E>> binaryOperator() {
		return (acumulador, novo) -> {
			(acumulador == null ? acumulador = new HashSet<>() : acumulador).addAll(novo == null ? Set.of() : novo);
			return acumulador;
		};
	}
}