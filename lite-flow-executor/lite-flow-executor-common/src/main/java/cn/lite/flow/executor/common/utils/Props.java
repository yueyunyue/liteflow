/*
 * Copyright 2012 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cn.lite.flow.executor.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * 参数工具类
 */
public class Props {

  private final Map<String, String> paramMap;

  /**
   * Constructor for empty jobProps with empty parent.
   */
  public Props() {
    this(new HashMap<>());
  }

  /**
   * json
   * @param json
   */
  public Props(String json) {
    Map<String, String> map = Maps.newHashMap();
    if(StringUtils.isNotBlank(json)){
      JSONObject propObj = JSONObject.parseObject(json);
      Map<String, Object> innerMap = propObj.getInnerMap();
      for(Map.Entry<String, Object> mapKV : innerMap.entrySet()){
        map.put(mapKV.getKey(), String.valueOf(mapKV.getValue()));
      }
    }
    this.paramMap = map;
  }

  /**
   * Constructor for empty Props with parent override.
   */
  public Props(final Map<String, String> paramMap) {
    this.paramMap = paramMap;
  }

  /**
   *
   * @param inputStream
   * @throws IOException
   */
  private void loadFrom(final InputStream inputStream) throws IOException {
    final Properties properties = new Properties();
    properties.load(inputStream);
    this.put(properties);
  }

  /**
   * Clear the current Props, but leaves the parent untouched.
   */
  public void clearLocal() {
    this.paramMap.clear();
  }

  /**
   * Check key in current Props then search in parent
   */
  public boolean containsKey(final Object k) {
    return this.paramMap.containsKey(k);
  }

  /**
   * Check value in current Props then search in parent
   */
  public boolean containsValue(final Object value) {
    return this.paramMap.containsValue(value);
  }

  /**
   * Return value if available in current Props otherwise return from parent
   */
  public String get(final Object key) {
    if (this.paramMap.containsKey(key)) {
      return this.paramMap.get(key);
    } else {
      return null;
    }
  }

  /**
   * Get the key set from the current Props
   */
  public Set<String> localKeySet() {
    return this.paramMap.keySet();
  }

  /**
   * Put the given string value for the string key. This method performs any variable substitution
   * in the value replacing any occurance of ${name} with the value of get("name").
   *
   * @param key The key to put the value to
   * @param value The value to do substitution on and store
   * @throws IllegalArgumentException If the variable given for substitution is not a valid key in
   * this Props.
   */
  public String put(final String key, final String value) {
    return this.paramMap.put(key, value);
  }

  /**
   * Put the given Properties into the Props. This method performs any variable substitution in the
   * value replacing any occurrence of ${name} with the value of get("name"). get() is called first
   * on the Props and next on the Properties object.
   *
   * @param properties The properties to put
   * @throws IllegalArgumentException If the variable given for substitution is not a valid key in
   * this Props.
   */
  public void put(final Properties properties) {
    for (final String propName : properties.stringPropertyNames()) {
      this.paramMap.put(propName, properties.getProperty(propName));
    }
  }

  /**
   * Put integer
   */
  public String put(final String key, final Integer value) {
    return this.paramMap.put(key, value.toString());
  }

  /**
   * Put Long. Stores as String.
   */
  public String put(final String key, final Long value) {
    return this.paramMap.put(key, value.toString());
  }

  /**
   * Put Double. Stores as String.
   */
  public String put(final String key, final Double value) {
    return this.paramMap.put(key, value.toString());
  }

  /**
   * Put everything in the map into the jobProps.
   */
  public void putAll(final Map<? extends String, ? extends String> m) {
    if (m == null) {
      return;
    }

    for (final Map.Entry<? extends String, ? extends String> entry : m.entrySet()) {
      this.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Put all properties in the jobProps into the current jobProps. Will handle null p.
   */
  public void putAll(final Props p) {
    if (p == null) {
      return;
    }

    for (final String key : p.getKeySet()) {
      this.put(key, p.get(key));
    }
  }

  /**
   * Puts only the local jobProps from p into the current properties
   */
  public void putLocal(final Props p) {
    for (final String key : p.localKeySet()) {
      this.put(key, p.get(key));
    }
  }

  /**
   * Remove only the local value of key s, and not the parents.
   */
  public String removeLocal(final Object s) {
    return this.paramMap.remove(s);
  }

  /**
   * The number of unique keys defined by this Props and all parent Props
   */
  public int size() {
    return getKeySet().size();
  }

  /**
   * The number of unique keys defined by this Props (keys defined only in parent Props are not
   * counted)
   */
  public int localSize() {
    return this.paramMap.size();
  }

  /**
   * Attempts to return the Class that corresponds to the Props value. If the class doesn't exit, an
   * IllegalArgumentException will be thrown.
   */
  public Class<?> getClass(final String key) {
    try {
      if (containsKey(key)) {
        return Class.forName(get(key));
      } else {
        throw new UndefinedPropertyException("Missing required property '"
            + key + "'");
      }
    } catch (final ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public Class<?> getClass(final String key, final boolean initialize, final ClassLoader cl) {
    try {
      if (containsKey(key)) {
        return Class.forName(get(key), initialize, cl);
      } else {
        throw new UndefinedPropertyException("Missing required property '"
            + key + "'");
      }
    } catch (final ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Gets the class from the Props. If it doesn't exist, it will return the defaultClass
   */
  public Class<?> getClass(final String key, final Class<?> defaultClass) {
    if (containsKey(key)) {
      return getClass(key);
    } else {
      return defaultClass;
    }
  }

  /**
   * Gets the string from the Props. If it doesn't exist, it will return the defaultValue
   */
  public String getString(final String key, final String defaultValue) {
    if (containsKey(key)) {
      return get(key);
    } else {
      return defaultValue;
    }
  }

  /**
   * Gets the string from the Props. If it doesn't exist, throw and UndefinedPropertiesException
   */
  public String getString(final String key) {
    if (containsKey(key)) {
      return get(key);
    }
    return null;
  }

  /**
   * Returns a list of strings with the comma as the separator of the value
   */
  public List<String> getStringList(final String key) {
    return getStringList(key, "\\s*,\\s*");
  }

  /**
   * Returns a list of clusters with the comma as the separator of the value
   * e.g., for input string: "thrift://hcat1:port,thrift://hcat2:port;thrift://hcat3:port,thrift://hcat4:port;"
   * we will get ["thrift://hcat1:port,thrift://hcat2:port", "thrift://hcat3:port,thrift://hcat4:port"] as output
   */
  public List<String> getStringListFromCluster(final String key) {
    List<String> curlist = getStringList(key, "\\s*;\\s*");
    // remove empty elements in the array
    for (Iterator<String> iter = curlist.listIterator(); iter.hasNext(); ) {
      String a = iter.next();
      if (a.length() == 0) {
        iter.remove();
      }
    }
    return curlist;
  }

  /**
   * Returns a list of strings with the sep as the separator of the value
   */
  public List<String> getStringList(final String key, final String sep) {
    final String val = get(key);
    if (val == null || val.trim().length() == 0) {
      return Collections.emptyList();
    }

    if (containsKey(key)) {
      return Arrays.asList(val.split(sep));
    } else {
      throw new UndefinedPropertyException("Missing required property '" + key
          + "'");
    }
  }

  /**
   * Returns a list of strings with the comma as the separator of the value. If the value is null,
   * it'll return the defaultValue.
   */
  public List<String> getStringList(final String key, final List<String> defaultValue) {
    if (containsKey(key)) {
      return getStringList(key);
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns a list of strings with the sep as the separator of the value. If the value is null,
   * it'll return the defaultValue.
   */
  public List<String> getStringList(final String key, final List<String> defaultValue,
      final String sep) {
    if (containsKey(key)) {
      return getStringList(key, sep);
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns true if the value equals "true". If the value is null, then the default value is
   * returned.
   */
  public boolean getBoolean(final String key, final boolean defaultValue) {
    if (containsKey(key)) {
      return "true".equalsIgnoreCase(get(key).trim());
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns true if the value equals "true". If the value is null, then an
   * UndefinedPropertyException is thrown.
   */
  public boolean getBoolean(final String key) {
    if (containsKey(key)) {
      return "true".equalsIgnoreCase(get(key));
    } else {
      throw new UndefinedPropertyException("Missing required property '" + key
          + "'");
    }
  }

  /**
   * Returns the long representation of the value. If the value is null, then the default value is
   * returned. If the value isn't a long, then a parse exception will be thrown.
   */
  public long getLong(final String name, final long defaultValue) {
    if (containsKey(name)) {
      return Long.parseLong(get(name));
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns the long representation of the value. If the value is null, then a
   * UndefinedPropertyException will be thrown. If the value isn't a long, then a parse exception
   * will be thrown.
   */
  public long getLong(final String name) {
    if (containsKey(name)) {
      return Long.parseLong(get(name));
    } else {
      throw new UndefinedPropertyException("Missing required property '" + name
          + "'");
    }
  }

  /**
   * Returns the int representation of the value. If the value is null, then the default value is
   * returned. If the value isn't a int, then a parse exception will be thrown.
   */
  public int getInt(final String name, final int defaultValue) {
    if (containsKey(name)) {
      return Integer.parseInt(get(name).trim());
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns the int representation of the value. If the value is null, then a
   * UndefinedPropertyException will be thrown. If the value isn't a int, then a parse exception
   * will be thrown.
   */
  public int getInt(final String name) {
    if (containsKey(name)) {
      return Integer.parseInt(get(name).trim());
    } else {
      throw new UndefinedPropertyException("Missing required property '" + name
          + "'");
    }
  }

  /**
   * Returns the double representation of the value. If the value is null, then the default value is
   * returned. If the value isn't a double, then a parse exception will be thrown.
   */
  public double getDouble(final String name, final double defaultValue) {
    if (containsKey(name)) {
      return Double.parseDouble(get(name).trim());
    } else {
      return defaultValue;
    }
  }

  /**
   * Returns the double representation of the value. If the value is null, then a
   * UndefinedPropertyException will be thrown. If the value isn't a double, then a parse exception
   * will be thrown.
   */
  public double getDouble(final String name) {
    if (containsKey(name)) {
      return Double.parseDouble(get(name).trim());
    } else {
      throw new UndefinedPropertyException("Missing required property '" + name
          + "'");
    }
  }

  /**
   * Returns the uri representation of the value. If the value is null, then the default value is
   * returned. If the value isn't a uri, then a IllegalArgumentException will be thrown.
   */
  public URI getUri(final String name) {
    if (containsKey(name)) {
      try {
        return new URI(get(name));
      } catch (final URISyntaxException e) {
        throw new IllegalArgumentException(e.getMessage());
      }
    } else {
      throw new UndefinedPropertyException("Missing required property '" + name
          + "'");
    }
  }

  /**
   * Returns the double representation of the value. If the value is null, then the default value is
   * returned. If the value isn't a uri, then a IllegalArgumentException will be thrown.
   */
  public URI getUri(final String name, final URI defaultValue) {
    if (containsKey(name)) {
      return getUri(name);
    } else {
      return defaultValue;
    }
  }

  public URI getUri(final String name, final String defaultValue) {
    try {
      return getUri(name, new URI(defaultValue));
    } catch (final URISyntaxException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  /**
   * Store only those properties defined at this local level
   *
   * @param file The file to write to
   * @throws IOException If the file can't be found or there is an io error
   */
  public void storeLocal(final File file) throws IOException {
    final BufferedOutputStream out =
        new BufferedOutputStream(new FileOutputStream(file));
    try {
      storeLocal(out);
    } finally {
      out.close();
    }
  }

  /**
   * Store only those properties defined at this local level
   *
   * @param out The output stream to write to
   * @throws IOException If the file can't be found or there is an io error
   */
  public void storeLocal(final OutputStream out) throws IOException {
    final Properties p = new Properties();
    for (final String key : this.paramMap.keySet()) {
      p.setProperty(key, get(key));
    }
    p.store(out, null);
  }

  /**
   * Returns a java.util.Properties file populated with the current Properties in here.
   * Note: if you want to import parent properties (e.g., database credentials), please use
   * toAllProperties
   */
  public Properties toProperties() {
    final Properties p = new Properties();
    for (final String key : this.paramMap.keySet()) {
      p.setProperty(key, get(key));
    }

    return p;
  }

  /**
   * Returns a java.util.Properties file populated with both current and parent properties.
   */
  public Properties toAllProperties() {
    Properties allProp = new Properties();
    // import local properties
    allProp.putAll(toProperties());

    return allProp;
  }

  /**
   * Returns a map of all the flattened properties, the item in the returned map is sorted
   * alphabetically by the key value.
   *
   * @Return
   */
  public Map<String, String> getFlattened() {
    final TreeMap<String, String> returnVal = new TreeMap<>();
    returnVal.putAll(getMapByPrefix(""));
    return returnVal;
  }

  /**
   * Get a map of all properties by string prefix
   *
   * @param prefix The string prefix
   */
  public Map<String, String> getMapByPrefix(final String prefix) {
    final Map<String, String> values = new HashMap<>();

    // when there is a conflict, value from the child takes the priority.
    for (final String key : this.localKeySet()) {
      if (key.startsWith(prefix)) {
        values.put(key.substring(prefix.length()), get(key));
      }
    }
    return values;
  }

  /**
   * Returns a set of all keys, including the parents
   */
  public Set<String> getKeySet() {
    final HashSet<String> keySet = new HashSet<>();
    keySet.addAll(localKeySet());
    return keySet;
  }

  /**
   */
  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    } else if (o == null) {
      return false;
    } else if (o.getClass() != Props.class) {
      return false;
    }

    final Props p = (Props) o;
    return this.paramMap.equals(p.paramMap);
  }

  /**
   * Returns true if the properties are equivalent, regardless of the hierarchy.
   */
  public boolean equalsProps(final Props p) {
    if (p == null) {
      return false;
    }

    final Set<String> myKeySet = getKeySet();
    for (final String s : myKeySet) {
      if (!get(s).equals(p.get(s))) {
        return false;
      }
    }

    return myKeySet.size() == p.getKeySet().size();
  }

  public Map<String, String> getParamMap(){
    if(this.paramMap != null){
      return ImmutableMap.copyOf(this.paramMap);
    }
    return null;
  }

  /**
   *
   */
  @Override
  public int hashCode() {
    int code = this.paramMap.hashCode();
    return code;
  }

  /**
   *
   */
  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder("{");
    for (final Map.Entry<String, String> entry : this.paramMap.entrySet()) {
      builder.append(entry.getKey());
      builder.append(": ");
      builder.append(entry.getValue());
      builder.append(", ");
    }
    builder.append("}");
    return builder.toString();
  }

}
