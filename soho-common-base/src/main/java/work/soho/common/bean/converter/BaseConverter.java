package work.soho.common.bean.converter;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * <p>
 * BaseConverter
 * </p>
 *
 * @param <S> the type parameter
 * @param <T> the type parameter
 * @author livk
 * @date 2021 /11/4
 */
public interface BaseConverter<S, T> {

	S getSource(T t);

	T getTarget(S s);

	Stream<S> streamSource(Collection<T> ct);

	Stream<T> streamTarget(Collection<S> cs);
}
