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
package org.neovera.jdiablo.internal.convert;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.neovera.jdiablo.annotation.Option;

/**
 * Exposes the supported value types of an option value from
 * a given option of the provided command line 
 */
public class CommandLineOptionValueFacets implements OptionValueFacets {
    CommandLine _commandLine;
    Option _option;
    
    /**
     * @param commandLine
     */
    public CommandLineOptionValueFacets(CommandLine commandLine, Option option) {
        super();
        _commandLine = commandLine;
        _option = option;
    }

    public String getOptionValue() {
        return _commandLine.getOptionValue(_option.shortOption());
    }

    public String[] getOptionValues() {
        return _commandLine.getOptionValues(_option.shortOption());
    }
    
    public Properties getOptionProperties() {
        return _commandLine.getOptionProperties(_option.shortOption());
    }
    
}