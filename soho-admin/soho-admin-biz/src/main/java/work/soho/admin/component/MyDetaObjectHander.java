package work.soho.admin.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 自动填充更新时间 创建时间
 */
@Component
@Slf4j
public class MyDetaObjectHander implements MetaObjectHandler {
    private static final String UPDATED_TIME = "updatedTime";
    private static final String CREATED_TIME = "createdTime";

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("自动插入时间");
        fillField(CREATED_TIME, metaObject);
        fillField(UPDATED_TIME, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("auto set updated time on update");
        fillField(UPDATED_TIME, metaObject);
    }

    private void fillField(String fieldName, MetaObject metaObject) {
        if (metaObject.hasSetter(fieldName)) {
            Object fieldValue = getFieldValByName(fieldName, metaObject);
            if (fieldValue == null) {
                Class<?> fieldType = metaObject.getGetterType(fieldName);
                if (LocalDateTime.class.equals(fieldType)) {
                    this.setFieldValByName(fieldName, LocalDateTime.now(), metaObject);
                } else if (Date.class.equals(fieldType)) {
                    this.setFieldValByName(fieldName, new Date(), metaObject);
                }
            }
        }
    }
}