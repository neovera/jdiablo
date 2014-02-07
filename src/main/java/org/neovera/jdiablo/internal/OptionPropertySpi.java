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

import org.neovera.jdiablo.OptionProperty;
import org.neovera.jdiablo.annotation.Option;

/**
 * Information about a property that is annotated as an option.
 */
public interface OptionPropertySpi extends OptionProperty {

    /**
     * @param propertyName Name of the property
     */
    public void setPropertyName(String propertyName);

    /**
     * @param setterMethod Setter method for the property.
     */
    public void setSetterMethod(Method setterMethod);

    /**
     * @param option Option annotation specified.
     */
    public void setOption(Option option);

    /**
     * @param value true to indicate value was provided in command line.
     */
    public void setValueInCommandLine(boolean value);

    /**
     * @param optionNotRequiredOverride set to true to indicate that a value has been provided for this option
     * and hence the command-line option should be overridden to set it to not-required.
     */
    public void setOptionNotRequiredOverride(boolean optionNotRequiredOverride);

}