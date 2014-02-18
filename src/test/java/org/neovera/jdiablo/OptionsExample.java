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

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.annotation.RawOptions;
import org.neovera.jdiablo.annotation.UseEnvironment;
import org.neovera.jdiablo.annotation.UseOptionValueProviders;
import org.neovera.jdiablo.environment.ClasspathOptions;
import org.neovera.jdiablo.environment.Help;

@UseEnvironment({Help.class})
//The following directive enables default option values to be declared in an associated properties files.
@UseOptionValueProviders({ClasspathOptions.class})
public class OptionsExample implements Launchable {

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
    private String[] _stringArrayOptions = new String[0];
    
    @Option(shortOption = "p", longOption = "properties-option", description = "properties-option", hasArgs = true, valueSeparator = '=', argName = "key=value")
    private Properties _propertiesOption = new Properties();
    
    @RawOptions  // For those rare cases where access to the main's args is needed 
    private String[] _rawArgs;

    @RawOptions  // For those rare cases where access to the list of declared options is needed 
    private List<? extends OptionProperty> _rawOptions;
    
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

    public String[] getStringArrayOptions() {
        return _stringArrayOptions;
    }

    public void setStringArrayOptions(String[] stringArrayOptions) {
        _stringArrayOptions = stringArrayOptions;
    }
    
    public Properties getPropertiesOption() {
        return _propertiesOption;
    }

    public void setPropertiesOption(Properties propertiesOption) {
        _propertiesOption = propertiesOption;
    }

    public String[] getRawArgs() {
        return _rawArgs;
    }

    public void setRawArgs(String[] rawArgs) {
        _rawArgs = rawArgs;
    }

    public List<? extends OptionProperty> getRawOptions() {
        return _rawOptions;
    }

    public void setRawOptions(List<? extends OptionProperty> rawOptions) {
        _rawOptions = rawOptions;
    }

    public ExecutionState mainEntryPoint() {
        System.out.println("Initialized option values:");
        System.out.println("string-option: "+getStringOption());
        System.out.println("boolean-option: "+getBooleanOption());
        System.out.println("integer-option: "+getIntegerOption());
        System.out.println("long-option: "+getLongOption());
        System.out.println("bigdecimal-option: "+getBigDecimalOption());
        System.out.print("string-array-option: [");
        for (int i=0; i< getStringArrayOptions().length; i++) {
            System.out.print(getStringArrayOptions()[i]);
            System.out.print(i < getStringArrayOptions().length-1 ? "," : "]");
        }
        System.out.println("\nproperties-option: "+getPropertiesOption());

        System.out.println();
        System.out.println("Access to your annotated options:");
        for (OptionProperty option : _rawOptions) {
            System.out.println(option.getOption().shortOption()+":"+option.getOption().longOption()+":"+option.getPropertyName());
        }
        
        System.out.println();
        System.out.println("Access to the args passed to your main():");
        for (String s : _rawArgs) {
            System.out.println(s);
        }
        
        return ExecutionState.SUCCESS;
    }

    public static void main(String[] args) {
        new BuilderFactory().create(args).withTarget(OptionsExample.class).build().execute();
    }

}
