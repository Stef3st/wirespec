package community.flock.wirespec.integration.spring.java.web

import community.flock.wirespec.java.Wirespec
import jakarta.servlet.http.HttpServletRequest
import java.util.stream.Collectors
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class WirespecMethodArgumentResolver(
    private val wirespecSerialization: Wirespec.Serialization<String>
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        Wirespec.Request::class.java.isAssignableFrom(parameter.parameterType)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Wirespec.Request<*> {
        val servletRequest = webRequest.nativeRequest as HttpServletRequest
        val declaringClass = parameter.parameterType.declaringClass
        val handler = declaringClass.declaredClasses.toList().find { it.simpleName == "Handler" }
        val handlers = handler?.declaredClasses?.toList()?.find { it.simpleName == "Handlers" }
        val instance = handlers?.getDeclaredConstructor()?.newInstance() as Wirespec.Server<*, *>
        val server = instance.getServer(wirespecSerialization)
        return server.from(servletRequest.toRawRequest())
    }
}

fun HttpServletRequest.toRawRequest(): Wirespec.RawRequest = Wirespec.RawRequest(
    method,
    pathInfo.split("/"),
    queryString
        ?.split("&")
        ?.associate {
            val (key, value) = it.split("=")
            key to value
        }
        .orEmpty(),
    headerNames.toList().associateWith(::getHeader),
    reader.lines().collect(Collectors.joining(System.lineSeparator()))
)