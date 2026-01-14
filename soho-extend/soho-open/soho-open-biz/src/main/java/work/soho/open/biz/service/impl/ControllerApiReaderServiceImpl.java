package work.soho.open.biz.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ControllerApiReaderServiceImpl {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * ✅ Controller 根包（可配置）
     *
     * application.yml:
     * soho:
     *   api-doc:
     *     base-package: work.soho.open.
     *
     * 不配则使用默认 work.soho.example.
     */
    @Value("${soho.api-doc.base-package:work.soho.example.}")
    private String basePackage;

    private final ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 可选注入：若工程中有 Jackson ObjectMapper Bean，将自动使用它输出 pretty json
     */
    @Autowired(required = false)
    private ObjectMapper objectMapper;

    enum ParamIn {
        PATH("Path"),
        QUERY("Query(GET参数)"),
        HEADER("Header"),
        BODY("Body(JSON)");

        private final String label;

        ParamIn(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    // 常见返回包装（按你们项目习惯可扩展）
    private static final Set<String> KNOWN_WRAPPER_SIMPLE_NAMES = new HashSet<>(
            Arrays.asList("Result", "ApiResult", "CommonResult", "Response", "ResponseEntity")
    );

    // Markdown 表格递归最大深度（防止循环/过深）
    private static final int MD_SCHEMA_MAX_DEPTH = 10;

    // schema 缓存：只缓存顶层（depth=0）的 schema，避免重复构建
    private final Map<String, Object> schemaCache = new ConcurrentHashMap<>();

    public Map<String, Object> getFullApiDocumentation() {
        Map<String, Object> documentation = new LinkedHashMap<>();
        documentation.put("title", "Open Platform API Documentation");
        documentation.put("timestamp", new Date());
        documentation.put("controllers", getAllControllerApis());
        return documentation;
    }

    public Map<String, List<Map<String, Object>>> getAllControllerApis() {
        Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();

        // 稳定排序：按 Controller -> Path -> MethodName
        List<Map.Entry<RequestMappingInfo, HandlerMethod>> entries = new ArrayList<>(handlerMethods.entrySet());
        entries.sort(Comparator
                .comparing((Map.Entry<RequestMappingInfo, HandlerMethod> e) -> e.getValue().getBeanType().getSimpleName())
                .thenComparing(e -> firstPath(e.getKey()))
                .thenComparing(e -> e.getValue().getMethod().getName()));

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : entries) {
            HandlerMethod handlerMethod = entry.getValue();
            Class<?> controllerClass = handlerMethod.getBeanType();

            if (controllerClass.getPackage() == null
                    || controllerClass.getPackage().getName() == null
                    || !controllerClass.getPackage().getName().startsWith(basePackage)) {
                continue;
            }

            String controllerName = controllerClass.getSimpleName();
            Map<String, Object> apiDetail = extractApiDetailWithSwagger(entry.getKey(), handlerMethod);

            result.computeIfAbsent(controllerName, k -> new ArrayList<>()).add(apiDetail);
        }
        return result;
    }

    // ===========================
    // ✅ Spring5/6 兼容的 Paths 提取
    // ===========================

    private List<String> extractPaths(RequestMappingInfo mappingInfo) {
        // Spring 5.3+ / 6: PathPatternsRequestCondition
        if (mappingInfo.getPathPatternsCondition() != null) {
            return mappingInfo.getPathPatternsCondition()
                    .getPatterns()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        // Spring 5.2-: PatternsRequestCondition
        if (mappingInfo.getPatternsCondition() != null) {
            return new ArrayList<>(mappingInfo.getPatternsCondition().getPatterns());
        }
        return Collections.emptyList();
    }

    private String firstPath(RequestMappingInfo mappingInfo) {
        List<String> paths = extractPaths(mappingInfo);
        return paths.isEmpty() ? "" : paths.get(0);
    }

    private Map<String, Object> extractApiDetailWithSwagger(RequestMappingInfo mappingInfo, HandlerMethod handlerMethod) {
        Map<String, Object> detail = new LinkedHashMap<>();
        Method method = handlerMethod.getMethod();
        Class<?> controllerClass = handlerMethod.getBeanType();

        // ✅ Controller 的 @Api 信息（类级别）
        putControllerApiInfo(detail, controllerClass);

        detail.put("methodName", method.getName());

        // 返回类型（展示用）—— ✅ 不暴露内部类名
        Type genericRet = method.getGenericReturnType();
        detail.put("returnType", getPublicTypeName(genericRet));

        // 路径 & HTTP 方法
        detail.put("paths", extractPaths(mappingInfo));

        RequestMethodsRequestCondition methods = mappingInfo.getMethodsCondition();
        List<String> httpMethods = methods == null
                ? Collections.emptyList()
                : methods.getMethods().stream().map(Enum::name).collect(Collectors.toList());
        detail.put("httpMethods", httpMethods);

        // Swagger 方法级描述
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        if (apiOperation != null) {
            detail.put("summary", safe(apiOperation.value()));
            detail.put("notes", safe(apiOperation.notes()));
        }

        // 参数（按位置分组）
        Map<ParamIn, List<Map<String, Object>>> paramsGrouped = extractParametersGrouped(method);
        detail.put("paramsGrouped", paramsGrouped);

        // 返回结构（schema）+ example
        Type unwrappedRet = unwrapKnownWrappers(genericRet);
        Object returnSchema = buildSchemaFromTypeCached(unwrappedRet, 0);
        detail.put("returnSchema", returnSchema);

        String returnExample = buildExampleJson(unwrappedRet, 0, new HashSet<>());
        detail.put("returnExample", returnExample);

        return detail;
    }

    /**
     * ✅ 将 Controller 类上的 @Api 信息写入 detail（供 markdown 里展示）
     */
    private void putControllerApiInfo(Map<String, Object> detail, Class<?> controllerClass) {
        if (controllerClass == null) return;

        Api api = controllerClass.getAnnotation(Api.class);
        if (api == null) return;

        List<String> tags = api.tags() == null ? Collections.emptyList() : Arrays.asList(api.tags());
        detail.put("controllerTags", tags);
        detail.put("controllerValue", safe(api.value()));

        // description 字段并非所有版本都有，用反射做兼容
        try {
            Method m = api.annotationType().getMethod("description");
            Object desc = m.invoke(api);
            detail.put("controllerDescription", safe(desc));
        } catch (Exception ignore) {
            // no-op
        }
    }

    // ===========================
    // ✅ 参数解析
    // ===========================

    private Map<ParamIn, List<Map<String, Object>>> extractParametersGrouped(Method method) {
        Map<ParamIn, List<Map<String, Object>>> grouped = new EnumMap<>(ParamIn.class);
        for (ParamIn in : ParamIn.values()) grouped.put(in, new ArrayList<>());

        String[] discoveredParamNames = nameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];

            // 一些框架参数（HttpServletRequest/Response/Principal 等）可以选择跳过
            if (isFrameworkParam(p.getType())) {
                continue;
            }

            ParamIn in = resolveParamIn(p);

            Map<String, Object> info = new LinkedHashMap<>();

            // ✅ 关键：Body 的“参数名”对开放平台用户通常没意义，固定为 body 防止误解
            String name;
            if (in == ParamIn.BODY) {
                name = "body";
            } else {
                name = resolveExternalParamName(p, discoveredParamNames, i);
            }

            info.put("name", name);
            info.put("in", in.label());
            info.put("required", resolveRequired(p, in));
            info.put("description", resolveDescription(p));
            info.put("type", resolveDisplayType(p, in));

            if (in == ParamIn.BODY) {
                Type bodyType = p.getParameterizedType();
                Class<?> raw = rawClassOf(bodyType);
                if (raw != null && shouldExpandBodySchema(raw)) {
                    info.put("schema", buildSchemaFromTypeCached(bodyType, 0));
                }
            }

            grouped.get(in).add(info);
        }

        // ApiImplicitParams 追加（去重）
        ApiImplicitParams implicitParams = method.getAnnotation(ApiImplicitParams.class);
        if (implicitParams != null) {
            for (ApiImplicitParam ip : implicitParams.value()) {
                Map<String, Object> ipInfo = new LinkedHashMap<>();
                ipInfo.put("name", safe(ip.name()));
                ipInfo.put("description", safe(ip.value()));
                ipInfo.put("required", ip.required());

                ParamIn in = mapImplicitParamType(ip.paramType());
                ipInfo.put("in", in.label());

                String type = safe(ip.dataType());
                if (type.isEmpty()) type = "-";
                ipInfo.put("type", type);

                grouped.get(in).add(ipInfo);
            }
        }

        for (ParamIn in : ParamIn.values()) {
            grouped.put(in, dedup(grouped.get(in)));
        }

        return grouped;
    }

    private boolean isFrameworkParam(Class<?> cls) {
        if (cls == null) return false;
        String n = cls.getName();
        // 常见框架注入参数（按需扩展）
        return n.startsWith("javax.servlet.")
                || n.startsWith("jakarta.servlet.")
                || n.startsWith("org.springframework.web.")
                || "java.security.Principal".equals(n);
    }

    private List<Map<String, Object>> dedup(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> seen = new LinkedHashMap<>();
        for (Map<String, Object> m : list) {
            String key = safe(m.get("in")) + "::" + safe(m.get("name"));
            seen.putIfAbsent(key, m);
        }
        return new ArrayList<>(seen.values());
    }

    private ParamIn resolveParamIn(Parameter param) {
        if (param.isAnnotationPresent(RequestBody.class)) return ParamIn.BODY;
        if (param.isAnnotationPresent(PathVariable.class)) return ParamIn.PATH;
        if (param.isAnnotationPresent(RequestHeader.class)) return ParamIn.HEADER;
        return ParamIn.QUERY;
    }

    private ParamIn mapImplicitParamType(String paramType) {
        if (paramType == null) return ParamIn.QUERY;
        switch (paramType.toLowerCase(Locale.ROOT)) {
            case "path":
                return ParamIn.PATH;
            case "header":
                return ParamIn.HEADER;
            case "body":
                return ParamIn.BODY;
            case "query":
            case "form":
            default:
                return ParamIn.QUERY;
        }
    }

    private String resolveExternalParamName(Parameter param, String[] discoveredNames, int idx) {
        if (param.isAnnotationPresent(RequestParam.class)) {
            RequestParam rp = param.getAnnotation(RequestParam.class);
            String n = firstNonBlank(rp.name(), rp.value());
            if (!n.isEmpty()) return n;
        }
        if (param.isAnnotationPresent(PathVariable.class)) {
            PathVariable pv = param.getAnnotation(PathVariable.class);
            String n = firstNonBlank(pv.name(), pv.value());
            if (!n.isEmpty()) return n;
        }
        if (param.isAnnotationPresent(RequestHeader.class)) {
            RequestHeader rh = param.getAnnotation(RequestHeader.class);
            String n = firstNonBlank(rh.name(), rh.value());
            if (!n.isEmpty()) return n;
        }

        ApiParam apiParam = param.getAnnotation(ApiParam.class);
        if (apiParam != null && StringUtils.hasText(apiParam.name())) {
            return apiParam.name();
        }

        if (discoveredNames != null && idx < discoveredNames.length && discoveredNames[idx] != null) {
            return discoveredNames[idx];
        }

        return param.getName();
    }

    private boolean resolveRequired(Parameter param, ParamIn in) {
        ApiParam apiParam = param.getAnnotation(ApiParam.class);
        if (apiParam != null) return apiParam.required();

        RequestBody rb = param.getAnnotation(RequestBody.class);
        if (rb != null) return rb.required();

        RequestParam rp = param.getAnnotation(RequestParam.class);
        if (rp != null) {
            // 有 defaultValue 通常不必填（但你们也可以按项目规范调整）
            String dv = rp.defaultValue();
            if (dv != null && !ValueConstants.DEFAULT_NONE.equals(dv)) {
                return false;
            }
            return rp.required();
        }

        RequestHeader rh = param.getAnnotation(RequestHeader.class);
        if (rh != null) return rh.required();

        PathVariable pv = param.getAnnotation(PathVariable.class);
        if (pv != null) return pv.required();

        return in == ParamIn.BODY;
    }

    private String resolveDescription(Parameter param) {
        ApiParam apiParam = param.getAnnotation(ApiParam.class);
        if (apiParam != null && StringUtils.hasText(apiParam.value())) return apiParam.value();
        return "-";
    }

    /**
     * ✅ 展示参数类型：对外不暴露内部类名（只展示 string/number/object/array/map 等）
     */
    private String resolveDisplayType(Parameter param, ParamIn in) {
        if (in == ParamIn.BODY) {
            return "JSON: " + getPublicTypeName(param.getParameterizedType());
        }
        return getPublicTypeName(param.getParameterizedType());
    }

    private boolean shouldExpandBodySchema(Class<?> cls) {
        if (cls == null) return false;
        String name = cls.getName();
        if (name.startsWith("org.springframework")) return false;
        if (name.startsWith("javax.servlet") || name.startsWith("jakarta.servlet")) return false;

        // LocalDateTime 等“时间/数值/JDK常用对象”不展开
        return isComplexType(cls);
    }

    // ===========================
    // ✅ 泛型变量绑定 & 解析
    // ===========================

    private Map<TypeVariable<?>, Type> buildTypeVarMap(ParameterizedType pt) {
        Map<TypeVariable<?>, Type> map = new HashMap<>();
        Type raw = pt.getRawType();
        if (!(raw instanceof Class)) return map;

        TypeVariable<?>[] vars = ((Class<?>) raw).getTypeParameters();
        Type[] args = pt.getActualTypeArguments();
        for (int i = 0; i < vars.length && i < args.length; i++) {
            map.put(vars[i], args[i]);
        }
        return map;
    }

    private Type resolveTypeVariables(Type type, Map<TypeVariable<?>, Type> typeVarMap) {
        if (type == null) return null;
        if (typeVarMap == null || typeVarMap.isEmpty()) return type;

        if (type instanceof TypeVariable) {
            Type real = typeVarMap.get(type);
            return real != null ? real : type;
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type[] args = pt.getActualTypeArguments();
            Type[] newArgs = new Type[args.length];
            for (int i = 0; i < args.length; i++) {
                newArgs[i] = resolveTypeVariables(args[i], typeVarMap);
            }

            return new ParameterizedType() {
                @Override
                public Type[] getActualTypeArguments() {
                    return newArgs;
                }

                @Override
                public Type getRawType() {
                    return pt.getRawType();
                }

                @Override
                public Type getOwnerType() {
                    return pt.getOwnerType();
                }

                @Override
                public String getTypeName() {
                    return pt.getTypeName();
                }
            };
        }

        return type;
    }

    // ===========================
    // ✅ Schema：支持泛型替换 & 递归挂子 schema
    // ===========================

    private String typeKey(Type t) {
        return t == null ? "null" : t.getTypeName();
    }

    private Object buildSchemaFromTypeCached(Type type, int depth) {
        if (type == null) return Collections.emptyList();

        // 只缓存顶层，避免 visited/depth 复杂度
        if (depth == 0) {
            String key = typeKey(type);
            return schemaCache.computeIfAbsent(key, k -> buildSchemaFromType(type, new HashSet<>(), 0));
        }
        return buildSchemaFromType(type, new HashSet<>(), depth);
    }

    private List<Map<String, Object>> buildSchema(Class<?> clazz,
                                                  Map<TypeVariable<?>, Type> typeVarMap,
                                                  Set<String> visitedTypeKeys,
                                                  int depth) {
        if (clazz == null) return Collections.emptyList();
        if (depth > MD_SCHEMA_MAX_DEPTH) return Collections.emptyList();

        List<Map<String, Object>> fields = new ArrayList<>();
        Class<?> cur = clazz;

        while (cur != null && !cur.equals(Object.class)) {
            for (Field field : cur.getDeclaredFields()) {
                if (shouldSkipField(field)) continue;

                Map<String, Object> fieldMap = new LinkedHashMap<>();
                fieldMap.put("name", field.getName());

                Type fieldType = resolveTypeVariables(field.getGenericType(), typeVarMap);

                // ✅ 不暴露内部类名：对外统一用 public type 表示
                fieldMap.put("type", getPublicTypeName(fieldType));

                ApiModelProperty anno = field.getAnnotation(ApiModelProperty.class);
                if (anno != null) {
                    fieldMap.put("description", safe(anno.value()));
                    fieldMap.put("required", anno.required());
                    fieldMap.put("example", safe(anno.example()));
                } else {
                    fieldMap.put("description", "-");
                    fieldMap.put("required", false);
                    fieldMap.put("example", "-");
                }

                Object childSchema = buildSchemaFromType(fieldType, visitedTypeKeys, depth + 1);
                if (childSchema instanceof List && !((List<?>) childSchema).isEmpty()) {
                    fieldMap.put("schema", childSchema);
                } else if (childSchema instanceof Map && !((Map<?, ?>) childSchema).isEmpty()) {
                    fieldMap.put("schema", childSchema);
                }

                fields.add(fieldMap);
            }
            cur = cur.getSuperclass();
        }

        return fields;
    }

    private boolean shouldSkipField(Field f) {
        int mod = f.getModifiers();
        if (Modifier.isStatic(mod)) return true;
        if (Modifier.isTransient(mod)) return true;
        if (f.isSynthetic()) return true;

        String n = f.getName();
        if ("serialVersionUID".equals(n)) return true;
        if (n.startsWith("this$")) return true;
        if (n.startsWith("$jacoco")) return true;

        return false;
    }

    private Object buildSchemaFromType(Type type, Set<String> visitedTypeKeys, int depth) {
        if (type == null) return Collections.emptyList();
        if (depth > MD_SCHEMA_MAX_DEPTH) return Collections.emptyList();

        String key = typeKey(type);
        if (!visitedTypeKeys.add(key)) {
            // 类型级别循环引用，直接断开
            return Collections.emptyList();
        }

        if (type instanceof Class) {
            Class<?> cls = (Class<?>) type;
            if (!isComplexType(cls)) return Collections.emptyList();
            return buildSchema(cls, Collections.emptyMap(), visitedTypeKeys, depth + 1);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();

            if (raw instanceof Class) {
                Class<?> rawCls = (Class<?>) raw;

                if (Collection.class.isAssignableFrom(rawCls)) {
                    Type itemType = pt.getActualTypeArguments().length > 0 ? pt.getActualTypeArguments()[0] : Object.class;
                    Map<String, Object> arr = new LinkedHashMap<>();
                    arr.put("type", "array");
                    arr.put("items", buildSchemaFromType(itemType, visitedTypeKeys, depth + 1));
                    return arr;
                }

                if (Map.class.isAssignableFrom(rawCls)) {
                    Type valType = pt.getActualTypeArguments().length > 1 ? pt.getActualTypeArguments()[1] : Object.class;
                    Map<String, Object> obj = new LinkedHashMap<>();
                    obj.put("type", "object");
                    obj.put("additionalProperties", buildSchemaFromType(valType, visitedTypeKeys, depth + 1));
                    return obj;
                }

                if (isComplexType(rawCls)) {
                    Map<TypeVariable<?>, Type> typeVarMap = buildTypeVarMap(pt);
                    return buildSchema(rawCls, typeVarMap, visitedTypeKeys, depth + 1);
                }
            }
        }

        return Collections.emptyList();
    }

    // ===========================
    // ✅ Example JSON 生成（优先用注入的 ObjectMapper）
    // ===========================

    private String buildExampleJson(Type type, int depth, Set<Type> visited) {
        Object exampleObj = buildExampleObject(type, depth, visited);

        try {
            if (objectMapper != null) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exampleObj);
            }
        } catch (Exception ignore) {
        }

        // fallback：尽量保持原行为
        try {
            Class<?> omCls = Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            Object om = omCls.getDeclaredConstructor().newInstance();
            Method writerWithDefaultPrettyPrinter = omCls.getMethod("writerWithDefaultPrettyPrinter");
            Object writer = writerWithDefaultPrettyPrinter.invoke(om);
            Method writeValueAsString = writer.getClass().getMethod("writeValueAsString", Object.class);
            return String.valueOf(writeValueAsString.invoke(writer, exampleObj));
        } catch (Throwable ignore) {
            return String.valueOf(exampleObj);
        }
    }

    private Object buildExampleObjectFromClass(Class<?> cls,
                                               int depth,
                                               Set<Type> visited,
                                               Map<TypeVariable<?>, Type> typeVarMap) {
        if (cls == null) return null;
        if (depth > 3) return null;

        if (cls.equals(String.class)) return "string";
        if (cls.equals(Integer.class) || cls.equals(int.class)) return 0;
        if (cls.equals(Long.class) || cls.equals(long.class)) return 0L;
        if (cls.equals(Boolean.class) || cls.equals(boolean.class)) return false;
        if (Number.class.isAssignableFrom(cls)) return 0;

        if (Date.class.isAssignableFrom(cls)) return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // java.time.* 给字符串样例，不展开结构
        if (Temporal.class.isAssignableFrom(cls)) return "2026-01-14T12:00:00";

        if (UUID.class.isAssignableFrom(cls)) return "00000000-0000-0000-0000-000000000000";
        if (cls.isEnum()) {
            Object[] cs = cls.getEnumConstants();
            return (cs != null && cs.length > 0) ? String.valueOf(cs[0]) : "ENUM";
        }

        if (!isComplexType(cls)) return cls.getSimpleName();

        Map<String, Object> obj = new LinkedHashMap<>();
        Class<?> cur = cls;

        while (cur != null && !cur.equals(Object.class)) {
            for (Field f : cur.getDeclaredFields()) {
                if (shouldSkipField(f)) continue;

                String name = f.getName();

                ApiModelProperty amp = f.getAnnotation(ApiModelProperty.class);
                if (amp != null && amp.example() != null && !amp.example().trim().isEmpty()) {
                    obj.put(name, amp.example().trim());
                    continue;
                }

                Type ft = resolveTypeVariables(f.getGenericType(), typeVarMap);
                obj.put(name, buildExampleObject(ft, depth + 1, visited));
            }
            cur = cur.getSuperclass();
        }

        return obj;
    }

    private Object buildExampleObject(Type type, int depth, Set<Type> visited) {
        if (type == null) return null;
        if (depth > 3) return null;
        if (visited.contains(type)) return null;
        visited.add(type);

        if (type instanceof Class) {
            return buildExampleObjectFromClass((Class<?>) type, depth, visited, Collections.emptyMap());
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();

            // 先解包 wrapper（如 Result<T>）
            Type unwrapped = unwrapKnownWrappers(type);
            if (unwrapped != type) {
                return buildExampleObject(unwrapped, depth + 1, visited);
            }

            if (raw instanceof Class) {
                Class<?> rawCls = (Class<?>) raw;

                if (Collection.class.isAssignableFrom(rawCls)) {
                    Type itemType = pt.getActualTypeArguments().length > 0 ? pt.getActualTypeArguments()[0] : Object.class;
                    List<Object> arr = new ArrayList<>();
                    arr.add(buildExampleObject(itemType, depth + 1, visited));
                    return arr;
                }

                if (Map.class.isAssignableFrom(rawCls)) {
                    Type valType = pt.getActualTypeArguments().length > 1 ? pt.getActualTypeArguments()[1] : Object.class;
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("key", buildExampleObject(valType, depth + 1, visited));
                    return m;
                }

                Map<TypeVariable<?>, Type> typeVarMap = buildTypeVarMap(pt);
                return buildExampleObjectFromClass(rawCls, depth + 1, visited, typeVarMap);
            }
        }

        return "object";
    }

    // ===========================
    // ✅ Wrapper 解包
    // ===========================

    private Type unwrapKnownWrappers(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();
            String rawName = (raw instanceof Class) ? ((Class<?>) raw).getSimpleName() : raw.getTypeName();

            if (KNOWN_WRAPPER_SIMPLE_NAMES.contains(rawName)) {
                Type[] args = pt.getActualTypeArguments();
                if (args != null && args.length > 0) return args[0];
            }
        }
        return type;
    }

    private Class<?> rawClassOf(Type type) {
        if (type instanceof Class) return (Class<?>) type;
        if (type instanceof ParameterizedType) {
            Type raw = ((ParameterizedType) type).getRawType();
            if (raw instanceof Class) return (Class<?>) raw;
        }
        return null;
    }

    // ===========================
    // ✅ 对外类型显示（不暴露内部类名）
    // ===========================

    /**
     * 对外展示类型名：
     * - 复杂对象统一展示为 object
     * - Collection => array<...>
     * - Map => map<string, ...>
     * - 基本类型/日期/时间 => string/number/integer/boolean 等
     */
    private String getPublicTypeName(Type type) {
        if (type == null) return "-";

        // 先解包 wrapper（Result<T> / ApiResult<T> 等）
        Type unwrapped = unwrapKnownWrappers(type);
        if (unwrapped != type) return getPublicTypeName(unwrapped);

        if (type instanceof Class) {
            return getPublicTypeNameFromClass((Class<?>) type);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();

            if (raw instanceof Class) {
                Class<?> rawCls = (Class<?>) raw;

                if (Collection.class.isAssignableFrom(rawCls)) {
                    Type itemType = pt.getActualTypeArguments().length > 0 ? pt.getActualTypeArguments()[0] : Object.class;
                    return "array<" + getPublicTypeName(itemType) + ">";
                }

                if (Map.class.isAssignableFrom(rawCls)) {
                    Type valType = pt.getActualTypeArguments().length > 1 ? pt.getActualTypeArguments()[1] : Object.class;
                    return "map<string, " + getPublicTypeName(valType) + ">";
                }

                // 其它泛型对象：对外统一 object（不暴露类名）
                if (isComplexType(rawCls)) return "object";
                return getPublicTypeNameFromClass(rawCls);
            }
        }

        // TypeVariable / WildcardType 等：保守处理
        return "object";
    }

    private String getPublicTypeNameFromClass(Class<?> cls) {
        if (cls == null) return "-";

        if (cls.isEnum()) return "string(enum)";

        if (cls == String.class || CharSequence.class.isAssignableFrom(cls)) return "string";
        if (cls == Boolean.class || cls == boolean.class) return "boolean";

        if (cls == Integer.class || cls == int.class) return "integer(int32)";
        if (cls == Long.class || cls == long.class) return "integer(int64)";
        if (cls == Short.class || cls == short.class) return "integer(int16)";
        if (cls == Byte.class || cls == byte.class) return "integer(int8)";
        if (cls == Float.class || cls == float.class) return "number(float)";
        if (cls == Double.class || cls == double.class) return "number(double)";

        if (Number.class.isAssignableFrom(cls) || cls == BigDecimal.class || cls == BigInteger.class) return "number";
        if (Date.class.isAssignableFrom(cls)) return "string(datetime)";
        if (UUID.class.isAssignableFrom(cls)) return "string(uuid)";
        if (Temporal.class.isAssignableFrom(cls) || cls == Duration.class || cls == Period.class) return "string(datetime)";

        if (cls.isArray()) return "array<" + getPublicTypeNameFromClass(cls.getComponentType()) + ">";

        if (Collection.class.isAssignableFrom(cls)) return "array<object>";
        if (Map.class.isAssignableFrom(cls)) return "map<string, object>";

        // 复杂对象统一 object
        if (isComplexType(cls)) return "object";

        // 兜底
        return "string";
    }

    // ===========================
    // ✅ 保留：内部类型格式化（如你还想调试用，可不删）
    // ===========================

    @SuppressWarnings("unused")
    private String getFormattedTypeName(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();
            String rawName = (raw instanceof Class) ? ((Class<?>) raw).getSimpleName() : raw.getTypeName();
            String args = Arrays.stream(pt.getActualTypeArguments())
                    .map(this::getFormattedTypeName)
                    .collect(Collectors.joining(", "));
            return rawName + "<" + args + ">";
        } else if (type instanceof Class) {
            return ((Class<?>) type).getSimpleName();
        }
        return type.getTypeName();
    }

    /**
     * ✅ 核心：把 LocalDateTime/LocalDate/Instant/Duration/Period/BigDecimal/BigInteger 等都当作“简单类型”
     * 这样 schema 就不会展开它们的内部字段（避免 “LocalDateTime 结构显示”）
     */
    private boolean isComplexType(Class<?> type) {
        if (type == null) return false;
        if (type.isPrimitive()) return false;
        if (type.isEnum()) return false;

        // java.lang.* 简单类型
        if (type.getName().startsWith("java.lang")) return false;

        // 数值（包含 BigDecimal/BigInteger）
        if (Number.class.isAssignableFrom(type)) return false;
        if (type == BigDecimal.class || type == BigInteger.class) return false;

        // Date / UUID
        if (Date.class.isAssignableFrom(type)) return false;
        if (UUID.class.isAssignableFrom(type)) return false;

        // java.time.* 全部当简单类型（含 LocalDateTime / LocalDate / Instant / ZonedDateTime 等）
        if (Temporal.class.isAssignableFrom(type)) return false;
        if (type == Duration.class || type == Period.class) return false;

        // 集合/Map 本身不算“对象字段”
        if (Collection.class.isAssignableFrom(type)) return false;
        if (Map.class.isAssignableFrom(type)) return false;

        // 其它：认为是复杂对象
        return true;
    }

    // ===========================
    // ✅ Markdown 输出：递归子结构全部用表格打印
    // ===========================

    public String printAsMarkdown(Map<String, List<Map<String, Object>>> controllerApis) throws JsonProcessingException {
        StringBuilder md = new StringBuilder();

        md.append("# 开放平台 API 接口文档\n\n");
        md.append("- 生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
//        md.append("- Controller 根包: `").append(safe(basePackage)).append("`\n\n");

        for (Map.Entry<String, List<Map<String, Object>>> entry : controllerApis.entrySet()) {
            String controllerName = entry.getKey();
            List<Map<String, Object>> apis = entry.getValue();
            if (apis == null || apis.isEmpty()) continue;

            // ✅ Controller @Api 信息（从第一个方法的 detail 里取一次展示）
            Map<String, Object> firstApi = apis.get(0);
            @SuppressWarnings("unchecked")
            List<String> ctrlTags = (List<String>) firstApi.getOrDefault("controllerTags", Collections.emptyList());
            String ctrlValue = safe(firstApi.get("controllerValue"));
            String ctrlDesc = safe(firstApi.get("controllerDescription"));

            md.append("## ").append(ctrlValue != null && !ctrlValue.isEmpty() ? ctrlValue : controllerName).append("\n\n");

            if ((ctrlTags != null && !ctrlTags.isEmpty()) || !ctrlValue.isEmpty() || !ctrlDesc.isEmpty()) {
                if (ctrlTags != null && !ctrlTags.isEmpty()) {
                    md.append("- Tags: ").append(String.join(", ", ctrlTags)).append("\n");
                }
                if (!ctrlDesc.isEmpty()) {
                    md.append("- 说明: ").append(ctrlDesc).append("\n");
                }
                md.append("\n");
            }

            for (int i = 0; i < apis.size(); i++) {
                Map<String, Object> api = apis.get(i);

                String summary = api.get("summary") != null && !api.get("summary").toString().isEmpty()
                        ? api.get("summary").toString()
                        : safe(api.get("methodName"));

                md.append("### ").append(i + 1).append(". ").append(summary).append("\n\n");

                List<String> paths = castList(api.get("paths"));
                List<String> methods = castList(api.get("httpMethods"));

                md.append("- 方法: `").append(methods.isEmpty() ? "-" : String.join(", ", methods)).append("`\n");
                md.append("- 路径: `").append(paths.isEmpty() ? "-" : String.join(", ", paths)).append("`\n");

                if (api.get("notes") != null && !api.get("notes").toString().isEmpty()) {
                    md.append("- 说明: ").append(api.get("notes")).append("\n");
                }
                md.append("\n");

                @SuppressWarnings("unchecked")
                Map<ParamIn, List<Map<String, Object>>> paramsGrouped =
                        (Map<ParamIn, List<Map<String, Object>>>) api.get("paramsGrouped");

                appendParamSection(md, "Path 参数", paramsGrouped.getOrDefault(ParamIn.PATH, Collections.emptyList()), false);
                appendParamSection(md, "Query 参数（GET 参数）", paramsGrouped.getOrDefault(ParamIn.QUERY, Collections.emptyList()), false);
                appendParamSection(md, "Header 参数", paramsGrouped.getOrDefault(ParamIn.HEADER, Collections.emptyList()), false);
                appendParamSection(md, "Body", paramsGrouped.getOrDefault(ParamIn.BODY, Collections.emptyList()), true);

                appendReturnSection(md, api);

                md.append("\n---\n\n");
            }
        }

        return md.toString();
    }

    private void appendParamSection(StringBuilder md, String title, List<Map<String, Object>> params, boolean showSchema) {
        if (params == null || params.isEmpty()) return;

        md.append("#### ").append(title).append("\n\n");

        // ✅ 统一先打印参数表（Body 也有）
        md.append("| 参数名 | 必填 | 类型 | 描述 |\n");
        md.append("|:------|:-----|:-----|:-----|\n");
        for (Map<String, Object> p : params) {
            md.append("| `").append(escapePipe(safe(p.get("name")))).append("`")
                    .append(" | ").append(String.valueOf(p.getOrDefault("required", false)))
                    .append(" | `").append(escapePipe(safe(p.get("type")))).append("`")
                    .append(" | ").append(escapePipe(safe(p.getOrDefault("description", "-"))))
                    .append(" |\n");
        }
        md.append("\n");

        if (!showSchema) return;

        // ✅ Body 结构递归输出
        for (Map<String, Object> p : params) {
            Object schema = p.get("schema");
            if (schema == null) continue;

            String paramName = safe(p.get("name"));
            md.append("**").append(escapePipe(paramName)).append(" 结构**\n\n");
            appendSchemaAsTables(md, schema, "Body", 0);
            md.append("\n");
        }
    }

    private void appendReturnSection(StringBuilder md, Map<String, Object> api) {
        md.append("#### 返回结果\n\n");
        md.append("- 返回类型: `").append(safe(api.get("returnType"))).append("`\n\n");

        Object schema = api.get("returnSchema");
        if (schema == null) {
            md.append("- 返回结构: `-`\n\n");
        } else {
            appendSchemaAsTables(md, schema, "Return", 0);
            md.append("\n");
        }

        String example = safe(api.get("returnExample"));
        if (!example.isEmpty() && !"null".equalsIgnoreCase(example)) {
            md.append("**Example**\n\n");
            md.append("```json\n").append(example).append("\n```\n\n");
        }
    }

    /**
     * 递归把 schema 全部用表格打印：
     * - List<Map>：对象字段列表表格，并对每个字段的 schema 继续递归
     * - Map(type=array/items)：打印 array 结构表 + 递归 items
     * - Map(type=object/additionalProperties)：打印 map 结构表 + 递归 additionalProperties
     */
    @SuppressWarnings("unchecked")
    private void appendSchemaAsTables(StringBuilder md, Object schema, String title, int depth) {
        if (schema == null) return;
        if (depth >= MD_SCHEMA_MAX_DEPTH) {
            md.append("_").append(escapePipe(title)).append(" 已达到最大递归深度_").append("\n\n");
            return;
        }

        if (schema instanceof List) {
            List<?> list = (List<?>) schema;
            if (list.isEmpty()) return;

            md.append("##### ").append(indentTitle(title, depth)).append("\n\n");

            md.append("| 字段 | 类型 | 必填 | 描述 | 示例 |\n");
            md.append("|:-----|:-----|:-----|:-----|:-----|\n");

            List<Map<String, Object>> fields = (List<Map<String, Object>>) schema;
            for (Map<String, Object> f : fields) {
                String fieldName = safe(f.get("name"));
                String fieldType = safe(f.get("type"));
                String required = String.valueOf(f.getOrDefault("required", false));
                String desc = safe(f.getOrDefault("description", "-"));
                String example = safe(f.getOrDefault("example", "-"));

                md.append("| `").append(escapePipe(fieldName)).append("`")
                        .append(" | `").append(escapePipe(fieldType)).append("`")
                        .append(" | ").append(required)
                        .append(" | ").append(escapePipe(desc))
                        .append(" | ").append(escapePipe(example))
                        .append(" |\n");
            }
            md.append("\n");

            for (Map<String, Object> f : fields) {
                Object child = f.get("schema");
                if (child == null) continue;

                String fieldName = safe(f.get("name"));
                appendSchemaAsTables(md, child, title + "." + fieldName, depth + 1);
            }

            return;
        }

        if (schema instanceof Map) {
            Map<String, Object> m = (Map<String, Object>) schema;
            if (m.isEmpty()) return;

            String type = safe(m.get("type"));
            if ("array".equalsIgnoreCase(type)) {
                md.append("##### ").append(indentTitle(title, depth)).append("\n\n");
                md.append("| 结构类型 | 说明 |\n");
                md.append("|:--------|:-----|\n");
                md.append("| `array` | 数组 |\n\n");

                Object items = m.get("items");
                if (items != null) {
                    appendSchemaAsTables(md, items, title + "[]", depth + 1);
                }
                return;
            }

            if ("object".equalsIgnoreCase(type) && m.containsKey("additionalProperties")) {
                md.append("##### ").append(indentTitle(title, depth)).append("\n\n");
                md.append("| 结构类型 | 说明 |\n");
                md.append("|:--------|:-----|\n");
                md.append("| `map` | 键值对（additionalProperties） |\n\n");

                Object ap = m.get("additionalProperties");
                if (ap != null) {
                    appendSchemaAsTables(md, ap, title + ".<value>", depth + 1);
                }
                return;
            }

            md.append("##### ").append(indentTitle(title, depth)).append("\n\n");
            md.append("| Key | Value |\n");
            md.append("|:----|:------|\n");
            for (Map.Entry<String, Object> e : m.entrySet()) {
                md.append("| `").append(escapePipe(e.getKey())).append("` | ")
                        .append(escapePipe(String.valueOf(e.getValue())))
                        .append(" |\n");
            }
            md.append("\n");
        }
    }

    private String indentTitle(String title, int depth) {
        if (depth <= 0) return title;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) sb.append("↳ ");
        sb.append(title);
        return sb.toString();
    }

    private String escapePipe(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|");
    }

    private String safe(Object o) {
        return o == null ? "" : String.valueOf(o).trim();
    }

    private String firstNonBlank(String a, String b) {
        if (a != null && !a.trim().isEmpty()) return a.trim();
        if (b != null && !b.trim().isEmpty()) return b.trim();
        return "";
    }

    @SuppressWarnings("unchecked")
    private List<String> castList(Object o) {
        if (o instanceof List) return (List<String>) o;
        return Collections.emptyList();
    }
}
