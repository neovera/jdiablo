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
package org.neovera.jdiablo;

import java.util.List;

/**
 * Core interface that "value providers" should implement. Value providers attempt to satisfy
 * command line options through other means (e.g., annotation against Class, resource in classpath,
 * Spring properties, etc.). Required options that are populated via option value providers turn
 * "optional" and don't need to be populated via command line arguments. If an option's value is
 * provided for such an option via command line arguments, optionValueProvider provided value is
 * overridden. 
 * 
 * Example: A classpath resource based option value provider may load a well-known
 * resources file and then look for keys that are named using the option's longOption value. It can
 * then use such values to populate the options with "default" values.
 */
public interface OptionValueProvider {

    /**
     * Called for each environment being used.
     * 
     * @param clz Class of the environment (often different from environment.getClass()).
     * @param environment Environment instance.
     * @param list List of properties relevant to the environment.
     * @param launchableClass "Main"'s class.
     * @param launchable Main instance.
     */
    void provideOptionValues(Class<? extends Environment> clz, Environment environment, List<? extends OptionPropertyAccessor> list, Class<? extends Launchable> launchableClass,
            Launchable launchable);

    /**
     * Callback to setup the target (launchable instance) with "default" options using this option
     * provider.
     * 
     * @param launchableClass Class of the "main" class (often different from launchable.getClass())
     * @param launchable Main instance.
     * @param launchProperties Option properties defined in the "main" launchable that may need to
     *            be populated by the OptionValueProvider.
     */
    void provideOptionValues(Class<? extends Launchable> launchableClass, Launchable launchable, List<? extends OptionPropertyAccessor> launchProperties);

}
