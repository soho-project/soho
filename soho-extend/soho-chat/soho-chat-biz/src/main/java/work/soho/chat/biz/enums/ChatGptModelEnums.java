package work.soho.chat.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@RequiredArgsConstructor
public enum ChatGptModelEnums {
    //gpt4
    GPT_4(1, "GPT-4", 8192),
    GPT_4_0613(2, "GPT-4-0613", 8192),
    GPT_4_32K(3, "GPT-4-32k", 8192),
    GPT_4_32K_0613(4, "GPT-4-32k-0613", 32768),

    GPT_35_TURBO(5, "GPT-35 Turbo", 4096),
    GPT_35_TURBO_16K(6, "GPT-35_turbo-16k", 16384),
    GPT_35_TURBO_0613(7, "GPT-35-turbo-0613", 4096),

    //代码优化模型
    CODE_DEVINCI_002(8, "code-davinci-002", 8001),

    ;

    private final int id;
    private final String name;
    private final int maxTokenLength;
}
