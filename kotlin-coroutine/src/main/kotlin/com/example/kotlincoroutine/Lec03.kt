package com.example.kotlincoroutine

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() {
//    example1()
//    example2()
//    example3()
//    example4()
//    example5()
//    example6()
//    example7()
//    example8()
}

private fun example1() {
    runBlocking {
        printWithThread("START")
        launch {
            delay(2_000L) // yield()
            printWithThread("LAUNCH END")
        }
    }

    printWithThread("END")
}

private fun example2() {
    runBlocking {
        val job = launch(start = CoroutineStart.LAZY) {
            printWithThread("Hello launch")
        }

        delay(1_000L)
        job.start()
    }
}

private fun example3() {
    runBlocking {
        val job = launch {
            (1..5).forEach {
                printWithThread(it)
                delay(500)
            }
        }

        delay(1_000L)
        job.cancel()
    }
}

private fun example4() {
    runBlocking {
        val job1 = launch {
            delay(1_000L)
            printWithThread("Job 1")
        }
        job1.join()

        val job2 = launch {
            delay(1_000L)
            printWithThread("Job 2")
        }
    }
}

private fun example5() {
    runBlocking {
        val job = async {
            3 + 5
        }
        val eight = job.await() // await: async의 결과를 가져오는 함수
        printWithThread(eight)
    }
}

private fun example6() {
    runBlocking {
        val time = measureTimeMillis {
            val job1 = async { apiCall6_1() }
            val job2 = async { apiCall6_2() }
            printWithThread(job1.await() + job2.await())
        }

        printWithThread("소요 시간: $time ms")
    }
}

suspend fun apiCall6_1(): Int {
    delay(1_000L)
    return 1
}

suspend fun apiCall6_2(): Int {
    delay(1_000L)
    return 2
}

private fun example7() {
    runBlocking {
        val time = measureTimeMillis {
            val job1 = async { apiCall7_1() }
            val job2 = async { apiCall7_2(job1.await()) }
            printWithThread(job2.await())
        }

        printWithThread("소요 시간: $time ms")
    }
}

suspend fun apiCall7_1(): Int {
    delay(1_000L)
    return 1
}

suspend fun apiCall7_2(num: Int): Int {
    delay(1_000L)
    return num + 2
}

private fun example8() {
    runBlocking {
        val time = measureTimeMillis {
            val job1 = async(start = CoroutineStart.LAZY) { apiCall8_1() }
            val job2 = async(start = CoroutineStart.LAZY) { apiCall8_2() }

            printWithThread(job1.await() + job2.await())
        }

        printWithThread("소요 시간: $time ms") // 2.0x seconds
    }
}

suspend fun apiCall8_1(): Int {
    delay(1_000L)
    return 1
}

suspend fun apiCall8_2(): Int {
    delay(1_000L)
    return 2
}

private fun example9() {
    runBlocking {
        val time = measureTimeMillis {
            val job1 = async(start = CoroutineStart.LAZY) { apiCall9_1() }
            val job2 = async(start = CoroutineStart.LAZY) { apiCall9_2() }

            job1.start()
            job2.start()

            printWithThread(job1.await() + job2.await())
        }

        printWithThread("소요 시간: $time ms") // 1.0x seconds
    }
}

suspend fun apiCall9_1(): Int {
    delay(1_000L)
    return 1
}

suspend fun apiCall9_2(): Int {
    delay(1_000L)
    return 2
}
