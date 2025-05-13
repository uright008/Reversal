package cn.stars.elixir.compat

import cn.stars.elixir.account.MicrosoftAccount
import cn.stars.reversal.Reversal
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class OAuthServer(val handler: MicrosoftAccount.OAuthHandler) {
    private var httpServer = HttpServer.create(InetSocketAddress("localhost", 1919), 0)
    private var started = false

    /**
     * Start the server.
     */
    fun start() {
        try {
            if (started) {
                httpServer.stop(0)
                httpServer = HttpServer.create(InetSocketAddress("localhost", 1919), 0)
            }
            httpServer.createContext("/login", OAuthHttpHandler(this))
            httpServer.executor = Reversal.threadPoolExecutor
            httpServer.start()
            handler.openUrl(
                MicrosoftAccount.replaceKeys(
                    MicrosoftAccount.AuthMethod.AZURE_APP,
                    MicrosoftAccount.XBOX_PRE_AUTH_URL
                )
            )
            started = true
        } catch (_: Exception) {
            httpServer.stop(0)
            handler.authError("Address already bound")
        }
    }

    /**
     * Stop the server.
     */
    fun stop(isInterrupt: Boolean = true) {
        httpServer.stop(0)
        if (isInterrupt) {
            handler.authError("Has been interrupted")
        }
    }

    /**
     * The handler of the OAuth redirect http request.
     */
    class OAuthHttpHandler(private val server: OAuthServer) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val query = exchange.requestURI.query.split("&").map { it.split("=") }.associate { it[0] to it[1] }
            if(query.containsKey("code")) {
                try {
                    server.handler.authResult(MicrosoftAccount.buildFromAuthCode(query["code"]!!, MicrosoftAccount.AuthMethod.AZURE_APP))
                    response(exchange, "Login Success", 200)
                } catch (e: Exception) {
                    server.handler.authError(e.toString())
                    response(exchange, "Error: $e", 500)
                }
            } else {
                server.handler.authError("No code in the query")
                response(exchange, "No code in the query", 500)
            }
            server.stop(false)
        }

        private fun response(exchange: HttpExchange, message: String, code: Int) {
            val byte = message.toByteArray()
            exchange.sendResponseHeaders(code, byte.size.toLong())
            exchange.responseBody.write(byte)
            exchange.close()
        }
    }
}