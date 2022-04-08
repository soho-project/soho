package work.soho.admin.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

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
        if(metaObject.hasSetter(CREATED_TIME)) {
            this.setFieldValByName(CREATED_TIME, new Date(), metaObject);
        }

        if(metaObject.hasSetter(UPDATED_TIME)) {
            this.setFieldValByName(UPDATED_TIME, new Date(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("auto set updated time  on  update");
        if(metaObject.hasSetter(UPDATED_TIME)) {
            this.setFieldValByName(UPDATED_TIME, new Date(), metaObject);
        }
    }
}