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

import com.gluonhq.connect.provider.FileClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An implementation of {@link IODataSource} that can read from and write to a file.
 *
 * <p><b>Attention:</b> it is advised not to use this class directly, but rather construct it by creating a
 * {@link FileClient} and build the FileDataSource with the {@link FileClient#createFileDataSource()} method.
 */
public class FileDataSource implements IODataSource {

    private final File file;

    /**
     * Create a new FileDataSource instance. The provided file will be used for reading
     * from and/or storing data into.
     *
     * @param file the file to use for reading the data from from or writing the data to
     */
    public FileDataSource(File file) {
        this.file = file;
    }

    /**
     * Returns the file that this data source is mapped to.
     *
     * @return the file that this data source will use for reading and writing data
     */
    public File getFile() {
        return file;
    }

    /**
     * Returns an InputStream that is able to read data from the file that was passed in when
     * constructing the FileDataSource.
     *
     * @return an InputStream that is able to read from the file
     * @throws IOException when the InputStream on the file could not be created
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * Returns an OutputStream that is able to write data to the file that was passed in when
     * constructing the FileDataSource.
     *
     * @return an OutputStream that is able to write to the file
     * @throws IOException when the OutputStream on the file could not be created
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(file);
    }
}
