package work.soho.pay.biz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import work.soho.api.admin.annotation.Node;
import work.soho.common.core.result.R;
import work.soho.common.core.util.PageUtils;
import work.soho.common.core.util.StringUtils;
import work.soho.pay.biz.domain.PayInfo;
import work.soho.pay.biz.service.PayInfoService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 支付表Controller
 *
 * @author i
 * @date 2022-11-11 16:36:32
 */
@Api(tags = "客户端支付信息")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/payInfo" )
public class PayInfoController {

    private final PayInfoService payInfoService;

    /**
     * 查询支付表列表
     */
    @GetMapping("/list")
    @Node(value = "payInfo::list", name = "支付表列表")
    public R<PageSerializable<PayInfo>> list(PayInfo payInfo)
    {
        PageUtils.startPage();
        LambdaQueryWrapper<PayInfo> lqw = new LambdaQueryWrapper<PayInfo>();
        if (payInfo.getId() != null){
            lqw.eq(PayInfo::getId ,payInfo.getId());
        }
        if (StringUtils.isNotBlank(payInfo.getTitle())){
            lqw.like(PayInfo::getTitle ,payInfo.getTitle());
        }
        if (StringUtils.isNotBlank(payInfo.getName())){
            lqw.like(PayInfo::getName ,payInfo.getName());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountAppId())){
            lqw.like(PayInfo::getAccountAppId ,payInfo.getAccountAppId());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountId())){
            lqw.like(PayInfo::getAccountId ,payInfo.getAccountId());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountPrivateKey())){
            lqw.like(PayInfo::getAccountPrivateKey ,payInfo.getAccountPrivateKey());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountPublicKey())){
            lqw.like(PayInfo::getAccountPublicKey ,payInfo.getAccountPublicKey());
        }
        if (StringUtils.isNotBlank(payInfo.getAccountImg())){
            lqw.like(PayInfo::getAccountImg ,payInfo.getAccountImg());
        }
        if (StringUtils.isNotBlank(payInfo.getClientType())){
            lqw.like(PayInfo::getClientType ,payInfo.getClientType());
        }
        if (payInfo.getCreatedTime() != null){
            lqw.eq(PayInfo::getCreatedTime ,payInfo.getCreatedTime());
        }
        if (payInfo.getUpdatedTime() != null){
            lqw.eq(PayInfo::getUpdatedTime ,payInfo.getUpdatedTime());
        }
        if (StringUtils.isNotBlank(payInfo.getPlatform())){
            lqw.like(PayInfo::getPlatform ,payInfo.getPlatform());
        }
        if (payInfo.getStatus() != null){
            lqw.eq(PayInfo::getStatus ,payInfo.getStatus());
        }
        if (StringUtils.isNotBlank(payInfo.getAdapterName())){
            lqw.like(PayInfo::getAdapterName ,payInfo.getAdapterName());
        }
        List<PayInfo> list = payInfoService.list(lqw);
        return R.success(new PageSerializable<>(list));
    }

    /**
     * 获取支付表详细信息
     */
    @GetMapping(value = "/{id}" )
    @Node(value = "payInfo::getInfo", name = "支付表详细信息")
    public R<PayInfo> getInfo(@PathVariable("id" ) Long id) {
        return R.success(payInfoService.getById(id));
    }

    /**
     * 新增支付表
     */
    @PostMapping
    @Node(value = "payInfo::add", name = "支付表新增")
    public R<Boolean> add(@RequestBody PayInfo payInfo) {
       payInfo.setCreatedTime(LocalDateTime.now());
       payInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(payInfoService.save(payInfo));
    }

    /**
     * 修改支付表
     */
    @PutMapping
    @Node(value = "payInfo::edit", name = "支付表修改")
    public R<Boolean> edit(@RequestBody PayInfo payInfo) {
       payInfo.setUpdatedTime(LocalDateTime.now());
        return R.success(payInfoService.updateById(payInfo));
    }

    /**
     * 删除支付表
     */
    @DeleteMapping("/{ids}" )
    @Node(value = "payInfo::remove", name = "支付表删除")
    public R<Boolean> remove(@PathVariable Long[] ids) {
//        new BigDecimal()
        return R.success(payInfoService.removeByIds(Arrays.asList(ids)));
    }

    /**
     * 获取该表options
     *
     * @return
     */
    @GetMapping("options")
    public R<HashMap<Integer, String>> options() {
        List<PayInfo> list = payInfoService.list();
        HashMap<Integer, String> map = new HashMap<>();
        for(PayInfo item: list) {

            map.put(item.getId(), item.getTitle());
        }
        return R.success(map);
    }
}
