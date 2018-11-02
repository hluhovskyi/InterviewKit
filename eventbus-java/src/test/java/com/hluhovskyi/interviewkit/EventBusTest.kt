package com.hluhovskyi.interviewkit

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class EventBusTest {

    @Rule
    @JvmField
    val exceptionRule: ExpectedException = ExpectedException.none()

    @Test
    fun `get default, no exception`() {
        EventBus.getDefault()
    }

    @Test
    fun `get default twice, buses are same`() {
        val first = EventBus.getDefault()
        val second = EventBus.getDefault()

        assertSame(
                "Multiple instances of EventBus are created",
                first,
                second
        )
    }

    @Test
    fun `register empty subscriber, no observing methods, exception is thrown`() {
        exceptionRule.expect(Exception::class.java)

        EventBus.getDefault().register(EmptySubscriber())
    }

    @Test
    fun `register subscriber, post event, event is delivered`() {
        val subscriber = spy(Subscriber())

        EventBus.getDefault().register(subscriber)
        EventBus.getDefault().post(FooEvent)

        verify(subscriber).observe(FooEvent)
    }

    @Test
    fun `register subscriber, post different event, event is ignored`() {
        val subscriber = spy(Subscriber())

        EventBus.getDefault().register(subscriber)
        EventBus.getDefault().post(BarEvent)

        verify(subscriber, never()).observe(any())
    }

    @Test
    fun `register subscriber, unregister subscriber, event is ignored`() {
        val subscriber = spy(Subscriber())

        EventBus.getDefault().register(subscriber)
        EventBus.getDefault().unregister(subscriber)
        EventBus.getDefault().post(FooEvent)

        verify(subscriber, never()).observe(any())
    }

    @Test
    fun `register multiple subscriber, post first event, methods invoked correctly`() {
        val subscriber = spy(MultipleSubscriber())

        EventBus.getDefault().register(subscriber)
        EventBus.getDefault().post(FooEvent)

        verify(subscriber).observeFoo(FooEvent)
        verify(subscriber, never()).observeBar(any())
    }

    @Test
    fun `register multiple subscriber, post second event, methods invoked correctly`() {
        val subscriber = spy(MultipleSubscriber())

        EventBus.getDefault().register(subscriber)
        EventBus.getDefault().post(BarEvent)

        verify(subscriber).observeBar(BarEvent)
        verify(subscriber, never()).observeFoo(any())
    }

    object FooEvent
    object BarEvent

    open class EmptySubscriber

    open class Subscriber {

        @Subscribe
        open fun observe(event: FooEvent) {
        }
    }

    open class MultipleSubscriber {

        @Subscribe
        open fun observeFoo(event: FooEvent) {
        }

        @Subscribe
        open fun observeBar(event: BarEvent) {
        }
    }
}
