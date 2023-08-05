package work.soho.common.data.upload.adapter.smb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import work.soho.common.core.util.BinderUtil;
import work.soho.common.data.upload.FactoryAdapter;
import work.soho.common.data.upload.Upload;

@Service("smb")
@RequiredArgsConstructor
public class Factory implements FactoryAdapter {
    @Override
    public Upload get(String name) {
        String keyName = "upload.channels." + name + ".config";
        SmbProperties smbProperties = BinderUtil.bind(keyName, SmbProperties.class);
        return new SmbUpload(smbProperties);
    }
}
