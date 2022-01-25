package work.soho.common.bean.util;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * BeanUtilsDemo
 * </p>
 *
 * @author livk
 * @date 2022/1/24
 */
public class BeanUtilsDemo {

	public static void main(String[] args) {
		C c = new C().setA(1).setB(2);
		D d = BeanUtils.copy(c, D.class);
		System.out.println(d);
	}

}

@Data
@Accessors(chain = true)
class C {

	private Integer a;

	private Integer b;

}

@Data
class D {

	private Integer a;

}
