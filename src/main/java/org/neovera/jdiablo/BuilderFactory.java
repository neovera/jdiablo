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

import java.util.HashMap;
import java.util.Map;

import org.neovera.jdiablo.internal.BuilderImpl;
import org.neovera.jdiablo.internal.SpecializationUtil;

/**
 * This class allows you to start the Diablo CLI "building" process. The BuilderFactory creates a TargetBuilder. The
 * TargetBuilder accepts the Launchable target to execute and returns an EnvironmentBuilder. The EnvironmentBuilder
 * provides hooks to specialize Environments (such as Spring) and then provides a handle to the Executor for starting
 * the execution.
 */
public class BuilderFactory {

    private Map<Class<? extends Environment>, Environment> _specializedEnvironments = new HashMap<Class<? extends Environment>, Environment>();

    /**
     * Factory method to create a target builder passing in the command line arguments.
     * @param args Command line arguments from main() as-is.
     * @return New TargetBuilder instance
     */
    public TargetBuilder create(String[] args) {
        return new BuilderImpl(args, this);
    }

    /**
     * Creates a TargetBuilder without any passed in command-line arguments.
     * @return New TargetBuilder instance
     */
    public TargetBuilder create() {
        return new BuilderImpl(this);
    }

    /**
     * Use this method to add specializations for a particular environment as part of the
     * environment builder. This allows specialized subclasses of BuilderFactory to introduce
     * specializations that can be applied across the board. Note, specializations apply only to new
     * instances of an environment.
     * 
     * @param <E> Environment type
     * @param clz Environment's class
     * @param specialization Specialization implementation for the environment.
     */
    public <E extends Environment> void add(Class<E> clz, Specialization<E> specialization) {
        E e = SpecializationUtil.createOptionAwareProxy(clz);
        specialization.specialize(e);
        _specializedEnvironments.put(clz, e);
    }

    /**
     * Ideally this method would be "module" scoped. There is no reason for client code to access
     * specialized environments.
     * 
     * @return The environment specializations map. 
     */
    public Map<Class<? extends Environment>, Environment> getSpecializations() {
        return _specializedEnvironments;
    }
}