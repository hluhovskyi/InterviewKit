package com.hluhovskyi.interviewkit;

@interface Subscribe {
}

interface EventBus {

    void register(Object subscriber);

    void unregister(Object subscriber);

    void post(Object event);

    static EventBus getInstance() {
        throw new InstantiationError("To be implemented");
    }
}
