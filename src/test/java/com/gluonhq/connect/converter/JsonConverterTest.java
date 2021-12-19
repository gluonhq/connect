/*
 * Copyright (c) 2016, 2018 Gluon
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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.testng.annotations.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class JsonConverterTest {

    @Test
    public void readEnumField() {
        JsonObject jsonObject = Json.createObjectBuilder().add("enumType", ClassWithEnumField.EnumType.C.name()).build();

        JsonConverter<ClassWithEnumField> jsonConverter = new JsonConverter<>(ClassWithEnumField.class);
        ClassWithEnumField object = jsonConverter.readFromJson(jsonObject);
        assertEquals(object.getEnumType(), ClassWithEnumField.EnumType.C);
    }

    @Test
    public void writeEnumField() {
        ClassWithEnumField object = new ClassWithEnumField();
        object.setEnumType(ClassWithEnumField.EnumType.A);

        JsonConverter<ClassWithEnumField> jsonConverter = new JsonConverter<>(ClassWithEnumField.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("enumType"));
        assertEquals(jsonObject.getString("enumType"), ClassWithEnumField.EnumType.A.name());
    }

    @Test
    public void readListField() {
        JsonObject jsonObject = Json.createObjectBuilder().add("list", Json.createArrayBuilder().add("string1").add("string2").add("string3")).build();

        JsonConverter<ClassWithListField> jsonConverter = new JsonConverter<>(ClassWithListField.class);
        ClassWithListField object = jsonConverter.readFromJson(jsonObject);
        assertEquals(object.getList().size(), 3);
        assertEquals(object.getList().get(0), "string1");
        assertEquals(object.getList().get(1), "string2");
        assertEquals(object.getList().get(2), "string3");
    }

    @Test
    public void writeListField() {
        ClassWithListField object = new ClassWithListField();
        object.setList(Arrays.asList("string1", "string2", "string3"));

        JsonConverter<ClassWithListField> jsonConverter = new JsonConverter<>(ClassWithListField.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("list"));
        assertEquals(jsonObject.getJsonArray("list").size(), 3);
        assertEquals(jsonObject.getJsonArray("list").getString(0), "string1");
        assertEquals(jsonObject.getJsonArray("list").getString(1), "string2");
        assertEquals(jsonObject.getJsonArray("list").getString(2), "string3");
    }

    @Test
    public void readObservableListField() {
        JsonObject jsonObject = Json.createObjectBuilder().add("list", Json.createArrayBuilder().add("string1").add("string2").add("string3")).build();

        JsonConverter<ClassWithObservableListField> jsonConverter = new JsonConverter<>(ClassWithObservableListField.class);
        ClassWithObservableListField object = jsonConverter.readFromJson(jsonObject);
        assertEquals(object.getList().size(), 3);
        assertEquals(object.getList().get(0), "string1");
        assertEquals(object.getList().get(1), "string2");
        assertEquals(object.getList().get(2), "string3");
    }

    @Test
    public void writeObservableListField() {
        ClassWithObservableListField object = new ClassWithObservableListField();
        object.setList(FXCollections.observableArrayList("string1", "string2", "string3"));

        JsonConverter<ClassWithObservableListField> jsonConverter = new JsonConverter<>(ClassWithObservableListField.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("list"));
        assertEquals(jsonObject.getJsonArray("list").size(), 3);
        assertEquals(jsonObject.getJsonArray("list").getString(0), "string1");
        assertEquals(jsonObject.getJsonArray("list").getString(1), "string2");
        assertEquals(jsonObject.getJsonArray("list").getString(2), "string3");
    }

    @Test
    public void readMapFields() {
        JsonObject jsonObject = Json.createObjectBuilder()
                .add("stringMap", Json.createObjectBuilder().add("entry", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "string1key").add("value", "string1value")).add(Json.createObjectBuilder().add("key", "string2key").add("value", "string2value")).add(Json.createObjectBuilder().add("key", "string3key").add("value", "string3value"))))
                .add("integerMap", Json.createObjectBuilder().add("entry", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "int1key").add("value", 1)).add(Json.createObjectBuilder().add("key", "int2key").add("value", 2)).add(Json.createObjectBuilder().add("key", "int3key").add("value", 3))))
                .add("doubleMap", Json.createObjectBuilder().add("entry", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "dbl1key").add("value", 1.0)).add(Json.createObjectBuilder().add("key", "dbl2key").add("value", 2.1)).add(Json.createObjectBuilder().add("key", "dbl3key").add("value", 3.91))))
                .add("booleanMap", Json.createObjectBuilder().add("entry", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "bool1key").add("value", true)).add(Json.createObjectBuilder().add("key", "bool2key").add("value", false)).add(Json.createObjectBuilder().add("key", "bool3key").add("value", true))))
                .add("objectMap", Json.createObjectBuilder().add("entry", Json.createArrayBuilder().add(Json.createObjectBuilder().add("key", "object1key").add("value", Json.createObjectBuilder().add("string", "string1").add("number", 1))).add(Json.createObjectBuilder().add("key", "object2key").add("value", Json.createObjectBuilder().add("string", "string2").add("number", 2))).add(Json.createObjectBuilder().add("key", "object3key").add("value", Json.createObjectBuilder().add("string", "string3").add("number", 3)))))
                .build();

        JsonConverter<ClassWithMapFields> jsonConverter = new JsonConverter<>(ClassWithMapFields.class);
        ClassWithMapFields object = jsonConverter.readFromJson(jsonObject);
        assertEquals(object.getStringMap().size(), 3);
        assertEquals(object.getStringMap().get("string1key"), "string1value");
        assertEquals(object.getStringMap().get("string2key"), "string2value");
        assertEquals(object.getStringMap().get("string3key"), "string3value");
        assertEquals(object.getIntegerMap().size(), 3);
        assertEquals(object.getIntegerMap().get("int1key"), Integer.valueOf(1));
        assertEquals(object.getIntegerMap().get("int2key"), Integer.valueOf(2));
        assertEquals(object.getIntegerMap().get("int3key"), Integer.valueOf(3));
        assertEquals(object.getDoubleMap().size(), 3);
        assertEquals(object.getDoubleMap().get("dbl1key").doubleValue(), 1.0);
        assertEquals(object.getDoubleMap().get("dbl2key").doubleValue(), 2.1);
        assertEquals(object.getDoubleMap().get("dbl3key").doubleValue(), 3.91);
        assertEquals(object.getBooleanMap().size(), 3);
        assertEquals(object.getBooleanMap().get("bool1key"), Boolean.TRUE);
        assertEquals(object.getBooleanMap().get("bool2key"), Boolean.FALSE);
        assertEquals(object.getBooleanMap().get("bool3key"), Boolean.TRUE);
        assertEquals(object.getObjectMap().size(), 3);
        assertEquals(object.getObjectMap().get("object1key"), new SimpleClass("string1", 1));
        assertEquals(object.getObjectMap().get("object2key"), new SimpleClass("string2", 2));
        assertEquals(object.getObjectMap().get("object3key"), new SimpleClass("string3", 3));
    }

    @Test
    public void writeMapFields() {
        ClassWithMapFields object = new ClassWithMapFields();
        object.setStringMap(Collections.unmodifiableMap(Stream
                .of(new SimpleEntry<>("string1key", "string1value"),
                        new SimpleEntry<>("string2key", "string2value"),
                        new SimpleEntry<>("string3key", "string3value"))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        object.setIntegerMap(Collections.unmodifiableMap(Stream
                .of(new SimpleEntry<>("int1key", 1),
                        new SimpleEntry<>("int2key", 2),
                        new SimpleEntry<>("int3key", 3))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        object.setDoubleMap(Collections.unmodifiableMap(Stream
                .of(new SimpleEntry<>("dbl1key", 1.0),
                        new SimpleEntry<>("dbl2key", 2.1),
                        new SimpleEntry<>("dbl3key", 3.91))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        object.setBooleanMap(Collections.unmodifiableMap(Stream
                .of(new SimpleEntry<>("bool1key", true),
                        new SimpleEntry<>("bool2key", false),
                        new SimpleEntry<>("bool3key", true))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));
        object.setObjectMap(Collections.unmodifiableMap(Stream
                .of(new SimpleEntry<>("object1key", new SimpleClass("string1", 1)),
                        new SimpleEntry<>("object2key", new SimpleClass("string2", 2)),
                        new SimpleEntry<>("object3key", new SimpleClass("string3", 3)))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue))));

        JsonConverter<ClassWithMapFields> jsonConverter = new JsonConverter<>(ClassWithMapFields.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("stringMap"));
        assertEquals(jsonObject.getJsonObject("stringMap").size(), 1);
        assertEquals(jsonObject.getJsonObject("stringMap").getJsonArray("entry").size(), 3);
        assertEqualsAnyOf(jsonObject.getJsonObject("stringMap").getJsonArray("entry").getJsonObject(0).getString("key"), Arrays.asList("string1key", "string2key", "string3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("stringMap").getJsonArray("entry").getJsonObject(1).getString("key"), Arrays.asList("string1key", "string2key", "string3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("stringMap").getJsonArray("entry").getJsonObject(2).getString("key"), Arrays.asList("string1key", "string2key", "string3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("stringMap").getJsonArray("entry").getJsonObject(0).getString("value"), Arrays.asList("string1value", "string2value", "string3value"));
        assertEqualsAnyOf(jsonObject.getJsonObject("stringMap").getJsonArray("entry").getJsonObject(1).getString("value"), Arrays.asList("string1value", "string2value", "string3value"));
        assertEqualsAnyOf(jsonObject.getJsonObject("stringMap").getJsonArray("entry").getJsonObject(2).getString("value"), Arrays.asList("string1value", "string2value", "string3value"));
        assertTrue(jsonObject.containsKey("integerMap"));
        assertEquals(jsonObject.getJsonObject("integerMap").size(), 1);
        assertEquals(jsonObject.getJsonObject("integerMap").getJsonArray("entry").size(), 3);
        assertEqualsAnyOf(jsonObject.getJsonObject("integerMap").getJsonArray("entry").getJsonObject(0).getString("key"), Arrays.asList("int1key", "int2key", "int3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("integerMap").getJsonArray("entry").getJsonObject(1).getString("key"), Arrays.asList("int1key", "int2key", "int3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("integerMap").getJsonArray("entry").getJsonObject(2).getString("key"), Arrays.asList("int1key", "int2key", "int3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("integerMap").getJsonArray("entry").getJsonObject(0).getJsonNumber("value").intValue(), Arrays.asList(1, 2, 3));
        assertEqualsAnyOf(jsonObject.getJsonObject("integerMap").getJsonArray("entry").getJsonObject(1).getJsonNumber("value").intValue(), Arrays.asList(1, 2, 3));
        assertEqualsAnyOf(jsonObject.getJsonObject("integerMap").getJsonArray("entry").getJsonObject(2).getJsonNumber("value").intValue(), Arrays.asList(1, 2, 3));
        assertTrue(jsonObject.containsKey("doubleMap"));
        assertEquals(jsonObject.getJsonObject("doubleMap").size(), 1);
        assertEquals(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").size(), 3);
        assertEqualsAnyOf(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").getJsonObject(0).getString("key"), Arrays.asList("dbl1key", "dbl2key", "dbl3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").getJsonObject(1).getString("key"), Arrays.asList("dbl1key", "dbl2key", "dbl3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").getJsonObject(2).getString("key"), Arrays.asList("dbl1key", "dbl2key", "dbl3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").getJsonObject(0).getJsonNumber("value").doubleValue(), Arrays.asList(1.0, 2.1, 3.91));
        assertEqualsAnyOf(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").getJsonObject(1).getJsonNumber("value").doubleValue(), Arrays.asList(1.0, 2.1, 3.91));
        assertEqualsAnyOf(jsonObject.getJsonObject("doubleMap").getJsonArray("entry").getJsonObject(2).getJsonNumber("value").doubleValue(), Arrays.asList(1.0, 2.1, 3.91));
        assertTrue(jsonObject.containsKey("booleanMap"));
        assertEquals(jsonObject.getJsonObject("booleanMap").size(), 1);
        assertEquals(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").size(), 3);
        assertEqualsAnyOf(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").getJsonObject(0).getString("key"), Arrays.asList("bool1key", "bool2key", "bool3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").getJsonObject(1).getString("key"), Arrays.asList("bool1key", "bool2key", "bool3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").getJsonObject(2).getString("key"), Arrays.asList("bool1key", "bool2key", "bool3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").getJsonObject(0).get("value"), Arrays.asList(JsonValue.FALSE, JsonValue.TRUE));
        assertEqualsAnyOf(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").getJsonObject(1).get("value"), Arrays.asList(JsonValue.FALSE, JsonValue.TRUE));
        assertEqualsAnyOf(jsonObject.getJsonObject("booleanMap").getJsonArray("entry").getJsonObject(2).get("value"), Arrays.asList(JsonValue.FALSE, JsonValue.TRUE));
        assertTrue(jsonObject.containsKey("objectMap"));
        assertEquals(jsonObject.getJsonObject("objectMap").size(), 1);
        assertEquals(jsonObject.getJsonObject("objectMap").getJsonArray("entry").size(), 3);
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(0).getString("key"), Arrays.asList("object1key", "object2key", "object3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(1).getString("key"), Arrays.asList("object1key", "object2key", "object3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(2).getString("key"), Arrays.asList("object1key", "object2key", "object3key"));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(0).getJsonObject("value").getString("string"), Arrays.asList("string1", "string2", "string3"));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(0).getJsonObject("value").getJsonNumber("number").intValue(), Arrays.asList(1, 2, 3));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(1).getJsonObject("value").getString("string"), Arrays.asList("string1", "string2", "string3"));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(1).getJsonObject("value").getJsonNumber("number").intValue(), Arrays.asList(1, 2, 3));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(2).getJsonObject("value").getString("string"), Arrays.asList("string1", "string2", "string3"));
        assertEqualsAnyOf(jsonObject.getJsonObject("objectMap").getJsonArray("entry").getJsonObject(2).getJsonObject("value").getJsonNumber("number").intValue(), Arrays.asList(1, 2, 3));
    }

    @Test
    public void writePrimitiveWrapperClassFields() {
        SimpleClassWithPrimitiveWrappers object = new SimpleClassWithPrimitiveWrappers();
        object.setABoolean(Boolean.TRUE);
        object.setAByte((byte) 0xa);
        object.setADouble(1.8d);
        object.setAFloat(-0.00313f);
        object.setAnInteger(10298318);
        object.setALong(831931831193157L);
        object.setAShort((short) -12723);

        JsonConverter<SimpleClassWithPrimitiveWrappers> jsonConverter = new JsonConverter<>(SimpleClassWithPrimitiveWrappers.class);
        JsonObject jsonObject = jsonConverter.writeToJson(object);
        assertTrue(jsonObject.containsKey("aBoolean"));
        assertEquals(Boolean.valueOf(jsonObject.getBoolean("aBoolean")), object.getABoolean());
        assertEquals(Byte.valueOf((byte) jsonObject.getJsonNumber("aByte").intValue()), object.getAByte());
        assertEquals(jsonObject.getJsonNumber("aDouble").doubleValue(), object.getADouble().doubleValue());
        assertEquals(Double.valueOf(jsonObject.getJsonNumber("aFloat").doubleValue()).floatValue(), object.getAFloat().floatValue());
        assertEquals(Integer.valueOf(jsonObject.getInt("anInteger")), object.getAnInteger());
        assertEquals(Long.valueOf(jsonObject.getJsonNumber("aLong").longValue()), object.getALong());
        assertEquals(Short.valueOf((short) jsonObject.getInt("aShort")), object.getAShort());
    }

    public static final class ClassWithEnumField {

        private EnumType enumType;

        public EnumType getEnumType() {
            return enumType;
        }

        public void setEnumType(EnumType enumType) {
            this.enumType = enumType;
        }

        private enum EnumType {
            A, B, C
        }
    }

    public static final class ClassWithListField {

        private List<String> list;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }

    public static final class ClassWithObservableListField {

        private ObservableList<String> list;

        public ObservableList<String> getList() {
            return list;
        }

        public void setList(ObservableList<String> list) {
            this.list = list;
        }
    }

    public static final class ClassWithMapFields {

        private Map<String, String> stringMap;
        private Map<String, Integer> integerMap;
        private Map<String, Double> doubleMap;
        private Map<String, Boolean> booleanMap;
        private Map<String, SimpleClass> objectMap;

        public Map<String, String> getStringMap() {
            return stringMap;
        }

        public void setStringMap(Map<String, String> stringMap) {
            this.stringMap = stringMap;
        }

        public Map<String, Integer> getIntegerMap() {
            return integerMap;
        }

        public void setIntegerMap(Map<String, Integer> integerMap) {
            this.integerMap = integerMap;
        }

        public Map<String, Double> getDoubleMap() {
            return doubleMap;
        }

        public void setDoubleMap(Map<String, Double> doubleMap) {
            this.doubleMap = doubleMap;
        }

        public Map<String, Boolean> getBooleanMap() {
            return booleanMap;
        }

        public void setBooleanMap(Map<String, Boolean> booleanMap) {
            this.booleanMap = booleanMap;
        }

        public Map<String, SimpleClass> getObjectMap() {
            return objectMap;
        }

        public void setObjectMap(Map<String, SimpleClass> objectMap) {
            this.objectMap = objectMap;
        }
    }

    public static final class SimpleClass {
        private String string;
        private int number;

        public SimpleClass() {
        }

        public SimpleClass(String string, int number) {
            this.string = string;
            this.number = number;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleClass that = (SimpleClass) o;

            if (number != that.number) return false;
            return string.equals(that.string);
        }

        @Override
        public int hashCode() {
            int result = string.hashCode();
            result = 31 * result + number;
            return result;
        }
    }

    public static final class SimpleClassWithPrimitiveWrappers {
        private Boolean aBoolean;
        private Byte aByte;
        private Double aDouble;
        private Float aFloat;
        private Integer anInteger;
        private Long aLong;
        private Short aShort;

        public Boolean getABoolean() {
            return aBoolean;
        }

        public void setABoolean(Boolean aBoolean) {
            this.aBoolean = aBoolean;
        }

        public Byte getAByte() {
            return aByte;
        }

        public void setAByte(Byte aByte) {
            this.aByte = aByte;
        }

        public Double getADouble() {
            return aDouble;
        }

        public void setADouble(Double aDouble) {
            this.aDouble = aDouble;
        }

        public Float getAFloat() {
            return aFloat;
        }

        public void setAFloat(Float aFloat) {
            this.aFloat = aFloat;
        }

        public Integer getAnInteger() {
            return anInteger;
        }

        public void setAnInteger(Integer anInteger) {
            this.anInteger = anInteger;
        }

        public Long getALong() {
            return aLong;
        }

        public void setALong(Long aLong) {
            this.aLong = aLong;
        }

        public Short getAShort() {
            return aShort;
        }

        public void setAShort(Short aShort) {
            this.aShort = aShort;
        }
    }

    private static <T> void assertEqualsAnyOf(T key, List<T> values) {
        assertTrue(values.contains(key));
    }
}
