package work.soho.upload.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.api.annotation.Node;
import work.soho.admin.api.request.BetweenCreatedTimeRequest;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.upload.biz.domain.UploadFile;
import work.soho.upload.biz.service.UploadFileService;

import java.util.Arrays;
import java.util.List;
/**
 * 上传文件Controller
 *
 * @author fang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/upload/admin/uploadFile" )
@Api(tags = "上传文件")
public class UploadFileController {

    private final UploadFileService uploadFileService;

    /**
     * 查询上传文件列表
     */
    @GetMapping("/list")
    @Node(value = "uploadFile::list", name = "上传文件;;列表")
    public R<PageSerializable<UploadFile>> list(UploadFile uploadFile, BetweenCreatedTimeRequest betweenCreatedTimeRequest)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<UploadFile> lqw = new LambdaQueryWrapper<UploadFile>();
        lqw.eq(uploadFile.getId() != null, UploadFile::getId ,uploadFile.getId());
        lqw.like(StringUtils.isNotBlank(uploadFile.getUrl()),UploadFile::getUrl ,uploadFile.getUrl());
        lqw.eq(uploadFile.getSize() != null, UploadFile::getSize ,uploadFile.getSize());
        lqw.eq(uploadFile.getRefCount() != null, UploadFile::getRefCount ,uploadFile.getRefCount());
        lqw.eq(uploadFile.getUpdatedTime() != null, UploadFile::getUpdatedTime ,uploadFile.getUpdatedTime());
        lqw.ge(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getStartTime() != null, UploadFile::getCreatedTime, betweenCreatedTimeRequest.getStartTime());
        lqw.lt(betweenCreatedTimeRequest!=null && betweenCreatedTimeRequest.getEndTime() != null, UploadFile::getCreatedTime, betweenCreatedTimeRequest.getEndTime());
        lqw.like(StringUtils.isNotBlank(uploadFile.getFileType()),UploadFile::getFileType ,uploadFile.getFileType());
        lqw.like(StringUtils.isNotBlank(uploadFile.getExtension()),UploadFile::getExtension ,uploadFile.getExtension());
        lqw.orderByDesc(UploadFile::getId);
        List<UploadFile> list = uploadFileService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取上传文件详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "uploadFile::getInfo", name = "上传文件;;详细信息")
    public R<UploadFile> getInfo(@PathVariable("id" ) Long id) {
        return R.success(uploadFileService.getById(id));
    }

    /**
     * 新增上传文件
     */
    @PostMapping
    @Node(value = "uploadFile::add", name = "上传文件;;新增")
    public R<Boolean> add(@RequestBody UploadFile uploadFile) {
        return R.success(uploadFileService.save(uploadFile));
    }

    /**
     * 修改上传文件
     */
    @PutMapping
    @Node(value = "uploadFile::edit", name = "上传文件;;修改")
    public R<Boolean> edit(@RequestBody UploadFile uploadFile) {
        return R.success(uploadFileService.updateById(uploadFile));
    }

    /**
     * 删除上传文件
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "uploadFile::remove", name = "上传文件;;删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
        return R.success(uploadFileService.removeByIds(Arrays.asList(ids)));
    }
}
