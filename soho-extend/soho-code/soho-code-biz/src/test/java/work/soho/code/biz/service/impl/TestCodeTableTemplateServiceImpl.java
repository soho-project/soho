package work.soho.code.biz.service.impl;

import cn.hutool.core.io.file.FileReader;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import work.soho.code.biz.domain.CodeTableTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 测试用测试
 */
@Primary
@Service("testCodeTableTemplateService")
public class TestCodeTableTemplateServiceImpl extends CodeTableTemplateServiceImpl {
    final static private String FILE_PATH = "/media/fang/ssd1t/home/fang/work/java/admin/soho-extend/soho-code/soho-code-biz/docs/demo/testTemp";

    public CodeTableTemplate getById(Integer id) {
        CodeTableTemplate result = super.getById(id);
        checkCache(result);
        updateById( result);
        return result;
    }
    @Override
    public CodeTableTemplate getOneById(Integer id) {
        CodeTableTemplate result = super.getById(id);
        return checkCache(result);
    }

    @Override
    public CodeTableTemplate getByName(String name) {
        return super.getByName(name);
    }

    /**
     * 将文件保存到数据库
     *
     * @param id
     * @return
     */
    public Boolean saveLocal2Db(Integer id) {
        CodeTableTemplate result = super.getById(id);
        checkCache(result);
        return updateById(result);
    }

    /**
     * 缓存替换
     *
     * @param result
     * @return
     */
    private CodeTableTemplate checkCache(CodeTableTemplate result) {
        String filePath = getFilePath(result);
        File file = new File(filePath);
        if(file.exists()) {
            //有本地测试文件
            result.setCode(getFile(file));
        } else {
            putFile(result);
        }

        return result;
    }

    private String putFile(CodeTableTemplate template) {
        String filePath = getFilePath(template);
        if(filePath != null) {
            File file = new File(filePath);
            if(file.exists()) {
                //有本地测试文件
            } else {
                //没有本地测试文件
                String content = template.getCode();
                try {
                    file.createNewFile();
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(content);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 获取文件内容
     *
     * @param file
     * @return
     */
    private String getFile(File file) {
        return FileReader.create(file).readString();
    }

    private String getFilePath(CodeTableTemplate template) {
        String path = FILE_PATH + "/" + template.getId() + "-" + template.getName() + ".groovy";
        return path;
    }
}
