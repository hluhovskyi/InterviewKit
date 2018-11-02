package com.hluhovskyi.interviewkit;

public class Example {

    static class Event {
    }

    static class Subscriber {

        @Subscribe
        public void observe(Event event) {
            System.out.println("Yay, event!");
        }
    }

    public static void main(String[] args) {
        Subscriber subscriber = new Subscriber();

        EventBus.getDefault().register(subscriber);

        // should print message
        EventBus.getDefault().post(new Event());

        EventBus.getDefault().unregister(subscriber);

        // should not print message
        EventBus.getDefault().post(new Event());
    }
}
