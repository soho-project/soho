package work.soho.ai.biz.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import work.soho.ai.biz.domain.AiProviderConfig;
import work.soho.common.core.util.JacksonUtils;
import work.soho.common.core.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AiProviderModelUtils {
    private AiProviderModelUtils() {
    }

    public static List<String> extractModels(AiProviderConfig providerConfig) {
        Set<String> models = new LinkedHashSet<>();
        if (providerConfig == null) {
            return new ArrayList<>();
        }

        addModel(models, providerConfig.getDefaultModel());
        addModels(models, providerConfig.getSupportedModels());

        if (StringUtils.isNotBlank(providerConfig.getConfigJson())) {
            Map<String, Object> config = JacksonUtils.toBean(providerConfig.getConfigJson(), new TypeReference<Map<String, Object>>() {});
            if (config != null) {
                Object value = config.get("models");
                if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        if (item != null) {
                            addModel(models, item.toString());
                        }
                    }
                }
                Object model = config.get("model");
                if (model != null) {
                    addModel(models, model.toString());
                }
            }
        }
        return new ArrayList<>(models);
    }

    private static void addModels(Set<String> models, String raw) {
        if (StringUtils.isBlank(raw)) {
            return;
        }
        String trimmed = raw.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                List<String> list = JacksonUtils.toBean(trimmed, new TypeReference<List<String>>() {});
                if (list != null) {
                    for (String item : list) {
                        addModel(models, item);
                    }
                    return;
                }
            } catch (Exception ignore) {
            }
        }

        String normalized = trimmed.replace("\r", "\n").replace(",", "\n");
        for (String item : normalized.split("\n")) {
            addModel(models, item);
        }
    }

    private static void addModel(Set<String> models, String model) {
        if (StringUtils.isNotBlank(model)) {
            models.add(model.trim());
        }
    }
}
