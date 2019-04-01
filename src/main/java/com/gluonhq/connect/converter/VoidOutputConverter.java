/*
 * Copyright (c) 2017 Gluon
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
package com.gluonhq.connect.converter;

import java.io.IOException;
import java.io.OutputStream;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import com.gluonhq.connect.Level;
import com.gluonhq.connect.Logger;

/**
 * An OutputConverter that doesn't write anything to an OutputStream.
 */
public class VoidOutputConverter extends OutputStreamOutputConverter<Void> {

    private static final Logger LOGGER = Logger.getLogger(StringOutputConverter.class.getName());

    /**
     * Just closes the provided OutputStream.
     *
     * @param v the void instance, which is always <code>null</code>.
     */
    @Override
    public void write(Void v) {
        try (OutputStream outputStream = getOutputStream()) {
            outputStream.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Something went wrong while closing OutputStream.", ex);
        }
    }
}
