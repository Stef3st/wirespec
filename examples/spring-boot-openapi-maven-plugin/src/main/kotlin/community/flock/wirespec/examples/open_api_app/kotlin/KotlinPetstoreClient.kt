package community.flock.wirespec.examples.open_api_app.kotlin

import com.fasterxml.jackson.databind.ObjectMapper
import community.flock.wirespec.generated.kotlin.v3.CreatePets
import community.flock.wirespec.generated.kotlin.v3.ListPets
import community.flock.wirespec.generated.kotlin.v3.WirespecShared
import community.flock.wirespec.generated.kotlin.v3.ShowPetById
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import java.net.URI
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaType

interface KotlinPetstoreClient : ListPets, CreatePets, ShowPetById

@Configuration
class KotlinPetClientConfiguration {

    @Bean
    fun kotlinContentMapper(objectMapper: ObjectMapper) =
        object : WirespecShared.ContentMapper<ByteArray> {
            override fun <T> read(
                content: WirespecShared.Content<ByteArray>,
                valueType: KType,
            ): WirespecShared.Content<T> = content.let {
                val type = objectMapper.constructType(valueType.javaType)
                val obj: T = objectMapper.readValue(content.body, type)
                WirespecShared.Content(it.type, obj)
            }

            override fun <T> write(
                content: WirespecShared.Content<T>,
            ): WirespecShared.Content<ByteArray> = content.let {
                val bytes = objectMapper.writeValueAsBytes(content.body)
                WirespecShared.Content(it.type, bytes)
            }
        }


    @Bean
    fun kotlinPetstoreClient(restTemplate: RestTemplate, kotlinContentMapper: WirespecShared.ContentMapper<ByteArray>): KotlinPetstoreClient =
        object : KotlinPetstoreClient {
            fun <Req : WirespecShared.Request<*>, Res : WirespecShared.Response<*>> handle(
                request: Req,
                responseMapper: (WirespecShared.ContentMapper<ByteArray>) -> (Int, Map<String, List<String>>, WirespecShared.Content<ByteArray>) -> Res
            ) = restTemplate.execute(
                URI("https://6467e16be99f0ba0a819fd68.mockapi.io${request.path}"),
                HttpMethod.valueOf(request.method.name),
                { req ->
                    request.content
                        ?.let { kotlinContentMapper.write(it) }
                        ?.let { req.body.write(it.body) }
                },
                { res ->
                    val contentType = res.headers.contentType?.toString() ?: error("No content type")
                    val content = WirespecShared.Content(contentType, res.body.readBytes())
                    responseMapper(kotlinContentMapper)(
                        res.statusCode.value(),
                        res.headers,
                        content
                    )
                }
            ) ?: error("No response")

            override suspend fun listPets(request: ListPets.Request<*>): ListPets.Response<*> {
                return handle(request, ListPets::RESPONSE_MAPPER)
            }

            override suspend fun createPets(request: CreatePets.Request<*>): CreatePets.Response<*> {
                return handle(request, CreatePets::RESPONSE_MAPPER)
            }

            override suspend fun showPetById(request: ShowPetById.Request<*>): ShowPetById.Response<*> {
                return handle(request, ShowPetById::RESPONSE_MAPPER)
            }

        }

}