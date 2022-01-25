package work.soho.common.bean.converter;

import lombok.Data;
import lombok.experimental.Accessors;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * <p>
 * BeanConverter
 * </p>
 *
 * @author livk
 * @date 2022/1/24
 */
@Mapper
public interface BeanConverter extends BaseConverter<A, B> {
    BeanConverter INSTANCE = Mappers.getMapper(BeanConverter.class);

    public static void main(String[] args) {
        A a = new A().setA(1).setB(2);
        B b = INSTANCE.getTarget(a);
        System.out.println(b);
    }
}

@Data
@Accessors(chain = true)
class A {
    private Integer a;
    private Integer b;
}

@Data
class B {
    private Integer a;
}
