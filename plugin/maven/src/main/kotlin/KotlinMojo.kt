package community.flock.wirespec.plugin.maven

import community.flock.wirespec.compiler.core.emit.KotlinEmitter
import community.flock.wirespec.compiler.core.emit.common.DEFAULT_PACKAGE_NAME
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

@Mojo(name = "kotlin", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class KotlinMojo : WirespecMojo() {

    @Parameter(required = true)
    private lateinit var sourceDirectory: String

    @Parameter(required = true)
    private lateinit var targetDirectory: String

    @Parameter
    private var packageName: String = DEFAULT_PACKAGE_NAME

    @Parameter(defaultValue = "\${project}", readonly = true, required = true)
    private lateinit var project: MavenProject

    private val emitter = KotlinEmitter(logger, packageName)

    override fun execute() {
        compile(sourceDirectory, logger, emitter).forEach { (name, result) ->
            (if (packageName.isBlank()) "" else "/" + packageName.split('.').joinToString("/"))
                .let { "$targetDirectory$it" }
                .also { File(it).mkdirs() }
                .let { File("$it/$name.kt") }
                .writeText(result)
        }
    }

}
