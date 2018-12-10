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
import com.gluonhq.connect.source.FileDataSource;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * <p>The FileClient assists in using the {@link DataProvider} with files, that are located on the local file system, as
 * the data source. For instance, to read a file that contains json data, you can use the following code:</p>
 *
 * <pre>
 *     FileClient fileClient = FileClient.create(new File("sample.json"));
 *     JsonInputConverter&lt;Sample&gt; converter = new JsonInputConverter&lt;&gt;(Sample.class);
 *     GluonObservableObject&lt;Sample&gt; sample = DataProvider.retrieveObject(fileClient.createObjectDataReader(converter));
 * </pre>
 */
public class FileClient {

    private File file;

    private FileClient(File file) {
        this.file = file;
    }

    /**
     * Create a FileClient builder using the specified file as a data source.
     *
     * @param file the file to be used as the data source
     * @return a FileClient that can be used together with the {@link DataProvider}
     */
    public static FileClient create(File file) {
        return new FileClient(file);
    }

    /**
     * Build a FileDataSource that can be used as an InputDataSource to read from the file or an OutputDataSource to
     * write to the file.
     *
     * @return a FileDataSource that works with the file that was specified on this FileClient
     */
    public FileDataSource createFileDataSource() {
        return new FileDataSource(file);
    }

    /**
     * Creates an instance of {@link ObjectDataReader} that can be passed directly in the
     * {@link DataProvider#retrieveObject(ObjectDataReader)} method. The object data reader will read the data from
     * the file that was specified on this FileClient and convert it into an object by using the specified
     * <code>converter</code>.
     *
     * @param converter the converter to use to convert the data read from the file into an object
     * @param <T> the type of the object to read
     * @return an ObjectDataReader instance that constructs an object from the file with the specified converter
     */
    public <T> ObjectDataReader<T> createObjectDataReader(InputStreamInputConverter<T> converter) {
        return new InputStreamObjectDataReader<>(createFileDataSource(), converter);
    }

    /**
     * Creates an instance of {@link ObjectDataWriter} that can be passed directly in the
     * {@link DataProvider#storeObject(Object, ObjectDataWriter)} method. The object data writer will convert an object
     * by using the specified <code>converter</code> and writes the converted data into the file that was specified on
     * this FileClient.
     *
     * @param converter the converter to use to convert the object into data to write to the file
     * @param <T> the type of the object to write
     * @return an ObjectDataWriter instance that writes an object into the file with the specified converter
     */
    public <T> ObjectDataWriter<T> createObjectDataWriter(OutputStreamOutputConverter<T> converter) {
        return new OutputStreamObjectDataWriter<>(createFileDataSource(), converter);
    }

    /**
     * Creates an instance of {@link ObjectDataRemover} that can be passed directly in the
     * {@link DataProvider#removeObject(GluonObservableObject, ObjectDataRemover)} method. The actual observable object
     * that is passed in with the removeObject method is ignored. The ObjectDataRemover will just remove the file that
     * was specified on this FileClient.
     *
     * @param <T> the type of the object to remove
     * @return an ObjectDataRemover instance that removes the file
     */
    public <T> ObjectDataRemover<T> createObjectDataRemover() {
        return observable -> {
            if (file.exists() && file.isFile()) {
                if (!file.delete()) {
                    throw new IOException("Failed to delete file: " + file);
                }
            }

            return Optional.empty();
        };
    }

    /**
     * Creates an instance of {@link ListDataReader} that can be passed directly in the
     * {@link DataProvider#retrieveList(ListDataReader)} method. The list data reader will read the data from the file
     * that was specified on this FileClient and convert it into a list of objects by using the specified
     * <code>converter</code>.
     *
     * @param converter the converter to use to convert the data read from the file into a list of objects
     * @param <T> the type of the objects contained in the list to read
     * @return a ListDataReader instance that constructs a list of objects from the file with the specified converter
     */
    public <T> ListDataReader<T> createListDataReader(InputStreamIterableInputConverter<T> converter) {
        return new InputStreamListDataReader<>(createFileDataSource(), converter);
    }
}
