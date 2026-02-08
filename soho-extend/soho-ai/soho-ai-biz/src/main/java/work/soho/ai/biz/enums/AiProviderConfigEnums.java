package work.soho.ai.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AiProviderConfigEnums {

    @RequiredArgsConstructor
    @Getter
    public enum Env {
        PROD("prod","prod"),
        DEV("dev","dev"),
        TEST("test","test");
        private final String id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Provider {
        GEMINI("gemini","gemini"),
        DEEPSEEK("deepseek","deepseek"),
        OPENAI("openai","openai"),
        ANTHROPIC("anthropic","anthropic"),
        QWEN("qwen","qwen"),
        TO_BE("ollama","ollama)");
        private final String id;
        private final String name;
    }
}