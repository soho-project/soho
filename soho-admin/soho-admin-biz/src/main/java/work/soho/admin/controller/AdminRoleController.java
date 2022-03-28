package work.soho.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.annotation.Node;
import work.soho.admin.service.impl.AdminRoleServiceImpl;
import work.soho.admin.domain.AdminRole;
import work.soho.api.admin.result.AdminPage;
import work.soho.common.core.result.R;

import java.util.List;

@Api(tags = "")
@RestController
@RequestMapping("/admin/adminRole")
public class AdminRoleController extends BaseController{

	@Autowired
	private AdminRoleServiceImpl adminRoleService;

	/**
	 * 角色列表
	 *
	 * @param name
	 * @return
	 */
	@GetMapping("list")
	public AdminPage<AdminRole> list(String name) {
		LambdaQueryWrapper<AdminRole> lqw = new LambdaQueryWrapper<>();
		if(!StringUtils.isEmpty(name)) {
			lqw.like(AdminRole::getName, name);
		}
		startPage();
		List<AdminRole> list = adminRoleService.list(lqw);
		return new AdminPage<AdminRole>().setData(list).setTotal(((Page<AdminRole>)list).getTotal());
	}

	@ApiOperation(value = "dmin-rule/update")
	@PostMapping("admin-rule/update")
	@Node(value = "test by fang annotation", name = "更新角色信息", visible = 1, describe = "")
	public R<Boolean> update(AdminRole adminRole) {
		adminRoleService.update(adminRole);
		return R.ok();
	}

	@ApiOperation(value = "dmin-rule/update")
	@PostMapping("admin-rule/insert")
	public R<Boolean> insert(AdminRole adminRole) {
		adminRoleService.insert(adminRole);
		return R.ok();
	}

	@PostMapping("admin-rule/update-resource")
	public R<Boolean> updateResource() {

		return R.ok();
	}

}
