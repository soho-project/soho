package work.soho.admin.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.annotation.Node;
import work.soho.admin.service.impl.AdminRoleServiceImpl;
import work.soho.admin.domain.AdminRole;
import work.soho.common.core.result.R;

@Api(tags = "")
@RestController
public class AdminRoleProvider {

	@Autowired
	private AdminRoleServiceImpl adminRoleService;

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
