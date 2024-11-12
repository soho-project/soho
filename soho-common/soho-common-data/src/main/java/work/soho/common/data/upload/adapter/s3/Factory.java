package work.soho.common.data.upload.adapter.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.FactoryAdapter;
import work.soho.common.data.upload.Upload;

import java.util.Arrays;

/**
 * 兼容S3存储的云存储：
 *
 * MinIO：开源的对象存储服务器，完全兼容S3协议，适合私有云部署。
 * Ceph：分布式存储平台，其RADOS Gateway组件兼容S3协议，适合私有和混合云场景。
 * 华为云OBS：华为云的对象存储服务，支持S3协议，可以与AWS S3工具直接兼容。
 * 腾讯云COS：腾讯云对象存储兼容S3 API，用户可通过S3协议进行存储操作。
 * 阿里云OSS：阿里云对象存储服务，通过SDK和API支持S3兼容模式。
 * 青云QingStor：青云的QingStor对象存储服务也支持S3协议，适合国内用户。
 * DigitalOcean Spaces：DigitalOcean的对象存储服务，提供S3兼容接口，适合海外业务。
 * Wasabi：兼容S3协议的云存储服务，价格较低，主要面向数据归档和备份需求。
 * Backblaze B2：Backblaze的云存储服务最近也开始支持S3协议接口。
 * Scality：Scality的对象存储（如RING、Zenko），支持S3协议，适合大规模企业级存储需求。
 */
@Service("s3")
@RequiredArgsConstructor
public class Factory implements FactoryAdapter {
    private final Environment environment;

    @Override
    public Upload get(String name) {
        S3Properties s3Properties = new S3Properties();
        Arrays.stream(S3Properties.class.getDeclaredFields()).forEach(item->{
            item.setAccessible(true);
            try {
                item.set(s3Properties, environment.getRequiredProperty("upload.channels." + name + ".config."+item.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return new S3Upload(s3Properties);
    }
}
