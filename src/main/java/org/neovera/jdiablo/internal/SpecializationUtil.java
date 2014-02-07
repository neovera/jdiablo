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

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class SpecializationUtil {

    @SuppressWarnings("unchecked")
    public static <T> T createOptionAwareProxy(Class<? extends T> clz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clz);
        enhancer.setCallbacks(new Callback[] {new SetterMethodInterceptor(), NoOp.INSTANCE});
        enhancer.setCallbackFilter(new DiabloFilter(clz));
        return (T)enhancer.create();
    }

    /**
     * If this is a setter method, then return the javabean property it represents.
     * 
     * @param method Method
     * @return null if not java bean property setter method, otherwise property name.
     */
    public static String getPropertyName(Method method) {
        if (method.getName().startsWith("set") && method.getName().length() > 3) {
            StringBuilder builder = new StringBuilder(method.getName().substring(3));
            builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
            return builder.toString();
        } else {
            return null;
        }
    }
}
