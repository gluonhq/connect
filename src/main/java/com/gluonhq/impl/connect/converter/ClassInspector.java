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
package com.gluonhq.impl.connect.converter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClassInspector<T> {

    private static final Map<Class<?>, ClassInspector<?>> inspectors = new HashMap<>();

    private final Map<Class<?>, Map<String, Method>> inspectedGetters = new HashMap<>();
    private final Map<Class<?>, Map<String, Method>> inspectedSetters = new HashMap<>();

    private final Class<T> targetClass;

    public synchronized static <T> ClassInspector<T> resolve(Class<T> targetClass) {
        if (inspectors.containsKey(targetClass)) {
            return (ClassInspector<T>) inspectors.get(targetClass);
        }

        ClassInspector<T> inspector = new ClassInspector<>(targetClass);
        inspectors.put(targetClass, inspector);
        return inspector;
    }

    private ClassInspector(Class<T> targetClass) {
        this.targetClass = targetClass;

        resolveProperties();
    }

    public Map<String, Method> getGetters() {
        return inspectedGetters.get(targetClass);
    }

    public Map<String, Method> getSetters() {
        return inspectedSetters.get(targetClass);
    }

    private void resolveProperties() {
        if (inspectedSetters.containsKey(targetClass)) {
            return;
        }
        Map<String, Method> gettersMappedByPropertyName = new HashMap<>();
        Map<String, Method> settersMappedByPropertyName = new HashMap<>();
        inspectedGetters.put(targetClass, gettersMappedByPropertyName);
        inspectedSetters.put(targetClass, settersMappedByPropertyName);

        Method[] methods = targetClass.getMethods();

        // sort methods array by method name, so getter methods are processed before setter methods
        Arrays.sort(methods, (m1, m2) -> m1.getName().compareTo(m2.getName()));

        Map<String, Method> getters = new HashMap<>();
        for (Method method : methods) {
            String methodName = method.getName();

            // check if the method is a getter:
            //   - is public
            //   - has no arguments
            //   - is not annotated with XmlTransient
            //   - starts with get and returns a non-void or starts with is and returns a boolean
            if (Modifier.isPublic(method.getModifiers()) &&
                    method.getParameterTypes().length == 0 &&
                    !method.isAnnotationPresent(XmlTransient.class) &&
                    ((methodName.matches("^get[A-Z].*") && !method.getReturnType().equals(void.class)) ||
                            (methodName.matches("^is[A-Z].*") && method.getReturnType().equals(boolean.class)))) {
                String bareMethodName = methodName.startsWith("get") ? methodName.substring(3) : methodName.substring(2);
                getters.put(bareMethodName, method);
            }

            // check if the method is a setter:
            //   - is public
            //   - has exactly one argument
            //   - is not annotated with XmlTransient
            //   - starts with set and returns void
            //   - has a matching getter method
            if (Modifier.isPublic(method.getModifiers()) &&
                    method.getParameterTypes().length == 1 &&
                    method.getReturnType().equals(void.class) &&
                    !method.isAnnotationPresent(XmlTransient.class) &&
                    methodName.matches("^set[A-Z].*")) {
                String bareMethodName = methodName.substring(3);
                Method getter = getters.get(bareMethodName);
                if (getter != null) {
                    String finalName = bareMethodName.substring(0, 1).toLowerCase(Locale.ROOT);
                    if (bareMethodName.length() > 1) {
                        finalName += bareMethodName.substring(1);
                    }

                    if (getter.isAnnotationPresent(XmlElement.class) || method.isAnnotationPresent(XmlElement.class)) {
                        XmlElement xmlElement = getter.isAnnotationPresent(XmlElement.class) ? getter.getAnnotation(XmlElement.class) : method.getAnnotation(XmlElement.class);
                        String annotatedName = xmlElement.name();
                        if (annotatedName != null && !annotatedName.isEmpty()) {
                            finalName = annotatedName;
                        }
                    }

                    gettersMappedByPropertyName.put(finalName, getter);
                    settersMappedByPropertyName.put(finalName, method);
                }
            }
        }
    }
}
