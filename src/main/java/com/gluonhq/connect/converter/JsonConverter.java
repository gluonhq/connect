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

import com.gluonhq.impl.connect.converter.ClassInspector;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class to convert Java objects from JSON Objects and from JSON Objects into Java objects.
 *
 * @param <T> the type of the object to convert from and into a JSON Object
 */
public class JsonConverter<T> {

    private static final Logger LOGGER = Logger.getLogger(JsonConverter.class.getName());

    private static final JsonBuilderFactory builderFactory = Json.createBuilderFactory(null);

    private final Class<T> targetClass;
    private final ClassInspector<T> inspector;

    /**
     * Construct a JsonConverter to convert between JSON and objects of the specified <code>targetClass</code>.
     *
     * @param targetClass The target class defining the objects being converted from and into JSON Objects.
     */
    public JsonConverter(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.inspector = ClassInspector.resolve(targetClass);
    }

    /**
     * Returns the target class that defines the objects being converted from and into JSON objects.
     *
     * @return The target class.
     */
    public Class<T> getTargetClass() {
        return targetClass;
    }

    /**
     * Convert the provided JSON Object into a Java object. If a new instance could not be created from the specified
     * <code>targetClass</code> in the constructor, then <code>null</code> will be returned.
     *
     * <p>The conversion works by inspecting all the property methods of the target class. A property method is any
     * field that has both a getter and a setter method. The name of the property is taken from the getter method, by
     * stripping the string "get" from the method name and converting the first character from upper case to lower case.
     * It's possible to override the property name by adding an {@literal @XmlElement} annotation. Then the name value
     * of the annotation will be used as the property name.</p>
     *
     * <p>The property name will then be looked up in the provided JSON Object. If a key was not found, the property
     * will be ignored. Otherwise, the setter method will be called with the value from the JSON Object that is mapped
     * to the key. The JsonConverter is able to convert from all types of JSON values, except for nested JSON Arrays.</p>
     *
     * @param json the instance of the JSON Object that needs to be converted into a Java object
     * @return The Java object that is converted from the provided JSON Object.
     */
    public T readFromJson(JsonObject json) {
        T t = null;

        try {
            if (! "java.util.Map".equals(targetClass.getName())) {
                t = targetClass.getDeclaredConstructor().newInstance();
            } else {
                LOGGER.log(Level.WARNING, "Map not yet supported");
            }

            Map<String, Method> settersMappedByPropertyName = this.inspector.getSetters();
            if (settersMappedByPropertyName != null) {
                for (String property : settersMappedByPropertyName.keySet()) {
                    if (!json.containsKey(property)) {
                        LOGGER.log(Level.FINEST, "Property " + property + " not defined on json object for class " + targetClass + ".");
                        continue;
                    }

                    if (json.containsKey(property)) {
                        Method setter = settersMappedByPropertyName.get(property);
                        Class<?> parameterType = setter.getParameterTypes()[0];

                        Object[] args = new Object[1];
                        JsonValue jsonValue = json.get(property);
                        switch (jsonValue.getValueType()) {
                            case NULL:
                                args[0] = null;
                                break;
                            case FALSE:
                                args[0] = Boolean.FALSE;
                                break;
                            case TRUE:
                                args[0] = Boolean.TRUE;
                                break;
                            case STRING:
                                JsonString stringProperty = (JsonString) jsonValue;
                                if (parameterType.isEnum()) {
                                    args[0] = Enum.valueOf(parameterType.asSubclass(Enum.class), stringProperty.getString());
                                } else {
                                    args[0] = stringProperty.getString();
                                }
                                break;
                            case NUMBER:
                                JsonNumber numberProperty = (JsonNumber) jsonValue;
                                Class setterParameterType = setter.getParameterTypes()[0];
                                if (!setterParameterType.isArray()) {
                                    String setterParameterTypeName = setterParameterType.getName();
                                    switch (setterParameterTypeName) {
                                        case "byte":
                                        case "java.lang.Byte":
                                        case "int":
                                        case "java.lang.Integer":
                                        case "short":
                                        case "java.lang.Short":
                                            args[0] = numberProperty.intValue();
                                            break;
                                        case "long":
                                        case "java.lang.Long":
                                            args[0] = numberProperty.longValue();
                                            break;
                                        case "double":
                                        case "java.lang.Double":
                                            args[0] = numberProperty.doubleValue();
                                            break;
                                        case "float":
                                        case "java.lang.Float":
                                            args[0] = (float) numberProperty.doubleValue();
                                            break;
                                        case "java.lang.String":
                                        case "javafx.beans.property.StringProperty":
                                            args[0] = numberProperty.toString();
                                            break;
                                        case "java.math.BigDecimal":
                                            args[0] = numberProperty.bigDecimalValue();
                                            break;
                                        case "java.math.BigInteger":
                                            args[0] = numberProperty.bigIntegerValue();
                                            break;
                                    }
                                }
                                break;
                            case ARRAY:
                                JsonArray arrayProperty = (JsonArray) jsonValue;
                                List<Object> values;
                                if (ObservableList.class.isAssignableFrom(parameterType)) {
                                    values = FXCollections.observableArrayList();
                                } else {
                                    values = new ArrayList<>();
                                }
                                for (JsonValue arrayValue : arrayProperty) {
                                    switch (arrayValue.getValueType()) {
                                        case NULL:
                                            values.add(null);
                                            break;
                                        case FALSE:
                                            values.add(Boolean.FALSE);
                                            break;
                                        case TRUE:
                                            values.add(Boolean.TRUE);
                                            break;
                                        case STRING:
                                            JsonString stringArrayValue = (JsonString) arrayValue;
                                            values.add(stringArrayValue.getString());
                                            break;
                                        case NUMBER: {
                                            ParameterizedType listType = (ParameterizedType) setter.getGenericParameterTypes()[0];
                                            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                                            JsonNumber numberArrayValue = (JsonNumber) arrayValue;
                                            switch (listClass.getName()) {
                                                case "byte":
                                                case "java.lang.Byte":
                                                case "int":
                                                case "java.lang.Integer":
                                                case "short":
                                                case "java.lang.Short":
                                                    values.add(numberArrayValue.intValue());
                                                    break;
                                                case "long":
                                                case "java.lang.Long":
                                                    values.add(numberArrayValue.longValue());
                                                    break;
                                                case "double":
                                                case "java.lang.Double":
                                                    values.add(numberArrayValue.doubleValue());
                                                    break;
                                                case "float":
                                                case "java.lang.Float":
                                                    values.add((float) numberArrayValue.doubleValue());
                                                    break;
                                                case "java.lang.String":
                                                case "javafx.beans.property.StringProperty":
                                                    values.add(numberArrayValue.toString());
                                                    break;
                                                case "java.math.BigDecimal":
                                                    values.add(numberArrayValue.bigDecimalValue());
                                                    break;
                                                case "java.math.BigInteger":
                                                    values.add(numberArrayValue.bigIntegerValue());
                                                    break;
                                            }
                                            if (numberArrayValue.isIntegral()) {
                                                values.add(numberArrayValue.longValue());
                                            } else {
                                                values.add(numberArrayValue.doubleValue());
                                            }
                                            break;
                                        }
                                        case ARRAY:
                                            // TODO: implement nested arrays in arrays
                                            LOGGER.log(Level.WARNING, "Arrays within arrays not yet supported.");
                                            break;
                                        case OBJECT: {
                                            ParameterizedType listType = (ParameterizedType) setter.getGenericParameterTypes()[0];
                                            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                                            JsonConverter<?> jsonConverter = new JsonConverter<>(listClass);
                                            values.add(jsonConverter.readFromJson((JsonObject) arrayValue));
                                            break;
                                        }
                                    }
                                }
                                args[0] = values;
                                break;
                            case OBJECT:
                                JsonObject jsonObject = (JsonObject) jsonValue;
                                if (Map.class.isAssignableFrom(parameterType) && jsonObject.containsKey("entry")) {
                                    Map<String, Object> map = new HashMap<>();
                                    JsonValue jsonEntry = jsonObject.get("entry");
                                    if (jsonEntry.getValueType() == JsonValue.ValueType.ARRAY) {
                                        JsonArray jsonEntryArray = (JsonArray) jsonEntry;
                                        for (int i = 0; i < jsonEntryArray.size(); i++) {
                                            JsonValue jsonEntryValue = jsonEntryArray.get(i);
                                            if (jsonEntryValue.getValueType() == JsonValue.ValueType.OBJECT) {
                                                JsonObject jsonEntryValueObject = (JsonObject) jsonEntryValue;
                                                if (jsonEntryValueObject.containsKey("key") && jsonEntryValueObject.containsKey("value")) {
                                                    String key = jsonEntryValueObject.getString("key");
                                                    JsonValue value = jsonEntryValueObject.get("value");
                                                    switch (value.getValueType()) {
                                                        case NULL:
                                                            map.put(key, null);
                                                            break;
                                                        case FALSE:
                                                            map.put(key, Boolean.FALSE);
                                                            break;
                                                        case TRUE:
                                                            map.put(key, Boolean.TRUE);
                                                            break;
                                                        case STRING:
                                                            map.put(key, ((JsonString) value).getString());
                                                            break;
                                                        case NUMBER: {
                                                            JsonNumber valueNumber = (JsonNumber) value;
                                                            ParameterizedType mapType = (ParameterizedType) setter.getGenericParameterTypes()[0];
                                                            Class<?> mapValueClass = (Class<?>) mapType.getActualTypeArguments()[1];
                                                            String setterParameterTypeName = mapValueClass.getName();
                                                            switch (setterParameterTypeName) {
                                                                case "byte":
                                                                case "java.lang.Byte":
                                                                case "int":
                                                                case "java.lang.Integer":
                                                                case "short":
                                                                case "java.lang.Short":
                                                                    map.put(key, valueNumber.intValue());
                                                                    break;
                                                                case "long":
                                                                case "java.lang.Long":
                                                                    map.put(key, valueNumber.longValue());
                                                                    break;
                                                                case "double":
                                                                case "java.lang.Double":
                                                                    map.put(key, valueNumber.doubleValue());
                                                                    break;
                                                                case "float":
                                                                case "java.lang.Float":
                                                                    map.put(key, (float) valueNumber.doubleValue());
                                                                    break;
                                                                case "java.lang.String":
                                                                case "javafx.beans.property.StringProperty":
                                                                    map.put(key, valueNumber.toString());
                                                                    break;
                                                                case "java.math.BigDecimal":
                                                                    map.put(key, valueNumber.bigDecimalValue());
                                                                    break;
                                                                case "java.math.BigInteger":
                                                                    map.put(key, valueNumber.bigIntegerValue());
                                                                    break;
                                                            }
                                                            break;
                                                        }
                                                        case ARRAY:
                                                            // TODO: implement nested arrays in maps
                                                            LOGGER.log(Level.WARNING, "Arrays within maps not yet supported.");
                                                            break;
                                                        case OBJECT: {
                                                            ParameterizedType mapType = (ParameterizedType) setter.getGenericParameterTypes()[0];
                                                            Class<?> mapValueClass = (Class<?>) mapType.getActualTypeArguments()[1];
                                                            JsonConverter<?> jsonConverter = new JsonConverter<>(mapValueClass);
                                                            map.put(key, jsonConverter.readFromJson((JsonObject) value));
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        JsonConverter<?> jsonConverter = new JsonConverter<>(parameterType);
                                        args[0] = jsonConverter.readFromJson(jsonObject);
                                    }
                                    args[0] = map;
                                } else {
                                    JsonConverter<?> jsonConverter = new JsonConverter<>(parameterType);
                                    args[0] = jsonConverter.readFromJson(jsonObject);
                                }
                                break;
                        }

                        try {
                            setter.invoke(t, args);
                        } catch (IllegalArgumentException | InvocationTargetException ex) {
                            LOGGER.log(Level.WARNING, "Failed to call setter " + setter + " with value " + property, ex);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to create object of type " + targetClass + " from the following json object " + json, ex);
        }

        return t;
    }

    /**
     * Convert the provided Java object into a JSON Object.
     *
     * <p>The conversion works by inspecting all the property methods of the target class. A property method is any
     * field that has both a getter and a setter method. The name of the property is taken from the getter method, by
     * stripping the string "get" from the method name and converting the first character from upper case to lower case.
     * It's possible to override the property name by adding an {@literal @XmlElement} annotation. Then the name value
     * of the annotation will be used as the property name.</p>
     *
     * <p>The property name will then be used as the key inside the JSON Object. Where the value will be the value that
     * was returned by calling the getter method on the provided Java object.</p>
     *
     * <p>As the return type of the getter method, all primitive java types are supported as well as the basic JavaFX
     * property objects (like BooleanProperty, IntegerProperty, etc...). {@link java.util.List Lists} are supported as
     * well and will be converted into a JSON Array. If the getter returns any other type, then the returned value will
     * be converted into a JSON Object as well by using a JsonConverter.</p>
     *
     * @param t the Java object to convert into a JSON Object
     * @return The JSON Object that was converted from the provided Java object.
     */
    public JsonObject writeToJson(T t) {
        JsonObjectBuilder jsonObjectBuilder = builderFactory.createObjectBuilder();
        Map<String, Method> getters = inspector.getGetters();
        if (getters != null) {
            for (String property : getters.keySet()) {
                Method getter = getters.get(property);

                try {
                    writeProperty(jsonObjectBuilder, property, getter, t);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    LOGGER.log(Level.WARNING, "Failed to call getter " + getter + " on object " + t, ex);
                }
            }
        }
        return jsonObjectBuilder.build();
    }

    private void writeProperty(JsonObjectBuilder jsonObjectBuilder, String property, Method method, T target) throws IllegalAccessException, InvocationTargetException {
        Object value = method.invoke(target);

        if (boolean.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (boolean) value);
        } else if (Boolean.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (Boolean) value);
            }
        } else if (byte.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (byte) value);
        } else if (Byte.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (byte) value);
            }
        } else if (double.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (double) value);
        } else if (Double.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (double) value);
            }
        } else if (float.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (float) value);
        } else if (Float.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (float) value);
            }
        } else if (int.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (int) value);
        } else if (Integer.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (int) value);
            }
        } else if (long.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (long) value);
        } else if (Long.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (Long) value);
            }
        } else if (short.class.equals(method.getReturnType())) {
            jsonObjectBuilder.add(property, (short) value);
        } else if (Short.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (short) value);
            }
        } else if (String.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (String) value);
            }
        } else if (BigDecimal.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (BigDecimal) value);
            }
        } else if (BigInteger.class.equals(method.getReturnType())) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, (BigInteger) value);
            }
        } else if (List.class.isAssignableFrom(method.getReturnType())) {
            List list = (List) value;
            if (list != null) {
                ParameterizedType listType = (ParameterizedType) method.getGenericReturnType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

                JsonArrayBuilder jsonArrayBuilder = builderFactory.createArrayBuilder();
                for (Object item : list) {
                    writeProperty(jsonArrayBuilder, listClass, item);
                }

                jsonObjectBuilder.add(property, jsonArrayBuilder);
            } else {
                jsonObjectBuilder.addNull(property);
            }
        } else if (ObservableList.class.isAssignableFrom(method.getReturnType())) {
            ObservableList list = (ObservableList) value;
            if (list != null) {
                ParameterizedType listType = (ParameterizedType) method.getGenericReturnType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];

                JsonArrayBuilder jsonArrayBuilder = builderFactory.createArrayBuilder();
                for (Object item : list) {
                    writeProperty(jsonArrayBuilder, listClass, item);
                }

                jsonObjectBuilder.add(property, jsonArrayBuilder);
            } else {
                jsonObjectBuilder.addNull(property);
            }
        } else if (Map.class.isAssignableFrom(method.getReturnType())) {
            Map map = (Map) value;
            if (map != null) {
                ParameterizedType mapType = (ParameterizedType) method.getGenericReturnType();
                Class<?> mapValueClass = (Class<?>) mapType.getActualTypeArguments()[1];

                JsonArrayBuilder jsonArrayBuilder = builderFactory.createArrayBuilder();
                for (Object key : map.keySet()) {
                    JsonObjectBuilder entryJsonObjectBuilder = builderFactory.createObjectBuilder()
                            .add("key", key.toString());
                    writeProperty(entryJsonObjectBuilder, "value", mapValueClass, map.get(key));
                    jsonArrayBuilder.add(entryJsonObjectBuilder);
                }
                jsonObjectBuilder.add(property, builderFactory.createObjectBuilder().add("entry", jsonArrayBuilder));
            } else {
                jsonObjectBuilder.addNull(property);
            }
        } else if (BooleanProperty.class.equals(method.getReturnType())) {
            BooleanProperty booleanProperty = (BooleanProperty) value;
            if (booleanProperty != null) {
                jsonObjectBuilder.add(property, booleanProperty.get());
            }
        } else if (DoubleProperty.class.equals(method.getReturnType())) {
            DoubleProperty doubleProperty = (DoubleProperty) value;
            if (doubleProperty != null) {
                jsonObjectBuilder.add(property, doubleProperty.get());
            }
        } else if (FloatProperty.class.equals(method.getReturnType())) {
            FloatProperty floatProperty = (FloatProperty) value;
            if (floatProperty != null) {
                jsonObjectBuilder.add(property, floatProperty.get());
            }
        } else if (IntegerProperty.class.equals(method.getReturnType())) {
            IntegerProperty integerProperty = (IntegerProperty) value;
            if (integerProperty != null) {
                jsonObjectBuilder.add(property, integerProperty.get());
            }
        } else if (LongProperty.class.equals(method.getReturnType())) {
            LongProperty longProperty = (LongProperty) value;
            if (longProperty != null) {
                jsonObjectBuilder.add(property, longProperty.get());
            }
        } else if (StringProperty.class.equals(method.getReturnType())) {
            StringProperty stringProperty = (StringProperty) value;
            if (stringProperty != null) {
                String string = stringProperty.get();
                if (string != null) {
                    jsonObjectBuilder.add(property, string);
                } else {
                    jsonObjectBuilder.addNull(property);
                }
            }
        } else if (method.getReturnType().isEnum()) {
            if (value == null) {
                jsonObjectBuilder.addNull(property);
            } else {
                jsonObjectBuilder.add(property, ((Enum) value).name());
            }
        } else {
            if (value != null) {
                JsonConverter converter = new JsonConverter(method.getReturnType());
                jsonObjectBuilder.add(property, converter.writeToJson(value));
            } else {
                jsonObjectBuilder.addNull(property);
            }
        }
    }

    private void writeProperty(JsonArrayBuilder jsonArrayBuilder, Class<?> type, Object value) {
        if (value == null) {
            jsonArrayBuilder.addNull();
        } else if (Boolean.class.equals(type)) {
            jsonArrayBuilder.add((Boolean) value);
        } else if (Byte.class.equals(type)) {
            jsonArrayBuilder.add((Byte) value);
        } else if (Double.class.equals(type)) {
            jsonArrayBuilder.add((Double) value);
        } else if (Float.class.equals(type)) {
            jsonArrayBuilder.add((Float) value);
        } else if (Integer.class.equals(type)) {
            jsonArrayBuilder.add((Integer) value);
        } else if (Long.class.equals(type)) {
            jsonArrayBuilder.add((Long) value);
        } else if (Short.class.equals(type)) {
            jsonArrayBuilder.add((Short) value);
        } else if (String.class.equals(type)) {
            jsonArrayBuilder.add((String) value);
        } else if (BigDecimal.class.equals(type)) {
            jsonArrayBuilder.add((BigDecimal) value);
        } else if (BigInteger.class.equals(type)) {
            jsonArrayBuilder.add((BigInteger) value);
        } else {
            JsonConverter converter = new JsonConverter(type);
            jsonArrayBuilder.add(converter.writeToJson(value));
        }
    }

    private void writeProperty(JsonObjectBuilder jsonObjectBuilder, String key, Class<?> type, Object value) {
        if (value == null) {
            jsonObjectBuilder.addNull(key);
        } else if (Boolean.class.equals(type)) {
            jsonObjectBuilder.add(key, (Boolean) value);
        } else if (Byte.class.equals(type)) {
            jsonObjectBuilder.add(key, (Byte) value);
        } else if (Double.class.equals(type)) {
            jsonObjectBuilder.add(key, (Double) value);
        } else if (Float.class.equals(type)) {
            jsonObjectBuilder.add(key, (Float) value);
        } else if (Integer.class.equals(type)) {
            jsonObjectBuilder.add(key, (Integer) value);
        } else if (Long.class.equals(type)) {
            jsonObjectBuilder.add(key, (Long) value);
        } else if (Short.class.equals(type)) {
            jsonObjectBuilder.add(key, (Short) value);
        } else if (String.class.equals(type)) {
            jsonObjectBuilder.add(key, (String) value);
        } else if (BigDecimal.class.equals(type)) {
            jsonObjectBuilder.add(key, (BigDecimal) value);
        } else if (BigInteger.class.equals(type)) {
            jsonObjectBuilder.add(key, (BigInteger) value);
        } else {
            JsonConverter converter = new JsonConverter(type);
            jsonObjectBuilder.add(key, converter.writeToJson(value));
        }
    }
}
