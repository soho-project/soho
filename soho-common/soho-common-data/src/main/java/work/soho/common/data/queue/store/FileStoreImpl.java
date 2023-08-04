package work.soho.common.data.queue.store;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import work.soho.common.core.util.BeanUtils;
import work.soho.common.data.queue.message.DelayedMessage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@Component
@ConditionalOnProperty(value = "soho.delayed-queue.store.drive", havingValue = "file")
public class FileStoreImpl implements StoreInterface {
    @Value("${soho.delayed-queue.store.path}")
    private String filePath;

    private File file;

    private List<String> lines;

    /**
     * 获取存储的文件
     *
     * @return
     */
    private File getFile() throws IOException {
        if(file == null) {
            file = FileUtil.file(filePath);
            if(!file.exists()) {
                file.createNewFile();
            }
        }
        return file;
    }

    private List<String> getLines() throws IOException {
        if(lines == null) {
            lines = FileUtil.readLines(getFile(), Charset.defaultCharset());
        }
        return lines;
    }

    @Override
    public void push(DelayedMessage delayedMessage) throws IOException {
        String data = BeanUtils.serializeBean2String(delayedMessage);
        FileUtil.appendUtf8String(data, getFile());
    }

    @Override
    public DelayedMessage pop() throws IOException {
        List<String> allLines = getLines();
        String data;
        if(allLines != null && allLines.size()>0) {
            data = allLines.get(0);
            allLines.remove(0);
            return BeanUtils.deserializeBeanFromString(data, DelayedMessage.class);
        }
        file.delete();
        return null;
    }
}
