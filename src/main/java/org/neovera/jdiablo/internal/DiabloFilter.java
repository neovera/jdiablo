/**
 * Copyright 2012 Neovera Inc
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
package org.neovera.jdiablo.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.neovera.jdiablo.annotation.Option;

import net.sf.cglib.proxy.CallbackFilter;

public class DiabloFilter implements CallbackFilter {

    private Set<String> _optionAnnotatedFieldNames = new HashSet<String>();

    public DiabloFilter(final Class<?> claz) {
        // Setup set of @Option annotated field names.
        Class<?> clz = claz;

        while (clz != null) {
            for (Field field : clz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Option.class)) {
                    _optionAnnotatedFieldNames.add(field.getName().startsWith("_") ? field.getName().substring(1) : field.getName());
                }
            }
            clz = clz.getSuperclass();
        }
    }

    public int accept(Method method) {
        String propertyName = SpecializationUtil.getPropertyName(method);
        if (propertyName != null && _optionAnnotatedFieldNames.contains(propertyName)) {
            return 0;
        } else {
            return 1;
        }
    }

}
