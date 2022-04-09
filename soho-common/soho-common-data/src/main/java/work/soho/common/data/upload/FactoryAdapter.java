package work.soho.common.data.upload;

import work.soho.common.data.upload.adapter.cos.CosUpload;
import work.soho.common.data.upload.adapter.oss.AliOssUpload;

public interface FactoryAdapter {
    Upload get(String name);
}
