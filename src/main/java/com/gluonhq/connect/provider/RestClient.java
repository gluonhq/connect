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
package com.gluonhq.connect.provider;

import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.converter.InputStreamInputConverter;
import com.gluonhq.connect.converter.InputStreamIterableInputConverter;
import com.gluonhq.connect.converter.OutputStreamOutputConverter;
import com.gluonhq.connect.source.RestDataSource;
import com.gluonhq.connect.MultiValuedMap;

/**
 * <p>The RestClient assists in using the {@link DataProvider} with HTTP URLs as the data source. For instance, to read
 * an object from a sample URL that responds with json, you can use the following code:</p>
 *
 * <pre>
 *     RestClient restClient = RestClient.create()
 *             .host("http://myhost.com")
 *             .path("restservice/2/sample/19")
 *             .method("GET");
 *     GluonObservableObject&lt;Sample&gt; sample = DataProvider.retrieveObject(restClient.createObjectDataReader(Sample.class));
 * </pre>
 */
public class RestClient {

    private String host;
    private String path;
    private String method;
    private int readTimeout;
    private int connectTimeout;
    private String dataString;
    private String consumerKey;
    private String consumerSecret;
    private MultiValuedMap<String, String> headers = new MultiValuedMap<>();
    private MultiValuedMap<String, String> queryParams = new MultiValuedMap<>();
    private MultiValuedMap<String, String> formParams = new MultiValuedMap<>();
    private MultiValuedMap<String, String> multipartStringFields = new MultiValuedMap<>();
    private MultiValuedMap<String, byte[]> multipartByteFields = new MultiValuedMap<>();
    private String contentType;

    private RestClient() {
    }

    /**
     * Create a RestClient builder for constructing a RestDataSource or one of the classes that are used in the methods
     * of the {@link DataProvider}.
     *
     * @return a RestClient that can be used together with the {@link DataProvider}
     */
    public static RestClient create() {
        return new RestClient();
    }

    /**
     * Sets the complete host address of the URL for the HTTP connection. The host consists of the scheme, the
     * remote host name and the port. If no port is specified, the default scheme port will be used.
     *
     * @param host the complete host address of the URL
     * @return A reference to this rest client.
     * @see RestDataSource#setHost(String)
     */
    public RestClient host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the entire path of the URL for the HTTP connection. A forward slash will automatically be added in
     * front of the provided <code>path</code> if it is missing.
     *
     * @param path the entire path of the URL
     * @return A reference to this rest client.
     * @see RestDataSource#setPath(String)
     */
    public RestClient path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Sets the request method to use for the HTTP connection.
     *
     * @param method the request method for the HTTP connection
     * @return A reference to this rest client.
     * @see RestDataSource#setMethod(String)
     */
    public RestClient method(String method) {
        this.method = method;
        return this;
    }

    /**
     * Sets the read timeout for the HTTP connection, in milliseconds. A timeout of zero is interpreted as an infinite
     * timeout.
     *
     * @param readTimeout an int that specifies the timeout value to be used in milliseconds
     * @return A reference to this rest client.
     * @see RestDataSource#setReadTimeout(int)
     */
    public RestClient readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Sets the connect timeout for the HTTP connection, in milliseconds. A timeout of zero is interpreted as an
     * infinite timeout.
     *
     * @param connectTimeout an int that specifies the timeout value to be used in milliseconds
     * @return A reference to this rest client.
     * @see RestDataSource#setConnectTimeout(int)
     */
    public RestClient connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    /**
     * Sets the entity to use for the HTTP connection. The <code>dataString</code> will be written to the OutputStream
     * of the HTTP connection. Please note, when specifying both a data string and form parameters, the data string
     * will be appended with an ampersand, followed by the encoded list of form parameters.
     *
     * @param dataString the entity for the HTTP connection
     * @return A reference to this rest client.
     * @see RestDataSource#setDataString(String)
     */
    public RestClient dataString(String dataString) {
        this.dataString = dataString;
        return this;
    }

    /**
     * Sets the consumer key to use for creating the OAuth 1.0 signature that is sent along with the request, by setting
     * the <code>Authorization</code> request header. Setting the {@link #consumerSecret(String) consumer secret} is
     * mandatory when setting the consumer key.
     *
     * @param consumerKey the consumer key used for calculating the OAuth 1.0 signature
     * @return A reference to this rest client.
     * @see RestDataSource#setConsumerKey(String)
     */
    public RestClient consumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
        return this;
    }

    /**
     * Sets the consumer secret to use for creating the OAuth 1.0 signature that is sent along with the request, by
     * setting the <code>Authorization</code> request header. Setting the consumer secret has no effect when the
     * {@link #consumerKey(String) consumer key} is not set.
     *
     * @param consumerSecret the consumer secret used for calculating the OAuth 1.0 signature
     * @return A reference to this rest client.
     * @see RestDataSource#setConsumerSecret(String)
     */
    public RestClient consumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
        return this;
    }

    /**
     * Add a single query parameter to the request.
     *
     * @param key the key of the query parameter
     * @param value the value of the query parameter
     * @return A reference to this rest client.
     * @see RestDataSource#addQueryParam(String, String)
     */
    public RestClient queryParam(String key, String value) {
        this.queryParams.putSingle(key, value);
        return this;
    }

    /**
     * Add a single form parameter to the request.
     *
     * @param key the key of the form parameter
     * @param value the value of the form parameter
     * @return A reference to this rest client.
     * @see RestDataSource#addFormParam(String, String)
     */
    public RestClient formParam(String key, String value) {
        this.formParams.putSingle(key, value);
        return this;
    }

    /**
     * Sets the Content-Type request header for the HTTP connection. The request header will only be set when either a
     * {@link #dataString(String) data string} or {@link #formParam(String, String) form parameters} were set. In case
     * the content type header was not set, it will by default be set to <code>application/x-www-form-urlencoded</code>.
     *
     * @param contentType the Content-Type request header for the HTTP connection
     * @return A reference to this rest client.
     * @see RestDataSource#setContentType(String)
     */
    public RestClient contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
    
    /**
     * Adds a single HTTP header to the request.
     * NOTE: Headers set using this method will be included <em>in addition to</em> those set via other methods (e.g. by {@link #contentType(String)}),
     * so to avoid complications you should only set headers here if you haven't set them via any other methods
     * @param field The name of the HTTP header field (e.g. "Accept")
     * @param value The header value to send with the request
     * @return A reference to this rest client.
     */
    public RestClient header(String field, String value) {
        this.headers.putSingle(field, value);
        return this;
    }

    /**
     * <p>Adds a form field to an HTTP Multipart Form-Data request. The content type of the form field will be
     * <code>text/plain</code>.</p>
     *
     * <p><b>Note:</b> the {@link #contentType(String) content type} needs to be set explicitly to
     * <code>multipart/form-data</code> for the multipart form to be written to the request.</p>
     *
     * @param field the name of the form field
     * @param value the value of the form field
     * @return A reference to this rest client.
     */
    public RestClient multipartField(String field, String value) {
        this.multipartStringFields.putSingle(field, value);
        return this;
    }

    /**
     * <p>Adds a binary field to an HTTP Multipart Form-Data request. The content type of the binary field will be
     * <code>application/octet-stream</code>.</p>
     *
     * <p><b>Note:</b> the {@link #contentType(String) content type} needs to be set explicitly to
     * <code>multipart/form-data</code> for the multipart form to be written to the request.</p>
     *
     * @param field the name of the form part
     * @param value the bytes of the form part
     * @return A reference to this rest client.
     */
    public RestClient multipartField(String field, byte[] value) {
        this.multipartByteFields.putSingle(field, value);
        return this;
    }

    /**
     * Build a RestDataSource that can be used as an InputDataSource to read from or an OutputDataSource to write to the
     * HTTP connection that is created with the parameters specified by this rest client builder.
     *
     * @return A RestDataSource that works with an HTTP connection, created with parameters that are set on this rest
     * client.
     */
    public RestDataSource createRestDataSource() {
        RestDataSource dataSource = new RestDataSource();
        applyFields(dataSource);
        return dataSource;
    }

    /**
     * Creates an instance of {@link ObjectDataReader} that can be passed directly in the
     * {@link DataProvider#retrieveObject(ObjectDataReader)} method. The object data reader will read the data from
     * the HTTP connection provided by the RestDataSource.
     *
     * <p>The object data reader will try to detect the converter to use based on the Content-Type response header that
     * is returned from the HTTP connection. The specified <code>targetClass</code> will be passed in to the detected
     * converter where needed. A custom converter can be specified with {@link #createObjectDataReader(InputStreamInputConverter)}
     * when no suitable converter could be detected.
     *
     * @param targetClass the class definition of the object to read
     * @param <T> the type of the object to read
     * @return an ObjectDataReader instance that constructs an object from an HTTP connection with an automatically
     * detected converter
     */
    public <T> ObjectDataReader<T> createObjectDataReader(Class<T> targetClass) {
        return new RestObjectDataReader<>(createRestDataSource(), targetClass);
    }

    /**
     * Creates an instance of {@link ObjectDataReader} that can be passed directly in the
     * {@link DataProvider#retrieveObject(ObjectDataReader)} method. The object data reader will read the data from
     * the HTTP connection provided by the RestDataSource and convert it into an object by using the specified
     * <code>converter</code>.
     *
     * @param converter the converter to use to convert the data read from the HTTP connection into an object
     * @param <T> the type of the object to read
     * @return an ObjectDataReader instance that constructs an object from an HTTP connection with the specified
     * converter
     */
    public <T> ObjectDataReader<T> createObjectDataReader(InputStreamInputConverter<T> converter) {
        return new RestObjectDataReader<>(createRestDataSource(), converter);
    }

    /**
     * Creates an instance of {@link ObjectDataWriter} that can be passed directly in the
     * {@link DataProvider#storeObject(Object, ObjectDataWriter)} method.
     *
     * The object data writer will try to detect the output converter to use based on the {@link #contentType(String) Content-Type}
     * request header that was set. The specified <code>targetClass</code> will be passed in to the detected converter
     * where needed. A custom output converter can be specified with {@link #createObjectDataWriter(OutputStreamOutputConverter, InputStreamInputConverter)}
     * when no suitable converter could be detected. If an output converter could be found, the writer will convert an
     * object by using the detected converter and writes the converted data to the OutputStream of the HTTP connection
     * that is provided by the RestDataSource.
     *
     * <p>The ObjectDataWriter also returns an optional object. In case of the RestClient, the returned object will be
     * read from the response of the HTTP request. The object data reader will try to detect the input converter to use
     * based on the Content-Type response header that is returned from the HTTP connection. The specified
     * <code>targetClass</code> will be passed in to the detected input converter as well where needed. The reader will
     * then use the input converter to convert the read bytes from the response of the HTTP request into an object.
     *
     * @param targetClass the class definition of the object to write
     * @param <T> the type of the object to write
     * @return an ObjectDataWriter instance that writes an object into an HTTP connection with an automatically detected
     * output converter and that constructs an object from that HTTP connection with an automatically detected input
     * converter
     */
    public <T> ObjectDataWriter<T> createObjectDataWriter(Class<T> targetClass) {
        return new RestObjectDataWriterAndRemover<>(createRestDataSource(), targetClass);
    }

    /**
     * Creates an instance of {@link ObjectDataWriter} that can be passed directly in the
     * {@link DataProvider#storeObject(Object, ObjectDataWriter)} method. The object data writer will convert an object
     * by using the specified output converter and writes the converted data to the OutputStream of the HTTP connection
     * that is provided by the RestDataSource.
     *
     * <p>The ObjectDataWriter also returns an optional object. In case of the RestClient, the returned object will be
     * read from the response of the HTTP request. The object data reader will use the specified input converter to
     * convert the read bytes from the response of the HTTP request into an object.
     *
     * @param outputConverter the output converter to use to convert the object into data to write to the HTTP connection
     * @param inputConverter the input converter to use to convert the data read from the HTTP connection into an object
     * @param <T> the type of the object to write
     * @return an ObjectDataWriter instance that writes an object into an HTTP connection with the specified
     * outputConverter and that constructs an object from the HTTP connection response with the specified inputConverter
     */
    public <T> ObjectDataWriter<T> createObjectDataWriter(OutputStreamOutputConverter<T> outputConverter,
                                                          InputStreamInputConverter<T> inputConverter) {
        return new RestObjectDataWriterAndRemover<>(createRestDataSource(), outputConverter, inputConverter);
    }

    /**
     * Creates an instance of {@link ObjectDataRemover} that can be passed directly in the
     * {@link DataProvider#removeObject(GluonObservableObject, ObjectDataRemover)} method. See the documentation for the
     * {@link #createObjectDataWriter(Class)} method, as the writer works exactly the same as the remover.
     *
     * @param targetClass the class definition of the object to remove
     * @param <T> the type of the object to remove
     * @return an ObjectDataRemover instance that writes an object into an HTTP connection with the specified
     * outputConverter and that constructs an object from the HTTP connection response with the specified inputConverter
     */
    public <T> ObjectDataRemover<T> createObjectDataRemover(Class<T> targetClass) {
        return new RestObjectDataWriterAndRemover<>(createRestDataSource(), targetClass);
    }

    /**
     * Creates an instance of {@link ObjectDataRemover} that can be passed directly in the
     * {@link DataProvider#removeObject(GluonObservableObject, ObjectDataRemover)} method. See the documentation for the
     * {@link #createObjectDataWriter(OutputStreamOutputConverter, InputStreamInputConverter)} method, as the
     * writer works exactly the same as the remover.
     *
     * @param outputConverter the output converter to use to convert the object into data to write to the HTTP connection
     * @param inputConverter the input converter to use to convert the data read from the HTTP connection into an object
     * @param <T> the type of the object to remove
     * @return an ObjectDataRemover instance that writes an object into an HTTP connection with the specified
     * outputConverter and that constructs an object from the HTTP connection response with the specified inputConverter
     */
    public <T> ObjectDataRemover<T> createObjectDataRemover(OutputStreamOutputConverter<T> outputConverter,
                                                            InputStreamInputConverter<T> inputConverter) {
        return new RestObjectDataWriterAndRemover<>(createRestDataSource(), outputConverter, inputConverter);
    }

    /**
     * Creates an instance of {@link ListDataReader} that can be passed directly in the
     * {@link DataProvider#retrieveList(ListDataReader)} method. The list data reader will read the data from the HTTP
     * connection provided by the RestDataSource.
     *
     * <p>The list data reader will try to detect the converter to use based on the Content-Type response header that is
     * returned from the HTTP connection. The specified <code>targetClass</code> will be passed in to the detected
     * converter where needed. A custom converter can be specified with {@link #createListDataReader(InputStreamIterableInputConverter)}
     * when no suitable converter could be detected.
     *
     * @param targetClass the class definition of the objects contained in the list
     * @param <E> the type of the objects contained in the list to read
     * @return a ListDataReader instance that constructs a list of objects from the HTTP connection with an
     * automatically detected converter
     */
    public <E> ListDataReader<E> createListDataReader(Class<E> targetClass) {
        return new RestListDataReader<>(createRestDataSource(), targetClass);
    }

    /**
     * Creates an instance of {@link ListDataReader} that can be passed directly in the
     * {@link DataProvider#retrieveList(ListDataReader)} method. The list data reader will read the data from the HTTP
     * connection provided by the RestDataSource and converts it into a list of objects by using the specified
     * <code>converter</code>.
     *
     * @param converter the converter to use to convert the data read from the HTTP connection into a list of objects
     * @param <E> the type of the objects contained in the list to read
     * @return a ListDataReader instance that constructs a list of objects from the HTTP connection with the specified
     * converter
     */
    public <E> ListDataReader<E> createListDataReader(InputStreamIterableInputConverter<E> converter) {
        return new RestListDataReader<>(createRestDataSource(), converter);
    }

    private void applyFields(RestDataSource dataSource) {
        dataSource.setHost(host);
        dataSource.setPath(path);
        dataSource.setMethod(method);
        dataSource.setReadTimeout(readTimeout);
        dataSource.setConnectTimeout(connectTimeout);
        dataSource.setDataString(dataString);
        dataSource.setConsumerKey(consumerKey);
        dataSource.setConsumerSecret(consumerSecret);
        dataSource.setQueryParams(queryParams);
        dataSource.setFormParams(formParams);
        dataSource.setMultipartStringFields(multipartStringFields);
        dataSource.setMultipartByteFields(multipartByteFields);
        dataSource.setContentType(contentType);
        dataSource.setHeaders(headers);
    }

    @Override
    public String toString() {
        return "RestClient{" +
                "host='" + host + '\'' +
                ", path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", readTimeout=" + readTimeout +
                ", connectTimeout=" + connectTimeout +
                ", dataString='" + dataString + '\'' +
                ", consumerKey='" + consumerKey + '\'' +
                ", consumerSecret='" + consumerSecret + '\'' +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                ", formParams=" + formParams +
                ", multipartStringFields=" + multipartStringFields +
                ", multipartByteFields=" + multipartByteFields +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}
