package work.soho.common.core.result;

import lombok.SneakyThrows;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import work.soho.common.core.annotation.ErrorCodeGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ErrorCodeCollector {

    /**
     * 扫描获取所有的错误码
     *
     * @param basePackages
     * @return
     */
    public static Map<Integer, String> collect(String... basePackages) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ErrorCodeGroup.class));

        Map<Integer, String> map = new HashMap<>();
        for (String pkg : basePackages) {
            for (var bd : scanner.findCandidateComponents(pkg)) {
                try {
                    Class<?> clazz = Class.forName(bd.getBeanClassName());
                    if (!clazz.isEnum()) continue;
                    for (Object constant : clazz.getEnumConstants()) {
                        ErrorCode ec = (ErrorCode) constant; // 确保 enum implements ErrorCode
                        if (map.putIfAbsent(ec.code(), ec.message()) != null) {
                            throw new IllegalStateException("duplicate error code: " + ec.code());
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Map.copyOf(map);
    }

    /**
     * 扫描获取所有的错误码
     *
     * @param basePackages
     * @return
     */
    @SneakyThrows
    public static List<ErrorCode> collectErrorCodeList(String... basePackages) {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ErrorCodeGroup.class));

        LinkedList<ErrorCode> list = new LinkedList<>();
        for (String pkg : basePackages) {
            for (var bd : scanner.findCandidateComponents(pkg)) {
                try {
                    Class<?> clazz = Class.forName(bd.getBeanClassName());
                    if (!clazz.isEnum()) continue;
                    for (Object constant : clazz.getEnumConstants()) {
                        ErrorCode ec = (ErrorCode) constant; // 确保 enum implements ErrorCode
                        list.add(ec);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return list;
    }
}
