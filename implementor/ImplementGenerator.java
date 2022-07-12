package info.kgeorgiy.ja.smaglii.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ImplementGenerator(Class<?> token) {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String SUFFIX_IMPL = "Impl";
    private final static String EMPTY = "";
    private final static String OPEN_BODY = "{";
    private final static String CLOSE_BODY = "}";
    private final static String SEMICOLON = ";";
    private static final String OPEN_CIRCLE_BRACKET = "(";
    private static final String CLOSE_CIRCLE_BRACKET = ")";
    private static final String THROWS = "throws";
    private static final String IMPLEMENTS = "implements";
    private static final String EXTENDS = "extends";
    private static final String SUPER = "super";
    private static final String SPACE = " ";
    private static final String TAB = "\t";
    private static final String RETURN = "return";
    private static final String PACKAGE = "package";
    private static final String CLASS = "class";
    private static final String PUBLIC = "public";
    private static final String DEFAULT_BOOLEAN = "false";
    private static final String NULL = "null";
    private static final String ZERO = "0";
    private static final String SEQ_SEPARATOR = ", ";

    // :NOTE: it is better to put static methods above others in a class
    private static <T> String getDefaultValue(Class<T> clazz) {
        if (clazz.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (!clazz.isPrimitive()) {
            return NULL;
        } else {
            return ZERO;
        }
    }

    public String generate() throws ImplerException {
        return String.join(LINE_SEPARATOR,
                getPackage(),
                getClassDeclaration(),
                getConstructors(),
                getMethods(),
                CLOSE_BODY
        );
    }

    public static String getClassName(Class<?> token) {
        return token.getSimpleName() + SUFFIX_IMPL;
    }

    private String getPackage() {
        // :NOTE: token.getPackage to variable
        final Package tokenPackage = token.getPackage();
        return Objects.isNull(tokenPackage) ? EMPTY :
                PACKAGE + SPACE + tokenPackage.getName() + SEMICOLON + LINE_SEPARATOR;
    }

    private String getClassDeclaration() {
        return String.join(SPACE,
                PUBLIC,
                CLASS,
                getClassName(token),
                token.isInterface() ? IMPLEMENTS : EXTENDS,
                token.getCanonicalName(),
                OPEN_BODY);
    }

    private String getConstructors() throws ImplerException {
        var constructors = Arrays.stream(token.getDeclaredConstructors())
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .map(constructor -> getExecutable(getExecutableDeclaration(constructor,
                        getClassName(token)), getSuper(constructor)))
                .reduce(EMPTY, (a, b) -> a + b);
        if (constructors.isEmpty() && !token.isInterface()) {
            throw new ImplerException("must be one or more non private constructors");
        }
        return constructors;
    }


    private Set<MethodProxy> getProxySet(Method[] methods) {
        return Arrays.stream(methods)
                .map(MethodProxy::new)
                .collect(Collectors.toSet());
    }

    private Set<MethodProxy> getSetMethods(Class<?> token) {
        // :NOTE: interface in the left part
        Set<MethodProxy> ans = new HashSet<>();
        while (token != null) {
            ans.addAll(getProxySet(token.getMethods()));
            ans.addAll(getProxySet(token.getDeclaredMethods()));
            token = token.getSuperclass();
        }
        return ans;
    }


    private String getMethods() {
        return getSetMethods(token).stream()
                .map(e -> e.method)
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .map(method -> getExecutable(getExecutableDeclaration(method,
                        method.getReturnType().getCanonicalName() + SPACE + method.getName()), getReturn(method)))
                .reduce(EMPTY, (a, b) -> a + LINE_SEPARATOR + b);
    }

    private String getExecutable(String declaration, String body) {
        return TAB + String.join(LINE_SEPARATOR + TAB,
                declaration,
                TAB + body,
                CLOSE_BODY
        ) + LINE_SEPARATOR;
    }

    private String getExecutableDeclaration(Executable executable, String name) {
        return String.join(SPACE,
                PUBLIC,
                name + OPEN_CIRCLE_BRACKET + getSeq(executable.getParameters(),
                        parameter -> parameter.getType().getCanonicalName() + SPACE + parameter.getName()) +
                        CLOSE_CIRCLE_BRACKET,
                getThrows(executable),
                OPEN_BODY
        );
    }


    private <T> String getSeq(T[] arr, Function<T, String> mapping) {
        return Arrays.stream(arr)
                .map(mapping)
                .collect(Collectors.joining(SEQ_SEPARATOR));
    }


    private String getThrows(Executable executable) {
        Class<?>[] exceptions = executable.getExceptionTypes();
        // :NOTE: brackets
        if (exceptions.length == 0) {
            return EMPTY;
        }
        return THROWS + SPACE + getSeq(executable.getExceptionTypes(), Class::getName) + SPACE;
    }

    private String getReturn(Method method) {
        var type = method.getReturnType();
        return type.equals(void.class) ? EMPTY : RETURN + SPACE + getDefaultValue(type) + SEMICOLON;
    }

    private String getSuper(Constructor<?> constructor) {
        return SUPER + OPEN_CIRCLE_BRACKET + getSeq(constructor.getParameters(), Parameter::getName) +
                CLOSE_CIRCLE_BRACKET + SEMICOLON;
    }

    private record MethodProxy(Method method) {
        @Override
        public boolean equals(Object o) {
            // :NOTE: formatting
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MethodProxy that = (MethodProxy) o;
            return method.getName().equals(that.method.getName()) &&
                    Arrays.equals(method.getParameterTypes(), that.method.getParameterTypes());
        }

        @Override
        public int hashCode() {
            return Objects.hash(method.getName(), Arrays.hashCode(method.getParameterTypes()));
        }
    }

}
