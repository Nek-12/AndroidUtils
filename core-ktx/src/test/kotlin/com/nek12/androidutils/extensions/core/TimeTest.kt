package com.nek12.androidutils.extensions.core

import org.junit.Assert.assertThrows
import org.junit.Test

// Yes, these are some shitty tests, but they work
class TimeTest {

    private val time = Time(16, 52, 15)
    private val min = Time(0, 0)
    private val with0Seconds = Time.with(time.hour, time.minute, 0)

    @Test
    fun testTime() {
        assert(time.asString(false) == "16:52:15") { time.asString(false) }

        assert(time.asString(true) == "04:52:15 PM") { time.asString(true) }

        assert(min.asString(true, true) == "00:00:00 AM") { min.asString(true) }
        assert(min.asString(true, false) == "00:00 AM")

        assert(Time.of(time.asString(true)) == time)
        assert(Time.of(min.asString(true)) == min)

        val (h, m, s) = time
        assert(h == time.hour)
        assert(m == time.minute)
        assert(s == time.second)

        val conv = Time.fromInt(time.toInt())

        assert(conv == time) { "$time != $conv" }

        assert(with0Seconds.asString(true) == "04:52 PM")

        assert(time.totalSeconds == 60735)
        assert(time.minutesSinceMidnight == 1012.25)

        assert(time.distanceInSeconds(min) == 60735) {
            println(time.distanceInSeconds(min))
        }

        assert(Time(15, 15, 15).distanceInSeconds(Time(15, 15, 15)) == 0)
        assert(Time(22, 22).distanceInSeconds(Time(22, 23)) == 60)

        assert(time == Time.fromMillisSinceMidnight(60735000))
        assert(min == Time.fromMillisSinceMidnight(0))

        assertThrows(IndexOutOfBoundsException::class.java) {
            time[4]
        }
        assert(time[0] == time.hour)
        assert(time[1] == time.minute)
        assert(time[2] == time.second)
    }

    @Test
    fun arithmeticTests() {
        assert(time.add(26) == Time(18, 52, 15)) {
            println(time.add(26))
        }
        assert(time.add() == time)
        assert(time.add(minutes = 6) == Time(16, 58, 15))
        assert(time.add(minutes = 121) == Time(18, 53, 15)) {
            println(time.add(minutes = 121))
        }
        assert(time.add(minutes = 128) == Time(19, 0, 15)) {
            println(time.add(minutes = 128))
        }
        assert(time.add(seconds = 3600) == Time(17, 52, 15)) {
            println(time.add(seconds = 3600))
        }
        assert(time.add(seconds = 324526) == Time(11, 1, 1)) {
            println(time.add(seconds = 324526))
        }

        assert(Time.normalize(seconds = 324526) == Triple(18, 8, 46))
        assert(Time.normalize(seconds = -324526) == Triple(-18, -8, -46))

        assert(time.add(minutes = -128) == Time(14, 44, 15)) {
            println(time.add(minutes = -128))
        }

        assert(time.add(seconds = -324526) == Time(22, 44, 29)) {
            println(time.add(seconds = -324526))
        }

        assert(time - time == Time.MIN)
        assert(min - min == Time.MIN)
        assert(time - min == time)
        assert(time - with0Seconds == Time(0, 0, time.second))

        assert(time > min)
    }
}
