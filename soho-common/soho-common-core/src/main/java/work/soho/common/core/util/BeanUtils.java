package work.soho.common.core.util;

import cn.hutool.core.codec.Base64;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * BeanUtils
 * </p>
 *
 * @author livk
 * @date 2022/1/20
 */
@UtilityClass
public class BeanUtils {

	/**
	 * 基于BeanUtils的复制
	 * @param source 目标源
	 * @param targetClass 需复制的结果类型
	 * @param <T> 类型
	 * @return result
	 */
	@SneakyThrows
	public <T> T copy(Object source, Class<T> targetClass) {
		Constructor<T> constructor = targetClass.getConstructor();
		T t = constructor.newInstance();
		org.springframework.beans.BeanUtils.copyProperties(source, t);
		return t;
	}

	/**
	 * list类型复制
	 * @param sourceList 目标list
	 * @param targetClass class类型
	 * @param <T> 类型
	 * @return result list
	 */
	public <T> List<T> copyList(Collection<?> sourceList, Class<T> targetClass) {
		return sourceList.stream().map(source -> copy(source, targetClass)).collect(Collectors.toList());
	}

	/**
	 * 序列化bean
	 *
	 * @param obj
	 * @return
	 */
	@SneakyThrows
	public static byte[] serializeBean(Object obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		return baos.toByteArray();
	}

	/**
	 * 序列化bean到String
	 *
	 * @param obj
	 * @return
	 */
	public static String serializeBean2String(Object obj) {
		return Base64.encode(serializeBean(obj));
	}

	/**
	 * 反序列化bean
	 *
	 * @param bytes
	 * @param clazz
	 * @return
	 * @param <T>
	 */
	@SneakyThrows
	public static  <T> T deserializeBean(byte[] bytes, Class<T> clazz) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object obj = ois.readObject();
		return clazz.cast(obj);
	}

	/**
	 * 从字符串反序列化bean
	 *
	 * @param data
	 * @param clazz
	 * @return
	 * @param <T>
	 */
	public static  <T> T deserializeBeanFromString(String data, Class<T> clazz) {
		return deserializeBean(Base64.decode(data), clazz);
	}
}
