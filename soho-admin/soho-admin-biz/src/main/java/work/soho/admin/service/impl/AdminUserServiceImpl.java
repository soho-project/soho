package work.soho.admin.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import work.soho.admin.mapper.AdminUserMapper;
import work.soho.admin.service.AdminUserService;
import work.soho.api.admin.po.AdminUser;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl  implements AdminUserService {
    private final String TOKEN_SLAT = "@#678DFASH&%^@!#$!@#%";

    private RedisTemplate<Object, Object> redisTemplate;

    private final AdminUserMapper adminuserMapper;

    public AdminUser getById(Integer id) {
        return adminuserMapper.getById(id);
    }

    public int insert(AdminUser adminuser) {
        return adminuserMapper.insert(adminuser);
    }

    public int update(AdminUser adminuser) {
        return adminuserMapper.update(adminuser);
    }

    /**
     * 用户登陆
     *
     * 用户登陆,签发Token
     *
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) {
        //TODO 用户查找
        AdminUser user = adminuserMapper.getById(1);
        if(user == null) {
            return null;
        }
        return signToken(user);
    }

    /**
     * 获取用户签名Token
     *
     * <p>Token: sign-userId-createdTimeTs</p>
     *
     * @param adminUser
     * @return
     */
     public String signToken(AdminUser adminUser) {
        String token = adminUser.getId() + "-" + (System.currentTimeMillis());
        token = DigestUtils.md5DigestAsHex((token+TOKEN_SLAT).getBytes()) +"-"+ token;
        return token;
    }

    /**
     * 根据tokne获取通过验证的用户ID
     *
     * @param token
     * @return
     */
    public Integer getUserIdOuthToken(String token) {
        String[] tokens = token.split("-");
        if(tokens[0] != DigestUtils.md5DigestAsHex((tokens[1]+"-"+tokens[2]+TOKEN_SLAT).getBytes())) {
            return null;
        }
        return Integer.valueOf(tokens[1]);
    }

    @Override
    public AdminUser getUserByToken(String token) {
        return null;
    }

    /**
     * 签发Token
     *
     * @param password
     * @return
     */
    private String signPassword(String password) {
        return DigestUtils.md5DigestAsHex((password+TOKEN_SLAT).getBytes());
    }
}
