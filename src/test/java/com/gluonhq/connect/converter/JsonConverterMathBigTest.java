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

import org.testng.annotations.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JsonConverterMathBigTest {

    @Test
    public void readBigDecimalField() {
        JsonObject jsonObject = Json.createObjectBuilder().add("bigDecimal", new BigDecimal(-24.21021)).build();

        JsonConverter<ClassWithBigDecimalField> jsonConverter = new JsonConverter<>(ClassWithBigDecimalField.class);
        ClassWithBigDecimalField object = jsonConverter.readFromJson(jsonObject);
        assertEquals(object.getBigDecimal(), new BigDecimal(-24.21021));
    }

    @Test
    public void writeBigDecimalField() {
        ClassWithBigDecimalField object = new ClassWithBigDecimalField();
        object.setBigDecimal(new BigDecimal(547893.3137490854));

        JsonConverter<ClassWithBigDecimalField> jsonConverter = new JsonConverter<>(ClassWithBigDecimalField.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("bigDecimal"));
        assertEquals(jsonObject.getJsonNumber("bigDecimal").bigDecimalValue(), new BigDecimal(547893.3137490854));
    }

    @Test
    public void readBigIntegerField() {
        JsonObject jsonObject = Json.createObjectBuilder().add("bigInteger", new BigInteger("84132514497684153649768137548986169814")).build();

        JsonConverter<ClassWithBigIntegerField> jsonConverter = new JsonConverter<>(ClassWithBigIntegerField.class);
        ClassWithBigIntegerField object = jsonConverter.readFromJson(jsonObject);
        assertEquals(object.getBigInteger(), new BigInteger("84132514497684153649768137548986169814"));
    }

    @Test
    public void writeBigIntegerField() {
        ClassWithBigIntegerField object = new ClassWithBigIntegerField();
        object.setBigInteger(new BigInteger("57486575406257840165894267426524656554057289574"));

        JsonConverter<ClassWithBigIntegerField> jsonConverter = new JsonConverter<>(ClassWithBigIntegerField.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("bigInteger"));
        assertEquals(jsonObject.getJsonNumber("bigInteger").bigIntegerValue(), new BigInteger("57486575406257840165894267426524656554057289574"));
    }

    public static final class ClassWithBigDecimalField {

        private BigDecimal bigDecimal;

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }
    }

    public static final class ClassWithBigIntegerField {

        private BigInteger bigInteger;

        public BigInteger getBigInteger() {
            return bigInteger;
        }

        public void setBigInteger(BigInteger bigInteger) {
            this.bigInteger = bigInteger;
        }
    }
}
