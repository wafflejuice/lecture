package com.example.kotlincoroutine

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    example5()
}

private fun example1() {
    runBlocking {
        val job1 = launch {
            delay(1_000L)
            printWithThread("Job 1")
        }

        val job2 = launch {
            delay(1_000L)
            printWithThread("Job 2")
        }

        delay(100)
        job1.cancel()
    }
}

private fun example2() {
    runBlocking {
        val job1 = launch {
            delay(10L)
            printWithThread("Job 1")
        }

        delay(100)
        job1.cancel()
    }
}

private fun example3() {
    runBlocking {
        val job = launch {
            var i = 1
            var nextPrintTime = System.currentTimeMillis()
            while (i <= 5) {
                if (nextPrintTime <= System.currentTimeMillis()) {
                    printWithThread("${i++}번째 출력")
                    nextPrintTime += 1_000L
                }
            }
        }

        delay(100L)
        job.cancel()
    }
}

private fun example4() {
    runBlocking {
        val job = launch(Dispatchers.Default) {
            var i = 1
            var nextPrintTime = System.currentTimeMillis()
            while (i <= 5) {
                if (nextPrintTime <= System.currentTimeMillis()) {
                    printWithThread("${i++}번째 출력")
                    nextPrintTime += 1_000L
                }

                if (isActive.not()) {
                    throw CancellationException()
                }
            }
        }

        delay(100L)
        job.cancel()
    }
}

private fun example5() {
    runBlocking {
        val job = launch {
            try {
                delay(1_000L)
            } catch (e: CancellationException) {
                // do nothing
            }

            printWithThread("delay에 의해 취소되지 않았다.")
        }

        delay(100L)
        printWithThread("취소 시작")
        job.cancel()
    }
}
