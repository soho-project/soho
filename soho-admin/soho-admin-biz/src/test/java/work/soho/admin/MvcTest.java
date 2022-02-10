package work.soho.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration
@WebAppConfiguration("src/main/resources")
@SpringBootTest(classes = AdminApplication.class)
class MvcTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	void testHello() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/hello")).andExpect(status().isOk())
				.andExpect(content().bytes("Hello world".getBytes())).andReturn();
	}

	@Test
	void testLogin() throws Exception {
		System.out.println("=============================================================================");
		MvcResult mvcResult = mockMvc.perform(
				post("/login").param("username", "root").param("password", "123456")
		).andExpect(status().isOk())
			.andReturn();
		System.out.println(mvcResult.getResponse().getContentAsString());
	}

}
