package work.soho.admin.mapper;

import java.util.List;

public interface BaseMapper<T> {

	public T getById(Integer id);

	public List<T> select(T model);

	public int insert(T model);

	public int update(T model);

}
