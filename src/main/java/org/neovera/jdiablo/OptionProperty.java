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
 * Information about a property that is annotated as an option.
 */
public interface OptionProperty extends OptionPropertyAccessor {

    /**
     * @return true if property value was provided in the command line.
     */
    public boolean isValueInCommandLine();

    /**
     * @return true if property has a value provided via specialization or option-value-provider.
     */
    public boolean isOptionNotRequiredOverride();
}