package com.nek12.androidutils.extensions.core

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FreeSpec

// todo: migrate to kotest assertions
class TimeTest : FreeSpec({

    "with time" - {
        val time = Time(16, 52, 15)
        val min = Time(0, 0)
        val with0Seconds = Time.with(time.hour, time.minute, 0)

        "should be convertible" {
            assert(time.asString(false) == "16:52:15") { time.asString(false) }

            assert(time.asString(true) == "04:52:15 PM") { time.asString(true) }

            assert(min.asString(true, addSecondsIfZero = true) == "00:00:00 AM") {
                min.asString(true)
            }

            assert(min.asString(true, addSecondsIfZero = false) == "00:00 AM")

            assert(Time.of(time.asString(true)) == time)
            assert(Time.of(min.asString(true)) == min)

            assert(with0Seconds.asString(true) == "04:52 PM")
        }

        "should be destructurable" {
            val (h, m, s) = time
            assert(h == time.hour)
            assert(m == time.minute)
            assert(s == time.second)
        }

        "should convert to int and back" {

            val conv = Time.fromInt(time.toInt())

            assert(conv == time) { "$time != $conv" }
        }

        "total seconds and minutes should match expected" {
            assert(time.totalSeconds == 60735)
            assert(time.totalMinutes == 1012.25)
        }

        "distance in seconds should match expected" {
            assert(time.distanceInSeconds(min) == 60735)
            assert(Time(15, 15, 15).distanceInSeconds(Time(15, 15, 15)) == 0)
            assert(Time(22, 22).distanceInSeconds(Time(22, 23)) == 60)
        }

        "should be convertible from millis and seconds" {
            assert(time == Time.fromMillisSinceMidnight(60735000))
            assert(min == Time.fromMillisSinceMidnight(0))
        }

        "should be indexable" {
            assert(time[0] == time.hour)
            assert(time[1] == time.minute)
            assert(time[2] == time.second)

            shouldThrowExactly<IndexOutOfBoundsException> {
                time[4]
            }
        }

        "and arithmetic operators" - {
            "negative additon as expected" {

                assert(time.add(minutes = -128) == Time(14, 44, 15)) {
                    println(time.add(minutes = -128))
                }

                assert(time.add(seconds = -324526) == Time(22, 44, 29)) {
                    println(time.add(seconds = -324526))
                }
            }

            "positive addition as expected" {
                assert(time.add(26) == Time(18, 52, 15)) { time.add(26) }
                assert(time.add() == time)
                assert(time.add(minutes = 6) == Time(16, 58, 15))
                assert(time.add(minutes = 121) == Time(18, 53, 15)) {
                    time.add(minutes = 121)
                }
                assert(time.add(minutes = 128) == Time(19, 0, 15)) {
                    time.add(minutes = 128)
                }
                assert(time.add(seconds = 3600) == Time(17, 52, 15)) {
                    time.add(seconds = 3600)
                }
                assert(time.add(seconds = 324526) == Time(11, 1, 1)) {
                    time.add(seconds = 324526)
                }
            }
            "subtraction as expected" {
                assert(time - time == Time.MIN)
                assert(min - min == Time.MIN)
                assert(time - min == time)
                assert(time - with0Seconds == Time(0, 0, time.second))
            }
        }

        "normalization as expected" {
            assert(Time.normalize(seconds = 324526) == Triple(18, 8, 46))
            assert(Time.normalize(seconds = -324526) == Triple(-18, -8, -46))
        }

        "comparison and equality as expected" {
            assert(time > min)
        }
    }
})
