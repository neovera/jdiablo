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

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanUtils {

    private static Logger _logger = LoggerFactory.getLogger(BeanUtils.class);

    public static void setProperty(Object instance, String propertyName, Object value) {
        String p = propertyName;
        if (p.startsWith("_")) {
            p = p.substring(1);
        }
        try {
            PropertyUtils.setProperty(instance, p, value);
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Method getWriteMethod(String fieldName, Class<?> clz) {
        try {
            return PropertyUtils.getWriteMethod(new PropertyDescriptor(fieldName, clz));
        } catch (IntrospectionException e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
