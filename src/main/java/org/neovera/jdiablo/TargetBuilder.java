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
 * Interface to create a builder with.
 */
public interface TargetBuilder {

    /**
     * Tell the builder to build a launchable of a particular derived type.
     *
     * @param <L> Launchable type
     * @param launchableClass Launchable class
     * @return Environment builder to customize environments for this launchable type.
     */
    public <L extends Launchable> EnvironmentBuilder withTarget(Class<L> launchableClass);

    /**
     * Tell the builder to build a launchable of a particular type with given specialization.
     *
     * @param <L> Launchable type
     * @param launchableClass Launchable class
     * @param specialization The specialization to apply to the launchable type.
     * @return Environment builder to customize environments for this launchable type.
     */
    public <L extends Launchable> EnvironmentBuilder withTarget(Class<L> launchableClass, Specialization<L> specialization);
}
