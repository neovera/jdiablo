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

import java.util.List;

import org.neovera.jdiablo.Environment;

public class EnvContainer {

    private Environment _environment;
    private boolean _new;
    List<OptionPropertySpi> _optionProperties;

    public EnvContainer(Environment environment) {
        _environment = environment;
    }

    public EnvContainer(Environment environment, boolean state) {
        _environment = environment;
        _new = state;
    }

    public Environment getEnvironment() {
        return _environment;
    }

    public void setEnvironment(Environment environment) {
        _environment = environment;
    }

    public boolean isNew() {
        return _new;
    }

    public void setNew(boolean new1) {
        _new = new1;
    }

    public List<OptionPropertySpi> getOptionProperties() {
        return _optionProperties;
    }

    public void setOptionProperties(List<OptionPropertySpi> optionProperties) {
        _optionProperties = optionProperties;
    }

}
