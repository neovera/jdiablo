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
 * "Service Provider" interface that allows a CLI to interrogate environments it is running in and get a builder to 
 * launch another CLI Launchable class.
 */
public interface ExecutionHandler {

    /**
     * @param <E> Environment type
     * @param clz Class of the environment
     * @return Reference to environment for the current execution if it exists, null otherwise.
     */
    public <E extends Environment> E getEnvironment(Class<E> clz);

    /**
     * @return Target builder for a chained execution.
     */
    public TargetBuilder getChainedExecutor();
}
