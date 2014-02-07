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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class SetterMethodInterceptor implements MethodInterceptor {

    private Set<String> _notRequiredProperties = new HashSet<String>();

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String propertyName = SpecializationUtil.getPropertyName(method);
        if (propertyName != null) {
            _notRequiredProperties.add(propertyName);
        }
        return proxy.invokeSuper(obj, args);
    }

    public boolean isPropertyNotRequired(String propertyName) {
        return _notRequiredProperties.contains(propertyName);
    }

}
