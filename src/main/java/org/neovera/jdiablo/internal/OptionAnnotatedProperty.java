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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang.StringUtils;
import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.internal.convert.CommandLineOptionValueFacets;

/**
 * Encapsulates a property and the corresponding option.
 */
public class OptionAnnotatedProperty {

    private org.apache.commons.cli.Option _cliOption;
    private OptionPropertySpi _optionProperty;
    
    private TargetOptionBinder _optionBinder;

    public OptionAnnotatedProperty(OptionPropertySpi optionProperty) {
        setOptionProperty(optionProperty);
        _optionBinder = new TargetOptionBinder(optionProperty);
    }

    public Option getOption() {
        return getOptionProperty().getOption();
    }

    public Method getSetterMethod() {
        return getOptionProperty().getSetterMethod();
    }

    public void setCliOption(org.apache.commons.cli.Option cliOption) {
        _cliOption = cliOption;
    }

    @SuppressWarnings("static-access")
    public org.apache.commons.cli.Option getCliOption() {
        if (_cliOption != null) {
            return _cliOption;
        } else {
            Option option = getOption();
            OptionBuilder builder = OptionBuilder.withDescription(option.description());
            if (StringUtils.isNotBlank(option.argName())) {
                builder = builder.withArgName(option.argName());
            }
            if (option.args() != 0) {
                builder = builder.hasArgs(option.args());
            }
            if (option.hasArgs()) {
                builder = builder.hasArgs();
            }
            if (StringUtils.isNotBlank(option.longOption())) {
                builder = builder.withLongOpt(option.longOption());
            }
            if (option.optionalArgs() != 0) {
                builder = builder.hasOptionalArgs(option.optionalArgs());
            }
            if (option.required() && !getOptionProperty().isOptionNotRequiredOverride()) {
                builder = builder.isRequired();
            }
            if (option.valueSeparator() != ' ') {
                builder = builder.withValueSeparator(option.valueSeparator());
            }

            setCliOption(builder.create(option.shortOption()));
            return getCliOption();
        }
    }

    private OptionPropertySpi getOptionProperty() {
        return _optionProperty;
    }

    private void setOptionProperty(OptionPropertySpi optionProperty) {
        _optionProperty = optionProperty;
    }

    /**
     * @param cmd Command line to apply.
     * @param target Object to apply to.
     */
    public void bind(CommandLine cmd, Object target) {
        if (!cmd.hasOption(getOption().shortOption())) {
            return;
        }

        boolean isBinded = _optionBinder.bind(new CommandLineOptionValueFacets(cmd, getOption()), target);
        getOptionProperty().setValueInCommandLine(isBinded);
    }
    
}