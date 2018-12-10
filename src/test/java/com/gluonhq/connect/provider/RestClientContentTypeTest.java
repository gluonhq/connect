/*
 * Copyright (c) 2017, 2018 Gluon
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
package com.gluonhq.connect.provider;

import com.gluonhq.connect.HttpServerManager;
import com.gluonhq.connect.converter.StringInputConverter;
import com.gluonhq.connect.converter.StringOutputConverter;
import com.gluonhq.connect.source.RestDataSource;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.testng.Assert.assertEquals;

public class RestClientContentTypeTest {

    @Test
    public void testCustomContentTypeWithPost() throws InterruptedException, IOException, TimeoutException {
        try (HttpServerManager httpServerManager = new HttpServerManager()){
            httpServerManager.startHttpServer(request -> {
                if (request.path().equals("/jsonBody")) {
                    request.response().setStatusCode(200).end("OK");
                }
            });

            RestDataSource restDataSource = RestClient.create()
                    .method("POST")
                    .host("http://localhost:45000")
                    .path("jsonBody")
                    .contentType("application/json")
                    .createRestDataSource();

            StringOutputConverter outputConverter = new StringOutputConverter();
            outputConverter.setOutputStream(restDataSource.getOutputStream());
            outputConverter.write("{\"body\":\"json\"}");

            StringInputConverter inputConverter = new StringInputConverter();
            inputConverter.setInputStream(restDataSource.getInputStream());
            String readString = inputConverter.read();
            assertEquals(readString, "OK");

            httpServerManager.verify(new HttpServerManager.HttpRequest()
                    .withMethod("POST")
                    .withHeader("content-type", "application/json"), 1);
        }
    }
}
