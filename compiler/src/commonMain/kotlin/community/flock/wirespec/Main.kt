package community.flock.wirespec

import community.flock.wirespec.Language.Kotlin
import community.flock.wirespec.Language.TypeScript
import community.flock.wirespec.compiler.WireSpec
import community.flock.wirespec.compiler.compile
import community.flock.wirespec.compiler.emit.KotlinEmitter
import community.flock.wirespec.compiler.emit.TypeScriptEmitter
import community.flock.wirespec.io.KotlinFile
import community.flock.wirespec.io.TypeScriptFile
import community.flock.wirespec.io.WireSpecFile
import community.flock.wirespec.utils.getFirst
import community.flock.wirespec.utils.getSecond

const val typesDir = "/types"

private enum class Language { Kotlin, TypeScript }

fun main(args: Array<String>) {

    val basePath = getFirst(args)
    val languages = getSecond(args)
        ?.split(",")
        ?.map(Language::valueOf)
        ?.toSet()
        ?: setOf(Kotlin)

    val typesPath = "$basePath$typesDir"
    val input = "$typesPath/in/input"
    val output = "$typesPath/out/output"

    languages.forEach { language ->
        when (language) {
            Kotlin -> KotlinEmitter to KotlinFile(output)
            TypeScript -> TypeScriptEmitter to TypeScriptFile(output)
        }.let { (emitter, file) ->
            WireSpecFile(input).read()
                .let(WireSpec::compile)(emitter)
                .let(file::write)
        }
    }

}
