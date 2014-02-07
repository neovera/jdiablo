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

import java.math.BigDecimal;
import java.util.Properties;

import org.neovera.jdiablo.ExecutionState;
import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.annotation.SkipEnvironment;
import org.neovera.jdiablo.environment.Help;

public class LevelTwoCLaunchable extends LevelOneLaunchable {
    
    @Option(shortOption = "s", longOption = "string-option", description = "a String option", required = true, args = 1)
    private String _stringOption;

    @Option(shortOption = "i", longOption = "integer-option", description = "an Integer option", required = true, args = 1)
    private Integer _integerOption;

    @Option(shortOption = "b", longOption = "boolean-option", description = "a Boolean option", required = true)
    private Boolean _booleanOption;

    @Option(shortOption = "l", longOption = "long-option", description = "a Long option", required = true, args = 1)
    private Long _longOption;

    @Option(shortOption = "d", longOption = "bigdecimal-option", description = "a BigDecimal option", required = true, args = 1)
    private BigDecimal _bigDecimalOption;
    
    @Option(shortOption = "S", longOption = "stringarray-option", description = "string-array-option", hasArgs = true, valueSeparator = ',', required=true)
    private String[] _stringArrayOption = new String[0];
    
    @Option(shortOption = "p", longOption = "properties-option", description = "properties-option", hasArgs = true, valueSeparator = '=', argName = "key=value")
    private Properties _propertiesOption = new Properties();
    

    public String getStringOption() {
        return _stringOption;
    }


    public void setStringOption(String stringOption) {
        _stringOption = stringOption;
    }


    public Integer getIntegerOption() {
        return _integerOption;
    }


    public void setIntegerOption(Integer integerOption) {
        _integerOption = integerOption;
    }


    public Boolean getBooleanOption() {
        return _booleanOption;
    }


    public void setBooleanOption(Boolean booleanOption) {
        _booleanOption = booleanOption;
    }


    public Long getLongOption() {
        return _longOption;
    }


    public void setLongOption(Long longOption) {
        _longOption = longOption;
    }


    public BigDecimal getBigDecimalOption() {
        return _bigDecimalOption;
    }


    public void setBigDecimalOption(BigDecimal bigDecimalOption) {
        _bigDecimalOption = bigDecimalOption;
    }


    public String[] getStringArrayOption() {
        return _stringArrayOption;
    }


    public void setStringArrayOption(String[] stringArrayOption) {
        _stringArrayOption = stringArrayOption;
    }


    public Properties getPropertiesOption() {
        return _propertiesOption;
    }


    public void setPropertiesOption(Properties propertiesOption) {
        _propertiesOption = propertiesOption;
    }

    
    public ExecutionState mainEntryPoint() {
        return ExecutionState.SUCCESS;
    }

}