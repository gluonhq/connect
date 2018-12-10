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
package com.gluonhq.connect.source;

import com.gluonhq.connect.provider.RestClient;
import com.gluonhq.connect.MultiValuedMap;
import com.gluonhq.impl.connect.OAuth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * An implementation of {@link IODataSource} that can read from and write to an HTTP URL resource.
 *
 * <p><b>Attention:</b> it is advised not to use this class directly, but rather construct it by creating a
 * {@link RestClient} and build the RestDataSource with the {@link RestClient#createRestDataSource()} method.
 */
public class RestDataSource implements IODataSource {

    private static final Logger LOG = Logger.getLogger(RestDataSource.class.getName());

    private static final String LINE_FEED = "\r\n";

    private String host;
    private String path = "";
    private String method = null;
    private int readTimeout = -1;
    private int connectTimeout = -1;
    private String dataString;
    private String consumerKey;
    private String consumerSecret;
    private MultiValuedMap<String, String> queryParams = new MultiValuedMap<>();
    private MultiValuedMap<String, String> formParams = new MultiValuedMap<>();
    private MultiValuedMap<String, String> headers = new MultiValuedMap<>();
    private MultiValuedMap<String, String> multipartStringFields = new MultiValuedMap<>();
    private MultiValuedMap<String, byte[]> multipartByteFields = new MultiValuedMap<>();
    private String contentType;

    private HttpURLConnection connection;

    private Map<String, List<String>> responseHeaders;
    private int responseCode = -1;
    private String responseMessage;

    /**
     * Returns an InputStream that is able to read data from an HTTP URL that will be constructed
     * with the settings defined on this data source.
     *
     * @return an InputStream that is able to read from an HTTP URL
     * @throws IOException when the HTTP connection could not be established or the InputStream could not be created
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return createInputStream();
    }

    /**
     * Returns an OutputStream that is able to write data to an HTTP URL that will be constructed
     * with the settings defined on this data source.
     *
     * @return an OutputStream that is able to write data to an HTTP URL
     * @throws IOException when the HTTP connection could not be established or the OutputStream could not be created
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return createOutputStream();
    }

    /**
     * Returns the complete host address of the URL to use for the HTTP connection. The host consists of the scheme, the
     * remote host name and the port. If no port is specified, the default scheme port will be used.
     *
     * @return the complete host address of the URL
     */
    public String getHost() {
        return host;
    }

    /**
     * Sets the complete host address of the URL to use for the HTTP connection. The host consists of the scheme, the
     * remote host name and the port. If no port is specified, the default scheme port will be used.
     *
     * @param host the complete host address of the URL
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Returns the entire path of the URL to use for the HTTP connection.
     *
     * @return the entire path of the URL
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the entire path of the URL to use for the HTTP connection. A forward slash will automatically be added in
     * front of the provided <code>path</code> if it is missing.
     *
     * @param path the entire path of the URL
     */
    public void setPath(String path) {
        if (path == null) {
            this.path = "";
        } else {
            if (path.startsWith("/")) {
                this.path = path;
            } else {
                this.path = "/" + path;
            }
        }
    }

    /**
     * Returns the request method to use for the HTTP connection.
     *
     * @return the request method for the HTTP connection
     * @see HttpURLConnection#getRequestMethod()
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the request method to use for the HTTP connection. When the request method is not specified, POST will be
     * used if any form parameters or a data string is set. Otherwise, the GET method will be used.
     *
     * @param method the request method for the HTTP connection
     * @see HttpURLConnection#setRequestMethod(String)
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the read timeout for the HTTP connection, in milliseconds. A timeout of zero is interpreted as an infinite
     * timeout.
     *
     * @return an int that indicates the read timeout value in milliseconds
     * @see URLConnection#getReadTimeout()
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the read timeout for the HTTP connection, in milliseconds. A timeout of zero is interpreted as an infinite
     * timeout.
     *
     * @param readTimeout an int that specifies the timeout value to be used in milliseconds
     * @see URLConnection#setReadTimeout(int)
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    /**
     * Gets the connect timeout for the HTTP connection, in milliseconds. A timeout of zero is interpreted as an
     * infinite timeout.
     *
     * @return an int that indicates the connect timeout value in milliseconds
     * @see URLConnection#getConnectTimeout()
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connect timeout for the HTTP connection, in milliseconds. A timeout of zero is interpreted as an
     * infinite timeout.
     *
     * @param connectTimeout an int that specifies the timeout value to be used in milliseconds
     * @see URLConnection#setConnectTimeout(int)
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Gets the entity to use for the HTTP connection.
     *
     * @return the entity of the HTTP connection
     */
    public String getDataString() {
        return dataString;
    }

    /**
     * Sets the entity to use for the HTTP connection. The <code>dataString</code> will be written to the OutputStream
     * of the HTTP connection. Please note, when specifying both a data string and form parameters, the data string
     * will be appended with an ampersand, followed by the encoded list of form parameters.
     *
     * @param dataString the entity for the HTTP connection
     */
    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    /**
     * Gets the consumer key to use for creating the OAuth 1.0 signature that is sent along with the request, by setting
     * the <code>Authorization</code> request header.
     *
     * @return the consumer key used for calculating the OAuth 1.0 signature
     */
    public String getConsumerKey() {
        return consumerKey;
    }

    /**
     * Sets the consumer key to use for creating the OAuth 1.0 signature that is sent along with the request, by setting
     * the <code>Authorization</code> request header. Setting the {@link #setConsumerSecret(String) consumer secret} is
     * mandatory when setting the consumer key.
     *
     * @param consumerKey the consumer key used for calculating the OAuth 1.0 signature
     */
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    /**
     * Gets the consumer secret to use for creating the OAuth 1.0 signature that is sent along with the request, by
     * setting the <code>Authorization</code> request header.
     *
     * @return the consumer secret used for calculating the OAuth 1.0 signature
     */
    public String getConsumerSecret() {
        return consumerSecret;
    }

    /**
     * Sets the consumer secret to use for creating the OAuth 1.0 signature that is sent along with the request, by
     * setting the <code>Authorization</code> request header. Setting the consumer secret has no effect when the
     * {@link #setConsumerKey(String) consumer key} is not set.
     *
     * @param consumerSecret the consumer secret used for calculating the OAuth 1.0 signature
     */
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    /**
     * Add a single query parameter to the request.
     *
     * @param key the key of the query parameter
     * @param value the value of the query parameter
     */
    public void addQueryParam(String key, String value) {
        queryParams.putSingle(key, value);
    }

    /**
     * Returns a list of query parameters that will be sent along with the request.
     *
     * @return the list of query parameters for the request
     */
    public MultiValuedMap<String, String> getQueryParams() {
        return queryParams;
    }

    /**
     * Sets the list of query parameters to be sent along with the request. The list is a multi valued map, hence there
     * can be more than one value assigned to the same query parameter key.
     *
     * @param queryParams the list of query parameters to be sent with the request
     */
    public void setQueryParams(MultiValuedMap<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * Add a single form parameter to the request.
     *
     * @param key the key of the form parameter
     * @param value the value of the form parameter
     */
    public void addFormParam(String key, String value) {
        formParams.putSingle(key, value);
    }

    /**
     * Returns a list of form parameters that will be sent along with the request.
     *
     * @return the list of form parameters for the request
     */
    public MultiValuedMap<String, String> getFormParams() {
        return formParams;
    }

    /**
     * Sets the list of form parameters to be sent along with the request. The list is a multi valued map, hence there
     * can be more than one value assigned to the same form parameter key.
     *
     * @param formParams the list of form parameters to be sent with the request
     */
    public void setFormParams(MultiValuedMap<String, String> formParams) {
        this.formParams = formParams;
    }

    /**
     * Gets the Content-Type request header that will be set on the HTTP connection.
     * @return the Content-Type request header for the HTTP connection
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the Content-Type request header for the HTTP connection. The request header will only be set when either a
     * {@link #setDataString(String) data string} or {@link #setFormParams(MultiValuedMap) form parameters} were set.
     * In case the content type header was not set, it will by default be set to
     * <code>application/x-www-form-urlencoded</code>.
     *
     * @param contentType the Content-Type request header for the HTTP connection
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    /**
     * Adds a single HTTP header to the request.
     * NOTE: Headers set using this method will be included <em>in addition to</em> those set via other methods (e.g. by {@link #setContentType(String)}),
     * so to avoid complications you should only set headers here if you haven't set them via any other methods
     * @param field The name of the HTTP header field (e.g. "Accept")
     * @param value The header value to send with the request
     */
    public void addHeader (String field, String value) {
        headers.putSingle(field, value);
    }

    /**
     * Returns a list of HTTP headers which will be sent along with the request.
     * Note: This only returns headers defined by either {@link #addHeader(String, String)} or {@link #setHeaders(MultiValuedMap)},
     * not those defined in any other way (e.g. by {@link #setContentType(String)})
     * @return the list of HTTP headers to be sent
     */
    public MultiValuedMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * Sets the list of HTTP headers to be sent along with the request. The list is a multi valued map, hence there
     * can be more than one header with the same field sent.
     *
     * @param headers the list of headers to be sent with the request
     */
    public void setHeaders(MultiValuedMap<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Returns a list of multipart form fields that will be sent along with the request as part of the multipart form
     * data.
     *
     * @return the list of multipart form fields to be sent
     */
    public MultiValuedMap<String, String> getMultipartStringFields() {
        return multipartStringFields;
    }

    /**
     * Sets the list of multipart form fields that will be sent along with the request as part of the multipart form
     * data.
     *
     * @param multipartStringFields the list of multipart form fields to be sent with the request
     */
    public void setMultipartStringFields(MultiValuedMap<String, String> multipartStringFields) {
        this.multipartStringFields = multipartStringFields;
    }

    /**
     * Returns a list of multipart binary fields that will be sent along with the request as part of the multipart
     * form data.
     *
     * @return the list of multipart binary fields to be sent
     */
    public MultiValuedMap<String, byte[]> getMultipartByteFields() {
        return multipartByteFields;
    }

    /**
     * Sets the list of multipart binary fields that will be sent along with the request as part of the multipart form
     * data.
     *
     * @param multipartByteFields the list of multipart binary fields to be sent with the request
     */
    public void setMultipartByteFields(MultiValuedMap<String, byte[]> multipartByteFields) {
        this.multipartByteFields = multipartByteFields;
    }

    private void createRequest() throws IOException {
        if (connection != null) {
            return;
        }

        String urlBase = host + path;

        String request = urlBase;

        String queryString = createQueryString();
        if (queryString != null) {
            request += "?" + queryString;
        }

        if (method == null) {
            if (formParams.isEmpty() && dataString == null) {
                method = "GET";
            } else {
                method = "POST";
            }
        }

        URL url = new URL(request);
        connection = (HttpURLConnection) url.openConnection();

        if (consumerKey != null) {
            try {
                MultiValuedMap<String, String> allParams = new MultiValuedMap<>();
                allParams.putAll(queryParams);
                allParams.putAll(formParams);
                String header = OAuth.getHeader(method, urlBase, allParams, consumerKey, consumerSecret);
                connection.addRequestProperty("Authorization", header);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RestDataSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(RestDataSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        connection.setRequestMethod(method);

        if (readTimeout > -1) {
            connection.setReadTimeout(readTimeout);
        }
        if (connectTimeout > -1) {
            connection.setConnectTimeout(connectTimeout);
        }

        if (headers != null) {
            for (Map.Entry<String, List<String>> requestProperty : headers.entrySet()) {
                for (String value : requestProperty.getValue()) {
                    connection.addRequestProperty(requestProperty.getKey(), value);
                }
            }
        }

        if (formParams != null && !formParams.isEmpty()) {
            if (dataString == null) {
                dataString = "";
            }

            for (Map.Entry<String, List<String>> entryList : formParams.entrySet()) {
                String key = entryList.getKey();
                for (String val : entryList.getValue()) {
                    if (val == null) {
                        throw new IllegalArgumentException("Values in form parameters can't be null -- was null for key " + key);
                    }
                    if (!dataString.isEmpty()) {
                        dataString += "&";
                    }
                    String eval = URLEncoder.encode(val, "UTF-8");
                    dataString = dataString + key + "=" + eval;
                }
            }
        }

        LOG.log(Level.FINE, "Created Rest Connection:\n\tMethod: " + method + "\n\tRequest URL: " + request + "\n\tForm Params: " + formParams + "\n\tContentType: " + contentType + "\n\tConsumer Credentials: " + consumerKey + " / " + (consumerSecret != null ? "********" : "null"));
    }

    private InputStream createInputStream() throws IOException {
        createRequest();

        // HttpURLConnection.getDoOutput() is true if the output stream has already been written to
        if (!connection.getDoOutput()) {
            if (dataString != null) {
                connection.setDoOutput(true);
                if (contentType == null) {
                    contentType = "application/x-www-form-urlencoded";
                }
                connection.setRequestProperty("Content-Type", contentType);

                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
                    outputStreamWriter.write(dataString);
                }
            } else if ("multipart/form-data".equals(contentType)) {
                connection.setDoOutput(true);
                String boundary = addMultipartBoundary(connection);

                try (OutputStream outputStream = connection.getOutputStream(); OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8")) {
                    writeMultipart(outputStream, writer, boundary);
                }
            }
        }

        InputStream finalInputStream;
        if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
            InputStream inputStream = connection.getInputStream();
            PushbackInputStream pb = new PushbackInputStream(inputStream, 2);
            byte[] hdr = new byte[2];
            int bytesRead = pb.read(hdr);
            if (bytesRead >= 0) {
                pb.unread(hdr, 0, bytesRead);
            }
            if (bytesRead == 2 && hdr[0] == (byte) GZIPInputStream.GZIP_MAGIC && hdr[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)) {
                finalInputStream = new GZIPInputStream(pb);
            } else {
                finalInputStream = pb;
            }
        } else {
            finalInputStream = connection.getErrorStream();
        }

        // Try to get the response headers, response code and response message that were returned from the server.
        // When these are not available, the original IOException will be thrown instead.
        this.responseHeaders = connection.getHeaderFields();
        this.responseCode = connection.getResponseCode();
        this.responseMessage = connection.getResponseMessage();

        return finalInputStream;
    }

    private OutputStream createOutputStream() throws IOException {
        createRequest();

        connection.setDoOutput(true);
        if (contentType == null) {
            contentType = "application/x-www-form-urlencoded";
        }

        if (dataString != null && "application/x-www-form-urlencoded".equals(contentType)) {
            connection.setRequestProperty("Content-Type", contentType);

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream())) {
                outputStreamWriter.write(dataString);
            }
        } else if ("multipart/form-data".equals(contentType)) {
            String boundary = addMultipartBoundary(connection);

            OutputStream outputStream = connection.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");

            writeMultipart(outputStream, writer, boundary);
        } else {
            connection.setRequestProperty("Content-Type", contentType);
        }

        return connection.getOutputStream();
    }

    private String createQueryString() {
        if (queryParams.isEmpty()) {
            return null;
        }

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            for (String value : entry.getValue()) {
                if (queryString.length() == 0) {
                    queryString = new StringBuilder(entry.getKey()).append("=").append(value);
                } else {
                    queryString.append("&").append(entry.getKey()).append("=").append(value);
                }
            }
        }
        return queryString.toString();
    }

    /**
     * Gets the response header fields from an HTTP response message.
     *
     * @return a Map of header fields.
     * @see HttpURLConnection#getHeaderFields()
     */
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Gets the status code from an HTTP response message.
     *
     * @return the HTTP Status-Code, or -1
     * @see HttpURLConnection#getResponseCode()
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Gets the HTTP response message, if any, returned along with the response
     * code from a server.
     *
     * @return the HTTP response message, or <code>null</code>
     * @see HttpURLConnection#getResponseMessage()
     */
    public String getResponseMessage() {
        return responseMessage;
    }

    private String addMultipartBoundary(HttpURLConnection connection) {
        String boundary = "---" + System.currentTimeMillis();
        connection.setRequestProperty("Content-Type", contentType + "; boundary=" + boundary);
        return boundary;
    }

    private void writeMultipart(OutputStream outputStream, Writer writer, String boundary) throws IOException {
        for (Map.Entry<String, List<String>> entry : multipartStringFields.entrySet()) {
            for (String value : entry.getValue()) {
                addMultipartFormField(boundary, writer, entry.getKey(), value);
            }
        }

        for (Map.Entry<String, List<byte[]>> entry : multipartByteFields.entrySet()) {
            for (byte[] value : entry.getValue()) {
                addMultipartFormField(boundary, writer, outputStream, entry.getKey(), value);
            }
        }

        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
    }

    private void addMultipartFormField(String boundary, Writer writer, String name, String value) throws IOException {
        writer.append("--").append(boundary).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED)
                .append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED)
                .append(LINE_FEED)
                .append(value).append(LINE_FEED);
        writer.flush();
    }

    private void addMultipartFormField(String boundary, Writer writer, OutputStream os, String name, byte[] value) throws IOException {
        writer.append("--").append(boundary).append(LINE_FEED)
                .append("Content-Disposition: form-data; name=\"").append(name).append("\"; filename=\"raw\"").append(LINE_FEED)
                .append("Content-Type: application/octet-stream").append(LINE_FEED)
                .append("Content-Transfer-Encoding: binary").append(LINE_FEED)
                .append(LINE_FEED);
        writer.flush();

        os.write(value);
        os.flush();

        writer.append(LINE_FEED);
        writer.flush();
    }
}
