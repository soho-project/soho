package work.soho.common.data.upload.adapter.qiniu;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.FactoryAdapter;
import work.soho.common.data.upload.adapter.oss.AliOssProperties;

import java.util.Arrays;

@Service("qiniu")
@RequiredArgsConstructor
public class Factory implements FactoryAdapter {
    private final Environment environment;

    /**
     * 获取七牛上传
     *
     * @param name
     * @return
     */
    public QiniuUpload get(String name) {
        QiniuProperties qiniuProperties = new QiniuProperties();
        Arrays.stream(QiniuProperties.class.getDeclaredFields()).forEach(item->{
            item.setAccessible(true);
            try {
                item.set(qiniuProperties, environment.getRequiredProperty("upload.channels." + name + ".config."+item.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return new QiniuUpload(qiniuProperties);
    }
}
