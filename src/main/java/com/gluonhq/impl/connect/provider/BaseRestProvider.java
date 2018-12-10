/*
 * Copyright (c) 2016 Gluon
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
package com.gluonhq.impl.connect.provider;

import com.gluonhq.connect.source.RestDataSource;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseRestProvider {

    private static final Logger LOG = Logger.getLogger(BaseRestProvider.class.getName());

    private static final String RESPONSE_HEADER_CONTENT_TYPE = "Content-Type";

    protected static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    protected static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    protected final RestDataSource dataSource;

    public BaseRestProvider(RestDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Returns the rest data source that is used as the input and output data source.
     *
     * @return the rest data source being used as the input and output data source
     */
    public RestDataSource getRestDataSource() {
        return dataSource;
    }

    /**
     * Returns the first Content-Type response header from the rest data source. The value of the content type
     * can be <code>null</code> when the response header is not provided by the HTTP response. Note that the method
     * {@link RestDataSource#getInputStream()} should already be called before calling this method.
     *
     * @return the Content-Type response header or <code>null</code> if the response header is not provided
     */
    String getContentType() {
        String contentType = null;

        Map<String, List<String>> responseHeaders = dataSource.getResponseHeaders();
        if (responseHeaders.containsKey(RESPONSE_HEADER_CONTENT_TYPE)) {
            List<String> contentTypes = responseHeaders.get(RESPONSE_HEADER_CONTENT_TYPE);
            if (! contentTypes.isEmpty()) {
                contentType = contentTypes.get(0);
            }
        }

        LOG.log(Level.FINE, "Detected Content-Type from response headers: " + contentType);

        return contentType;
    }
}
