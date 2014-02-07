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
 * An environment starts and stops frameworks and other aspects required for the process to function. An example of an
 * environment is the Spring IoC system. The SpringEnvironment.java class sets up and tears down the Spring context
 * when the process starts and stops. 
 */
public interface Environment {

    /**
     * Start the environment. Any exception thrown will cause target execution to abort 
     * and stop to be called.
     * @return true to continue to next environment, false to stop all environments and 
     * abort launch.
     */
    public boolean start();

    /**
     * Stop the environment.
     */
    public void stop();

    /**
     * This method is called after the start() of this environment. It is called for each 
     * environment that is registered - those that have already been initialized and those that are
     * yet to be initialized. In a chained execution, this is called again when each environment
     * is restarted and new ones are started.
     * @param environment Launch environment instance.
     * @param properties Annotated properties for this environment. NOTE: A property that is 
     * annotated will not appear in this list if the option was defined during specialization. The
     * isValueInCommandLine() will return true if the value was specified (and set) via command 
     * line.  
     */
    public void initialize(Environment environment, List<? extends OptionProperty> properties);

    /**
     * Called after the start() of this environment.
     * @param target The target of the launch.
     * @param properties Annotated properties for the launch target.
     */
    public void initialize(Object target, List<? extends OptionProperty> properties);

}
