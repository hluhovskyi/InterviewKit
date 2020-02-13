package com.hluhovskyi.interviewkit;

interface Queue<T> {

    void put(T item) throws InterruptedException;

    T take() throws InterruptedException;
}

public class BlockingQueues {

    public static <T> Queue<T> create(int capacity) {
        throw new IllegalStateException("Good luck!");
    }
}
