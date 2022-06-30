package community.flock.wirespec.compiler.emit.common

import community.flock.wirespec.compiler.parse.AST
import community.flock.wirespec.compiler.parse.Node
import community.flock.wirespec.compiler.parse.Type
import community.flock.wirespec.utils.log

abstract class Emitter : TypeDefinitionEmitter {

    fun emit(ast: AST): String = ast
        .map { it.emit() }
        .takeIf { it.isNotEmpty() }
        ?.reduce { acc, cur -> acc + cur } ?: ""

    private fun Node.emit(): String = run {
        log("Emitting Node $this")
        when (this) {
            is Type -> emit()
        }
    }

    companion object {
        protected const val SPACER = "  "
    }

}
