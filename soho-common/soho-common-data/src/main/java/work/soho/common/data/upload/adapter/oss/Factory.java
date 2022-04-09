package work.soho.common.data.upload.adapter.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.FactoryAdapter;

import java.util.Arrays;

@Service("oss")
@RequiredArgsConstructor
public class Factory implements FactoryAdapter {
    private final Environment environment;


    public AliOssUpload get(String name) {
        AliOssProperties aliOssProperties = new AliOssProperties();
        Arrays.stream(AliOssProperties.class.getDeclaredFields()).forEach(item->{
            item.setAccessible(true);
            try {
                item.set(aliOssProperties, environment.getRequiredProperty("upload." + name + ".config."+item.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return new AliOssUpload(aliOssProperties);
    }
}
