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

/**
 * This is used as part of the build process. This interface allows specialization of environments
 * during the launch process.
 */
public interface EnvironmentBuilder {

    /**
     * Use this when chaining Launchables (calling one Launchable from another) where you want a new
     * (non-specialized or uninitialized) instance of an Environment to be used in the execution.
     * For example, when calling one launchable from another, if you want to call the second
     * Launchable class with a different Spring context, call this method passing in
     * SpringEnvironment class so that you can pass in a new initialization for it.
     * 
     * @param clz Environment class
     * @return Environment builder.
     */
    public EnvironmentBuilder withNewInstanceOf(Class<? extends Environment> clz);

    /**
     * Allows for programmatic specialization (setting of properties) when launching. Example:
     * different command line processes in a project require different Spring contexts, you can
     * plug-in the context selection in here. Of course, in the case of Spring you can also annotate the Launchable
     * with the @SpringContext annotation or simply pass that as a command line, but the specialization hook allows
     * for algorithmic determination of the context. 
     * 
     * @param clz
     * @param specialization
     * @return Environment
     */
    public <E extends Environment> EnvironmentBuilder withSpecialization(Class<E> clz, Specialization<E> specialization);

    /**
     * @return build an executor for the above defined specs.
     */
    public Executor build();
}
