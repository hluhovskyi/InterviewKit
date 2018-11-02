package com.hluhovskyi.interviewkit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@interface Subscribe {
}

interface EventBus {

    void register(Object subscriber);

    void unregister(Object subscriber);

    void post(Object event);

    static EventBus getDefault() {
        throw new InstantiationError("To be implemented");
    }
}

/**
 * Class which holds all necessary information to implement subscription.
 * <p>
 * To invoke method with some event call `invoke(event)`
 */
class SubscriberMethod {

    final Class<?> eventType;
    final Object target;
    final Method method;

    SubscriberMethod(Class<?> eventType, Object target, Method method) {
        this.eventType = eventType;
        this.target = target;
        this.method = method;
    }

    void invoke(Object event) {
        try {
            method.invoke(target, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

class MethodFinder {

    /**
     * Returns list of method defined in given `target` which have only one input parameter
     * and are annotated with `annotationType` annotation.
     */
    static List<SubscriberMethod> findAnnotatedMethods(
            Object target,
            Class<? extends Annotation> annotationType
    ) {
        return Stream.of(target.getClass().getMethods())
                .filter(method -> method.getAnnotationsByType(annotationType).length == 1
                        && method.getGenericParameterTypes().length == 1
                )
                .map(method -> new SubscriberMethod(
                        method.getParameterTypes()[0],
                        target,
                        method)
                )
                .collect(Collectors.toList());
    }
}
