package work.soho.common.data.upload;

import lombok.RequiredArgsConstructor;
import org.joda.time.IllegalInstantException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import work.soho.common.core.support.SpringContextHolder;

@Service
@RequiredArgsConstructor
public class UploadManage {
    private final Environment environment;

    /**
     * 获取对应的上传实现
     *
     * @param name
     * @return
     */
    public Upload get(String name) {
        Boolean enable = environment.getProperty("upload.channels." + name + ".enable", Boolean.class, false);
        if(Boolean.FALSE.equals(enable)) {
            throw new IllegalInstantException("请指定正确的上传方式");
        }
        String type = environment.getProperty("upload.channels." + name + ".type");
        if(type == null) {
            throw new IllegalInstantException("请指定正确的上传类型");
        }
        FactoryAdapter factoryAdapter = SpringContextHolder.getBean(type);
        return factoryAdapter.get(name);
    }

    /**
     * 获取默认上传通道
     *
     * @return
     */
    public Upload getDefault() {
        return get(getDefaultChannelName());
    }

    /**
     * 获取默认上传通道
     *
     * @return
     */
    public String getDefaultChannelName() {
        return environment.getProperty("upload.defaultChannel");
    }
}
