/*
 * Copyright (c) 2018 Gluon
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of Gluon, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.connect;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpServerManager implements AutoCloseable {

    private HttpServer httpServer;
    private List<HttpServerRequest> requests = new ArrayList<>();

    public void startHttpServer(Handler<HttpServerRequest> requestHandler) throws InterruptedException, TimeoutException {
        CountDownLatch latch = new CountDownLatch(1);
        httpServer = Vertx.vertx().createHttpServer();
        httpServer.requestHandler(new RequestHandlerWrapper(requestHandler));
        httpServer.listen(45000, asyncResult -> {
            if (asyncResult.failed()) {
                asyncResult.cause().printStackTrace();
            }
            latch.countDown();
        });

        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new TimeoutException("HttpServer could not be started in time.");
        }
    }

    @Override
    public void close() {
        if (httpServer != null) {
            httpServer.close();
        }
    }

    public void verify(HttpRequest request, long times) {
        long count = requests.stream().filter(request::matches).count();
        if (times != count) {
            throw new AssertionError("Request must be called exactly " + times + " times, but was called " + count + " times instead.");
        }
    }

    private class RequestHandlerWrapper implements Handler<HttpServerRequest> {

        private Handler<HttpServerRequest> handler;

        RequestHandlerWrapper(Handler<HttpServerRequest> handler) {
            this.handler = handler;
        }

        @Override
        public void handle(HttpServerRequest request) {
            requests.add(request);
            handler.handle(request);
        }
    }

    public static class HttpRequest {

        private String method;
        private Map<String, String> headers = new HashMap<>();

        public HttpRequest withMethod(String method) {
            this.method = method;
            return this;
        }

        public HttpRequest withHeader(String header, String value) {
            this.headers.put(header, value);
            return this;
        }

        boolean matches(HttpServerRequest request) {
            return (this.method == null || this.method.equals(request.rawMethod())) &&
                    headers.entrySet().stream().allMatch(e -> request.getHeader(e.getKey()).equals(e.getValue()));
        }
    }
}
