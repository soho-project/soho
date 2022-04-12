package work.soho.common.data.upload.adapter.cos;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import work.soho.common.data.upload.FactoryAdapter;

import java.util.Arrays;

@Service("cos")
@RequiredArgsConstructor
public class Factory implements FactoryAdapter {
    private final Environment environment;

    @Override
    public CosUpload get(String name) {
        CosProperties cosProperties = new CosProperties();
        Arrays.stream(CosProperties.class.getDeclaredFields()).forEach(item->{
            item.setAccessible(true);
            try {
                item.set(cosProperties, environment.getRequiredProperty("upload.channels." + name + ".config."+item.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return new CosUpload(cosProperties);
    }
}
