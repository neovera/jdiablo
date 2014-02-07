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
import java.math.BigDecimal;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.lang.StringUtils;
import org.neovera.jdiablo.annotation.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates a property and the corresponding option.
 */
public class OptionAnnotatedProperty {

    private org.apache.commons.cli.Option _cliOption;
    private static Logger _logger = LoggerFactory.getLogger(OptionAnnotatedProperty.class);
    private OptionPropertySpi _optionProperty;

    public OptionAnnotatedProperty(OptionPropertySpi optionProperty) {
        setOptionProperty(optionProperty);
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
        Option option = getOption();
        try {
            if (!cmd.hasOption(option.shortOption())) {
                return;
            }
            if (!getSetterMethod().getDeclaringClass().isAssignableFrom(target.getClass())) {
                return;
            }

            if (_logger.isTraceEnabled()) {
                _logger.trace(getSetterMethod().getParameterTypes()[0].getName());
                _logger.trace(option.shortOption());
                _logger.trace(target.getClass().getName());
            }
            String value = cmd.getOptionValue(option.shortOption());
            getOptionProperty().setValueInCommandLine(true);
            if (getSetterMethod().getParameterTypes()[0].equals(Boolean.class) || "boolean".equals(getSetterMethod().getParameterTypes()[0].getName())) {
                if (option.args() == 0) {
                    getSetterMethod().invoke(target, true);
                } else {
                    getSetterMethod().invoke(target, value == null ? false : "true".equals(value));
                }
            } else if (getSetterMethod().getParameterTypes()[0].equals(Integer.class) || "int".equals(getSetterMethod().getParameterTypes()[0].getName())) {
                if (option.args() == 0) {
                    getSetterMethod().invoke(target, 1);
                } else {
                    getSetterMethod().invoke(target, value == null ? 0 : Integer.parseInt(value));
                }
            } else if (getSetterMethod().getParameterTypes()[0].equals(Long.class) || "long".equals(getSetterMethod().getParameterTypes()[0].getName())) {
                if (option.args() == 0) {
                    getSetterMethod().invoke(target, 1);
                } else {
                    getSetterMethod().invoke(target, value == null ? 0 : Long.parseLong(value));
                }
            } else if (getSetterMethod().getParameterTypes()[0].equals(BigDecimal.class)) {
                getSetterMethod().invoke(target, value == null ? null : new BigDecimal(value));
            } else if (getSetterMethod().getParameterTypes()[0].equals(String.class)) {
                getSetterMethod().invoke(target, value);
            } else if (getSetterMethod().getParameterTypes()[0].isArray() && getSetterMethod().getParameterTypes()[0].getName().contains(String.class.getName())) {
                getSetterMethod().invoke(target, (Object)cmd.getOptionValues(option.shortOption()));
            } else if (getSetterMethod().getParameterTypes()[0].equals(Properties.class)) {
                getSetterMethod().invoke(target, cmd.getOptionProperties(option.shortOption()));
            } else {
                getOptionProperty().setValueInCommandLine(false);
                throw new RuntimeException("Could not resolve parameter type for " + getSetterMethod().toString());
            }
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
