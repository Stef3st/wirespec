package community.flock.wirespec.js


external fun require(name: String): dynamic

val fs = require("fs")


external interface Process {
    val env: dynamic
    val argv: dynamic
}

external val process: Process
