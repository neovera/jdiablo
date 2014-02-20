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

/**
 * Exposes the supported value types of an option value from
 * a given string value.  
 * 
 * Only the literal option value facet is supported.
 */
public class StringLiteralOptionValueFacets implements OptionValueFacets {
    String _value;
    
    /**
     * @param commandLine
     */
    public StringLiteralOptionValueFacets(String value) {
        super();
        _value = value;
    }

    public String getOptionValue() {
        return _value;
    }

    public String[] getOptionValues() {
        if ( _value == null ) return null;
        String[] split = _value.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        
        String[] values = new String[split.length];
        
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            values[i] = s.replaceAll("^\"|\"$", "");
        }
        
        return values;
    }
    
    public Properties getOptionProperties() throws UnsupportedValueFacetException {
        throw new UnsupportedValueFacetException();
    }
    
}