/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.caliper.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class Util {
  private Util() {}

  // Users have no idea that nested classes are identified with '$', not '.', so if class lookup
  // fails try replacing the last . with $.
  public static Class<?> lenientClassForName(String className) throws ClassNotFoundException {
    try {
      // TODO: consider whether any control over class loader is really needed here
      // (forName(className, true, Thread.currentThread().getContextClassLoader())?)
      return Class.forName(className);
    } catch (ClassNotFoundException ignored) {
      // try replacing the last dot with a $, in case that helps
      // example: tutorial.Tutorial.Benchmark1 becomes tutorial.Tutorial$Benchmark1
      // amusingly, the $ character means three different things in this one line alone
      String newName = className.replaceFirst("\\.([^.]+)$", "\\$$1");
      return Class.forName(newName);
    }
  }

  public static ImmutableMap<String, String> loadProperties(ByteSource is) throws IOException {
    Properties props = new Properties();
    InputStream in = is.openStream();
    try {
      props.load(in);
    } finally {
      Closeables.closeQuietly(in);
    }
    return Maps.fromProperties(props);
  }

  public static ByteSource resourceSupplier(final Class<?> c, final String name) {
    return Resources.asByteSource(c.getResource(name));
  }

  public static <T> ImmutableMap<String, T> prefixedSubmap(
      Map<String, T> props, String prefix) {
    ImmutableMap.Builder<String, T> submapBuilder = ImmutableMap.builder();
    for (Map.Entry<String, T> entry : props.entrySet()) {
      String name = entry.getKey();
      if (name.startsWith(prefix)) {
        submapBuilder.put(name.substring(prefix.length()), entry.getValue());
      }
    }
    return submapBuilder.build();
  }

  public static boolean isPublic(Member member) {
    return Modifier.isPublic(member.getModifiers());
  }

  public static boolean isStatic(Member member) {
    return Modifier.isStatic(member.getModifiers());
  }

  private static final long FORCE_GC_TIMEOUT_SECS = 2;

  public static void forceGc() {
    System.gc();
    System.runFinalization();
    final CountDownLatch latch = new CountDownLatch(1);
    new Object() {
      @Override protected void finalize() {
        latch.countDown();
      }
    };
    System.gc();
    System.runFinalization();
    try {
      latch.await(FORCE_GC_TIMEOUT_SECS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public static <T> ImmutableBiMap<T, String> assignNames(Set<T> items) {
    ImmutableList<T> itemList = ImmutableList.copyOf(items);
    ImmutableBiMap.Builder<T, String> itemNamesBuilder = ImmutableBiMap.builder();
    for (int i = 0; i < itemList.size(); i++) {
      itemNamesBuilder.put(itemList.get(i), generateUniqueName(i));
    }
    return itemNamesBuilder.build();
  }

  private static String generateUniqueName(int index) {
    if (index < 26) {
      return String.valueOf((char) ('A' + index));
    } else {
      return generateUniqueName(index / 26 - 1) + generateUniqueName(index % 26);
    }
  }
}
