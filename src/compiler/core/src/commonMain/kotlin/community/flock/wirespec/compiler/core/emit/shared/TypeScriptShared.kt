package community.flock.wirespec.compiler.core.emit.shared

import community.flock.wirespec.compiler.core.emit.common.Spacer

data object TypeScriptShared : Shared {
    override val source = """
        |export module Wirespec {
        |${Spacer}export type Method = "GET" | "PUT" | "POST" | "DELETE" | "OPTIONS" | "HEAD" | "PATCH" | "TRACE"
        |${Spacer}export type RawRequest = { method: Method, path: string[], queries: Record<string, string[]>, headers: Record<string, string[]>, body?: string }
        |${Spacer}export type RawResponse = { status: number, headers: Record<string, string[]>, body?: string }
        |${Spacer}export type Content<T> = { type:string, body:T }
        |${Spacer}export type Request<T> = { path: Record<string, string>, method: Method, query?: Record<string, any>, headers?: Record<string, any>, content?:Content<T> }
        |${Spacer}export type Response<T> = { status:number, headers?: Record<string, any[]>, content?:Content<T> }
        |${Spacer}export type Serialization = { serialize: <T>(type: T) => string; deserialize: <T>(raw: string | undefined) => T }
        |${Spacer}export type Client<REQ extends Request<any>, RES extends Response<any>> = (serialization: Serialization) => { to: (request: REQ) => RawRequest; from: (response: RawResponse) => RES }
        |${Spacer}export type Server<REQ extends Request<any>, RES extends Response<any>> = (serialization: Serialization) => { from: (request: RawRequest) => REQ; to: (response: RES) => RawResponse }
        |}
    """.trimMargin()
}