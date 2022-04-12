package work.soho.common.data.upload.adapter.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.FactoryAdapter;
import work.soho.common.data.upload.Upload;
import work.soho.common.data.upload.adapter.qiniu.QiniuProperties;

import java.util.Arrays;

@Service("file")
@RequiredArgsConstructor
public class Factory implements FactoryAdapter{

    private final Environment environment;

    @Override
    public Upload get(String name) {
        FileProperties fileProperties = new FileProperties();
        Arrays.stream(FileProperties.class.getDeclaredFields()).forEach(item->{
            item.setAccessible(true);
            try {
                item.set(fileProperties, environment.getRequiredProperty("upload.channels." + name + ".config."+item.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return new FileUpload(fileProperties);
    }
}
