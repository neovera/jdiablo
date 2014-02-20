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

import org.neovera.jdiablo.OptionPropertyAccessor;
import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.internal.convert.OptionValueFacets;
import org.neovera.jdiablo.internal.convert.UnsupportedValueFacetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facilitates binding an option value on a specified object, handling type conversions.
 */
public class TargetOptionBinder {

    private static Logger _logger = LoggerFactory.getLogger(TargetOptionBinder.class);
    
    private OptionPropertyAccessor _optionProperty;

    public TargetOptionBinder(OptionPropertyAccessor optionProperty) {
        setOptionProperty(optionProperty);
    }

    /**
     * @param optionValueFacets source from which the option's value can be retrieved.
     * @param target Object to apply to.
     */
    public boolean bind(OptionValueFacets optionValueFacets, Object target) {
        boolean isValueBound = true;
        
        Option option = getOption();
        try {
            if (!getSetterMethod().getDeclaringClass().isAssignableFrom(target.getClass())) {
                return false;
            }

            if (_logger.isTraceEnabled()) {
                _logger.trace(getSetterMethod().getParameterTypes()[0].getName());
                _logger.trace(option.shortOption());
                _logger.trace(target.getClass().getName());
            }

            String value = optionValueFacets.getOptionValue();
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
                try {
                    getSetterMethod().invoke(target, (Object)optionValueFacets.getOptionValues());
                } catch (UnsupportedValueFacetException e) {
                    _logger.warn(optionValueFacets.getClass().getSimpleName() + " does not support setting string[] options");
                }
            } else if (getSetterMethod().getParameterTypes()[0].equals(Properties.class)) {
                try {
                    getSetterMethod().invoke(target, optionValueFacets.getOptionProperties());
                } catch (UnsupportedValueFacetException e) {
                    _logger.warn(optionValueFacets.getClass().getSimpleName() + " does not support setting Properties options");
                }
            } else {
                isValueBound = false;
                throw new RuntimeException("Could not resolve parameter type for " + getSetterMethod().toString());
            }
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return isValueBound;
    }
    
    private Option getOption() {
        return getOptionProperty().getOption();
    }

    private Method getSetterMethod() {
        return getOptionProperty().getSetterMethod();
    }

    private OptionPropertyAccessor getOptionProperty() {
        return _optionProperty;
    }

    private void setOptionProperty(OptionPropertyAccessor optionProperty) {
        _optionProperty = optionProperty;
    }

}
