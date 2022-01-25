package work.soho.admin.provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import work.soho.admin.service.AdminRoleService;
import work.soho.api.admin.po.AdminRole;
import work.soho.common.bean.result.R;

@Api(tags = "")
@RestController
public class AdminRoleProvider {

	@Autowired
	private AdminRoleService adminRoleService;

	@ApiOperation(value = "dmin-rule/update")
	@PostMapping("admin-rule/update")
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
