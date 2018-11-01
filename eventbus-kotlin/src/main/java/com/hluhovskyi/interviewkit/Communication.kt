package com.hluhovskyi.interviewkit

interface Action
object FooAction : Action
object BarAction : Action

interface Result
object FooResult : Result
object BarResult : Result

private const val JOBS_TO_COMPLETE = 6
private var JOBS_DONE = 0

class FooComponent(

) {

    private fun notifyWithResult(result: Result) {

    }

    fun doJob(action: Action) {
        if (!shouldDoJob()) return

        if (action is BarAction) {
            notifyWithResult(doBar())
        }
    }

    private fun doBar(): BarResult {
        Thread.sleep(1000L)
        return BarResult
    }
}

class BarComponent(

) {

    private fun notifyWithResult(result: Result) {

    }

    fun doJob(action: Action) {
        if (!shouldDoJob()) return

        if (action is FooAction) {
            notifyWithResult(doFoo())
        }
    }

    private fun doFoo(): FooResult {
        Thread.sleep(700L)
        return FooResult
    }
}

private fun shouldDoJob(): Boolean {
    JOBS_DONE++
    return JOBS_DONE <= JOBS_TO_COMPLETE
}