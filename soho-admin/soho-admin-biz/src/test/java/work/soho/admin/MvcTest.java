package work.soho.admin;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import work.soho.test.TestApp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MVC 接口测试。
 */
@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("local")
@Log4j2
class MvcTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	/**
	 * 基础路由测试。
	 *
	 * @throws Exception 请求异常
	 */
	@Test
	void testHello() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/hello")).andExpect(status().is(404))
				.andReturn();
	}

	/**
	 * 登录配置接口测试。
	 *
	 * @throws Exception 请求异常
	 */
	@Test
	void testLogin() throws Exception {
		System.out.println("=============================================================================");
		MvcResult mvcResult = mockMvc.perform(
				get("/admin/guest/auth/login/config").param("username", "admin")
		).andExpect(status().isOk())
			.andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

}
