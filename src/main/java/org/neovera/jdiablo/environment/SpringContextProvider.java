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
package org.neovera.jdiablo.environment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neovera.jdiablo.Environment;
import org.neovera.jdiablo.Launchable;
import org.neovera.jdiablo.OptionPropertyAccessor;
import org.neovera.jdiablo.OptionValueProvider;

/**
 * Define this as an OptionValueProvider in the @UseOptionValueProviders annotation against the launchable class if 
 * you want to provide default spring context files for the launchable using the @SpringContext annotation (also against
 * the launchable class). This allows overriding of individual targets using command line parameters.
 */
public class SpringContextProvider implements OptionValueProvider {

    public void provideOptionValues(Class<? extends Environment> envClz, Environment environment, List<? extends OptionPropertyAccessor> list,
            Class<? extends Launchable> launchableClass, Launchable launchable) {
        if (environment instanceof SpringEnvironment) {
            SpringEnvironment springEnvironment = (SpringEnvironment)environment;
            Class<?> clz = launchableClass;

            Set<String> contextFiles = new HashSet<String>();
            while (!clz.equals(Object.class)) {
                SpringContext context = clz.getAnnotation(SpringContext.class);
                if (context != null && context.value() != null) {
                    for (String ctx : context.value()) {
                        contextFiles.add(ctx);
                    }
                }
                clz = clz.getSuperclass();
            }
            if (contextFiles.size() > 0) {
                springEnvironment.setSpringContextFiles(contextFiles.toArray(new String[] {}));
            }
        }
    }

    public void provideOptionValues(Class<? extends Launchable> launchableClass, Launchable launchable, List<? extends OptionPropertyAccessor> launchProperties) {

    }

}
