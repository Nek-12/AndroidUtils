package com.nek12.androidutils.extensions.core

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class TimeTest : FreeSpec({

    "with time" - {
        val time = Time(16, 52, 15)
        val min = Time(0, 0)
        val with0Seconds = Time.with(time.hour, time.minute, 0)

        "should be convertible" {
            time.asString(false) shouldBe "16:52:15"

            time.asString(true) shouldBe "04:52:15 PM"

            min.asString(true, addSecondsIfZero = true) shouldBe "00:00:00 AM"

            min.asString(true, addSecondsIfZero = false) shouldBe "00:00 AM"

            Time.of(time.asString(true)) shouldBe time
            Time.of(min.asString(true)) shouldBe min

            with0Seconds.asString(true) shouldBe "04:52 PM"
        }

        "should be destructurable" {
            val (h, m, s) = time
            h shouldBe time.hour
            m shouldBe time.minute
            s shouldBe time.second
        }

        "should convert to int and back" {

            val conv = Time.fromInt(time.toInt())

            conv shouldBe time
        }

        "total seconds and minutes should match expected" {
            time.totalSeconds shouldBe 60735
            time.totalMinutes shouldBe 1012.25
        }

        "distance in seconds should match expected" {
            time.distanceInSeconds(min) shouldBe 60735
            Time(15, 15, 15).distanceInSeconds(Time(15, 15, 15)) shouldBe 0
            Time(22, 22).distanceInSeconds(Time(22, 23)) shouldBe 60
        }

        "should be convertible from millis and seconds" {
            time shouldBe Time.fromMillisSinceMidnight(60_735_000)
            min shouldBe Time.fromMillisSinceMidnight(0)
        }

        "should be indexable" {
            time[0] shouldBe time.hour
            time[1] shouldBe time.minute
            time[2] shouldBe time.second

            shouldThrowExactly<IndexOutOfBoundsException> {
                time[4]
            }
        }

        "and arithmetic operators" - {
            "negative additon as expected" {

                time.add(minutes = -128) shouldBe Time(14, 44, 15)

                time.add(seconds = -324526) shouldBe Time(22, 44, 29)
            }

            "positive addition as expected" {
                time.add(26) shouldBe Time(18, 52, 15)
                time.add() shouldBe time
                time.add(minutes = 6) shouldBe Time(16, 58, 15)
                time.add(minutes = 121) shouldBe Time(18, 53, 15)
                time.add(minutes = 128) shouldBe Time(19, 0, 15)
                time.add(seconds = 3600) shouldBe Time(17, 52, 15)
                time.add(seconds = 324526) shouldBe Time(11, 1, 1)
            }
            "subtraction as expected" {
                time - time shouldBe Time.MIN
                min - min shouldBe Time.MIN
                time - min shouldBe time
                time - with0Seconds shouldBe Time(0, 0, time.second)
            }
        }

        "normalization as expected" {
            Time.normalize(seconds = 324526) shouldBe Triple(18, 8, 46)
            Time.normalize(seconds = -324526) shouldBe Triple(-18, -8, -46)
        }

        "comparison and equality as expected" {
            time shouldBeGreaterThan min
        }
    }
})
