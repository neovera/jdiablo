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

import org.neovera.jdiablo.annotation.Option;

/**
 * Information about a property that is annotated as an option.
 */
public class OptionPropertyImpl implements OptionPropertySpi {

    private String _propertyName;
    private Method _setterMethod;
    private Option _option;
    private boolean _valueInCommandLine;
    private boolean _optionNotRequiredOverride;

    public String getPropertyName() {
        return _propertyName;
    }

    public void setPropertyName(String propertyName) {
        _propertyName = propertyName;
    }

    public Method getSetterMethod() {
        return _setterMethod;
    }

    public void setSetterMethod(Method setterMethod) {
        _setterMethod = setterMethod;
    }

    public Option getOption() {
        return _option;
    }

    public void setOption(Option option) {
        _option = option;
    }

    public boolean isValueInCommandLine() {
        return _valueInCommandLine;
    }

    public void setValueInCommandLine(boolean valueInCommandLine) {
        _valueInCommandLine = valueInCommandLine;
    }

    public boolean isOptionNotRequiredOverride() {
        return _optionNotRequiredOverride;
    }

    public void setOptionNotRequiredOverride(boolean optionNotRequiredOverride) {
        _optionNotRequiredOverride = optionNotRequiredOverride;
    }

}