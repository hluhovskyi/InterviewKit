package com.hluhovskyi.interviewkit

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertSame
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.random.Random

private val RANDOM_SEED = Random(228)

class BlockingQueuesTest {

    @Test
    fun create_noCrash() {
        BlockingQueues.create<Any>(10)
    }

    @Test
    fun putItems_takeItems_itemsReturnedInCorrectOrder() {
        val item1 = Any()
        val item2 = Any()

        val queue = BlockingQueues.create<Any>(2)
        queue.put(item1)
        queue.put(item2)

        assertSame(item1, queue.take())
        assertSame(item2, queue.take())
    }

    @Test
    fun putMoreItems_threadIsSuspended() {
        val pool = Executors.newFixedThreadPool(4)
        val queue = BlockingQueues.create<Any>(2)

        val results = (0 until 4)
                .map {
                    pool.submit<String> {
                        queue.put(Any())
                        "Put"
                    }
                }
                .map { future -> future.checkIsFinished() }

        results.assertState(finished = 2, unfinished = 2)
    }

    @Test
    fun takeMoreItems_threadsAreSuspended() {
        val pool = Executors.newFixedThreadPool(4)

        val queue = BlockingQueues.create<Any>(2)
        queue.put(Any())
        queue.put(Any())

        val results = (0 until 4)
                .map {
                    pool.submit<String> {
                        queue.take()
                        "Take"
                    }
                }
                .map { future -> future.checkIsFinished() }

        results.assertState(finished = 2, unfinished = 2)
    }

    @Test
    fun putAndTake_threadsAreManagedCorrectly() {
        val producerPool = Executors.newFixedThreadPool(4)
        val consumerPool = Executors.newFixedThreadPool(4)

        val queue = BlockingQueues.create<Any>(2)

        val producerFutures = (0 until 4)
                .map {
                    producerPool.submit<String> {
                        queue.put(Any())
                        "Put"
                    }
                }
        val initialProducerResults = producerFutures
                .map { future -> future.checkIsFinished() }

        initialProducerResults.assertState(finished = 2, unfinished = 2)

        val consumerResults = (0 until 8)
                .map {
                    consumerPool.submit<String> {
                        queue.take()
                        "Take"
                    }
                }
                .map { future -> future.checkIsFinished() }
        val producerResults = producerFutures
                .map { future -> future.checkIsFinished() }

        // We consumed all 4 previously pushed items, so all produced threads were finished.
        // 4 consume request were blocked, since there were no new items.
        consumerResults.assertState(finished = 4, unfinished = 4)
        producerResults.assertState(finished = 4, unfinished = 0)

        producerPool.shutdown()
        consumerPool.shutdown()
    }

    @Test
    fun takeAndPut_threadsAreManagedCorrectly() {
        val producerPool = Executors.newFixedThreadPool(4)
        val consumerPool = Executors.newFixedThreadPool(4)

        val queue = BlockingQueues.create<Any>(2)

        val consumerFutures = (0 until 4)
                .map {
                    consumerPool.submit<String> {
                        queue.take()
                        "Take"
                    }
                }
        val initialConsumerResults = consumerFutures
                .map { future -> future.checkIsFinished() }

        initialConsumerResults.assertState(finished = 0, unfinished = 4)

        val producerResults = (0 until 8)
                .map {
                    producerPool.submit<String> {
                        queue.put(Any())
                        "Put"
                    }
                }
                .map { future -> future.checkIsFinished() }
        val consumerResults = consumerFutures
                .map { future -> future.checkIsFinished() }

        // Last producer operation pushed 8 items.
        // 4 items were consumed because of inital consumer request,
        // 2 items were put to queue without blocking
        // 2 items weren't put to queue and its threads were blocked.
        producerResults.assertState(finished = 6, unfinished = 2)
        consumerResults.assertState(finished = 4, unfinished = 0)

        producerPool.shutdown()
        consumerPool.shutdown()
    }

    @Test
    fun raceCondition() {
        val pool = Executors.newFixedThreadPool(128)

        val queue = BlockingQueues.create<Any>(2)

        repeat(2000) {
            val results = (0 until 20)
                    .map {
                        pool.submit<String> {
                            if (it % 2 == 0) {
                                queue.take()
                                "Take"
                            } else {
                                queue.put(Any())
                                "Put"
                            }
                        }
                    }.map { it.checkIsFinished() }

            results.assertState(finished = 20, unfinished = 0)
        }
    }

    @Test
    fun raceCondition_shuffled() {
        val pool = Executors.newFixedThreadPool(128)

        val queue = BlockingQueues.create<Any>(1)

        repeat(100) {
            val results = (0 until 100)
                    .shuffled(RANDOM_SEED)
                    .map {
                        pool.submit<String> {
                            if (it % 2 == 0) {
                                queue.take()
                                queue.take()
                                "Take"
                            } else {
                                queue.put(Any())
                                queue.put(Any())
                                "Put"
                            }
                        }
                    }.map { it.checkIsFinished() }

            results.assertState(finished = 100, unfinished = 0)
        }
    }
}

private fun Future<String>.checkIsFinished(): Result = try {
    Result(get(200, TimeUnit.MILLISECONDS), true)
} catch (e: TimeoutException) {
    Result("", false)
}

private data class Result(val operation: String, val finished: Boolean)

private fun List<Result>.assertState(finished: Int, unfinished: Int) {
    assertThat(
            "Count of finished threads differs\n" +
                    "count of Queue#put ${count { it.operation == "Put" }}\n" +
                    "count of Queue#take ${count { it.operation == "Take" }}",
            count { it.finished },
            equalTo(finished)
    )
    assertThat(
            "Count of unfinished threads differs\n" +
                    "count of Queue#put ${count { it.operation == "Put" }}\n" +
                    "count of Queue#take ${count { it.operation == "Take" }}",
            count { !it.finished },
            equalTo(unfinished)
    )
}
