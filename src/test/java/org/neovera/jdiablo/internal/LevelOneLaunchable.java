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

import java.util.List;

import org.neovera.jdiablo.ExecutionHandler;
import org.neovera.jdiablo.Launchable;
import org.neovera.jdiablo.OptionProperty;
import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.annotation.RawOptions;
import org.neovera.jdiablo.annotation.State;
import org.neovera.jdiablo.annotation.UseEnvironment;
import org.neovera.jdiablo.annotation.UseOptionValueProviders;
import org.neovera.jdiablo.environment.ClasspathOptions;
import org.neovera.jdiablo.environment.Help;
import org.neovera.jdiablo.environment.PropertyPlaceholder;
import org.neovera.jdiablo.environment.SpringContext;
import org.neovera.jdiablo.environment.SpringContextProvider;
import org.neovera.jdiablo.environment.SpringEnvironment;

/**
 * Launchable base class to test inheritance of annotated options, environments, etc.
 */
@UseEnvironment({Help.class,SpringEnvironment.class})
@UseOptionValueProviders({ClasspathOptions.class,SpringContextProvider.class})
@SpringContext("ioc/launchable-annotation-test.xml")
public abstract class LevelOneLaunchable implements Launchable {
    
    @Option(shortOption = "a", longOption="option1a",  description = "Description for level 1 option a", args=1)
    private String _option1a;
    @Option(shortOption = "b", longOption="option1b",  description = "Description for level 1 option b", args=1)
    private String _option1b;
    @Option(shortOption = "c", longOption="option1c",  description = "Description for level 1 option c", args=1)
    @PropertyPlaceholder("LevelOneLaunchable.option1c")
    private String _option1c;
    
    public String getOption1a() {
        return _option1a;
    }
    public void setOption1a(String option1a) {
        _option1a = option1a;
    }
    public String getOption1b() {
        return _option1b;
    }
    public void setOption1b(String option1b) {
        _option1b = option1b;
    }
    
    public String getOption1c() {
        return _option1c;
    }
    public void setOption1c(String option1c) {
        _option1c = option1c;
    }

    
    
    @State
    private ExecutionHandler _builder;

    @RawOptions
    private List<? extends OptionProperty> _rawOptions;
    
    public ExecutionHandler getBuilder() {
        return _builder;
    }
    public void setBuilder(ExecutionHandler builder) {
        _builder = builder;
    }
    public List<? extends OptionProperty> getRawOptions() {
        return _rawOptions;
    }
    public void setRawOptions(List<? extends OptionProperty> rawOptions) {
        _rawOptions = rawOptions;
    }
    
    
    

}