package work.soho.user.cloud.bridge.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import work.soho.user.api.dto.UserInfoDto;

@FeignClient(name = "soho-user-cloud-biz", contextId = "soho-user-cloud-biz-1")
public interface UserApiFeign {
    @GetMapping("/cloud/user/user/{id}")
    UserInfoDto getUserById(@PathVariable("id") Long id);
}
