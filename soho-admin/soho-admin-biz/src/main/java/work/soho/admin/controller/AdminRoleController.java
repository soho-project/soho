package work.soho.admin.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageSerializable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import work.soho.admin.domain.AdminRoleResource;
import work.soho.admin.domain.AdminRoleUser;
import work.soho.admin.service.AdminRoleResourceService;
import work.soho.admin.service.AdminRoleUserService;
import work.soho.admin.service.impl.AdminRoleServiceImpl;
import work.soho.admin.domain.AdminRole;
import work.soho.api.admin.vo.AdminRoleVo;
import work.soho.api.admin.vo.OptionsRoleVo;
import work.soho.common.core.result.R;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "管理员角色")
@RestController
@RequestMapping("/admin/adminRole")
@RequiredArgsConstructor
public class AdminRoleController extends BaseController{
	private final AdminRoleServiceImpl adminRoleService;
	private final AdminRoleResourceService adminRoleResourceService;
	private final AdminRoleUserService adminRoleUserService;

	/**
	 * 角色列表
	 *
	 * @param name
	 * @return
	 */
	@ApiOperation("角色列表")
	@GetMapping("list")
	public R<PageSerializable<AdminRole>> list(String name) {
		LambdaQueryWrapper<AdminRole> lqw = new LambdaQueryWrapper<>();
		if(!StringUtils.isEmpty(name)) {
			lqw.like(AdminRole::getName, name);
		}
		startPage();
		List<AdminRole> list = adminRoleService.list(lqw);
		return R.success(new PageSerializable<>(list));
	}

	/**
	 * 角色选项
	 *
	 * @param name
	 * @return
	 */
	@ApiOperation("角色选项")
	@GetMapping("option-list")
	public R<List<OptionsRoleVo>> optionList(String name) {
		LambdaQueryWrapper<AdminRole> lqw = new LambdaQueryWrapper<>();
		if(!StringUtils.isEmpty(name)) {
			lqw.like(AdminRole::getName, name);
		}
		List<AdminRole> list = adminRoleService.list(lqw);
		List<OptionsRoleVo> optionsRoleVoList = list.stream().map(item-> new OptionsRoleVo().setId(item.getId()).setName(item.getName()))
				.collect(Collectors.toList());
		return R.success(optionsRoleVoList);
	}

	@ApiOperation("获取角色选中的资源")
	@GetMapping("/roleResources")
	public R<List<String>> getRoleResourceIds(Long id) {
		List<AdminRoleResource> list = adminRoleResourceService.list(new LambdaQueryWrapper<AdminRoleResource>().eq(AdminRoleResource::getRoleId, id));
		List<String> ids = list.stream().map(item->String.valueOf(item.getResourceId())).collect(Collectors.toList());
		return R.success(ids);
	}

	@ApiOperation("更新用户角色")
	@PutMapping("")
	public R<Boolean> update(@RequestBody AdminRoleVo adminRoleVo) {
		AdminRole adminRole = new AdminRole();
		BeanUtils.copyProperties(adminRoleVo, adminRole);
		adminRoleService.updateById(adminRole);
		LambdaQueryWrapper<AdminRoleResource> delLqw = new LambdaQueryWrapper<>();
		delLqw.eq(AdminRoleResource::getRoleId, adminRole.getId());

		//获取原有资源ID
		List<Long> oldResourceIds = adminRoleResourceService.list(new LambdaQueryWrapper<AdminRoleResource>()
				.eq(AdminRoleResource::getRoleId, adminRole.getId())).stream().map(AdminRoleResource::getResourceId)
				.collect(Collectors.toList());

		//新增关联资源
		for (Long rid: CollUtil.subtract(adminRoleVo.getResourceIds(), oldResourceIds)) {
			AdminRoleResource adminRoleResource = new AdminRoleResource();
			adminRoleResource.setResourceId(rid);
			adminRoleResource.setRoleId(adminRole.getId());
			adminRoleResource.setCreatedTime(new Date());
			adminRoleResourceService.save(adminRoleResource);
		}
		//获取需要删除的ID
		List<Long> delIds = (List<Long>) CollUtil.subtract(oldResourceIds, adminRoleVo.getResourceIds());
		if(CollUtil.isNotEmpty(delIds)) {
			delLqw.in(AdminRoleResource::getResourceId, delIds);
			adminRoleResourceService.remove(delLqw);
		}
		return R.success();
	}

	@ApiOperation("新增角色")
	@PostMapping("")
	public R<Boolean> insert(@RequestBody AdminRoleVo adminRoleVo) {
		AdminRole adminRole = new AdminRole();
		BeanUtils.copyProperties(adminRoleVo, adminRole);
		adminRole.setCreatedTime(new Date());
		adminRoleService.save(adminRole);

		for (Long rid: adminRoleVo.getResourceIds()) {
			AdminRoleResource adminRoleResource = new AdminRoleResource();
			adminRoleResource.setResourceId(rid);
			adminRoleResource.setRoleId(adminRole.getId());
			adminRoleResource.setCreatedTime(new Date());
			adminRoleResourceService.save(adminRoleResource);
		}
		return R.success();
	}

	@ApiOperation("更新角色资源")
	@DeleteMapping("/{ids}")
	public R<Boolean> delete(@PathVariable("ids") Long[] ids) {
		//检查是否有用户使用到该角色
		LambdaQueryWrapper<AdminRoleUser> lqw = new LambdaQueryWrapper<>();
		lqw.in(AdminRoleUser::getRoleId, ids);
		List<AdminRoleUser> list = adminRoleUserService.list(lqw);
		if(list!=null && !list.isEmpty()) {
			return R.error("角色还有用户在使用， 请先取消角色关联");
		}
		//删除角色资源关联
		LambdaQueryWrapper<AdminRoleResource> adminRoleResourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
		adminRoleResourceService.remove(adminRoleResourceLambdaQueryWrapper);
		//删除指定角色
		adminRoleService.removeBatchByIds(Arrays.asList(ids));

		return R.success();
	}

}
