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
package com.gluonhq.connect.converter;

import com.gluonhq.impl.connect.converter.ClassInspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import com.gluonhq.connect.Level;
import com.gluonhq.connect.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * An InputConverter that converts a XML Object read from an InputStream into an object. 
 * 
 * @param <T> the type of the object to convert the XML Object into
 */
public class XmlInputConverter<T> extends InputStreamInputConverter<T>  {

    private Class<T> clazz;
    private final ClassInspector<T> inspector;
    private String tag;
    
    /**
     * Construct a new instance of a XmlInputConverter that is able to convert the data read from the InputStream into
     * objects of the specified <code>targetClass</code>.
     *
     * @param targetClass The class defining the objects being converted from XML.
     */
    public XmlInputConverter(Class<T> targetClass) {
        this(targetClass, null);
    }
    
    /**
     * Construct a new instance of a XmlInputConverter that is able to convert the data read from the InputStream into
     * objects of the specified <code>targetClass</code>.
     * Often, the XML returned by web services has the relevant information nested or wrapped in rootnodes.
     * If there is a 1-1 mapping between the data and the Object, we would end up with unused wrapper classes.
     * By specifying the name of the node that contains the relevant information, the wrapping data is ignored.
     *
     * @param targetClass The class defining the objects being converted from XML.
     * @param tag the nodename of the rootnode containing the relevant information.
     */
    public XmlInputConverter(Class<T> targetClass, String tag) {
        this.clazz = targetClass;
        this.inspector = ClassInspector.resolve(targetClass);
        this.tag = tag;
    }
    
    @Override
    public T read() {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = getInputStream();
            T answer = clazz.newInstance();
            Document doc = builder.parse(is);
            Node rootNode = doc.getDocumentElement();
            if (tag != null) {
                NodeList nodeList = doc.getElementsByTagName(tag);
                if (nodeList.getLength() > 0) rootNode = nodeList.item(0);
                parseDom (rootNode.getChildNodes(), answer);
            }
            return answer;
        } catch (InstantiationException ex) {
            Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void parseDom(NodeList childNodes, T answer) {
                    Map<String, Method> setters = this.inspector.getSetters();
        Set<String> keySet = setters.keySet();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (keySet.contains(item.getNodeName())) {
                try {
                    Method setter = setters.get(item.getNodeName());
                    Class<?> parameterType = setter.getParameterTypes()[0];
                    if (parameterType.isAssignableFrom(String.class)) {
                        setter.invoke(answer, item.getTextContent());
                    }
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(XmlInputConverter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
                    
                    
    
}
