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

import javax.inject.Inject;

import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.annotation.UseEnvironment;
import org.neovera.jdiablo.annotation.UseOptionValueProviders;
import org.neovera.jdiablo.environment.Help;
import org.neovera.jdiablo.environment.PropertyPlaceholder;
import org.neovera.jdiablo.environment.SpringContext;
import org.neovera.jdiablo.environment.SpringContextProvider;
import org.neovera.jdiablo.environment.SpringEnvironment;

@UseEnvironment({Help.class, SpringEnvironment.class})
@UseOptionValueProviders({SpringContextProvider.class})  // This is required when using the @SpringContext annotation
@SpringContext({"ioc/spring-context-1.xml", "ioc/spring-context-2.xml"})  // Note: multiple spring context files can be specified.
public class SpringExample implements Launchable {
    
    @Inject
    private String service1;
    
    @Inject
    private String service2;
    
    @Option(shortOption = "r", longOption="root-directory", description = "the root file directory", required = true, args = 1)
    private String root;
    
    @Option(shortOption = "f", longOption="filter", description = "the filename filter (regex)", required = false, args = 1)
    // An option can be injected from Spring via the MapBasedPropertyPlaceholderProvider bean. 
    @PropertyPlaceholder("filter")   
    private String filter;
    
    public ExecutionState mainEntryPoint() {
        System.out.println("root: " + getRoot());
        System.out.println("filter: " + getFilter());
        
        System.out.println("service1: "+ service1);
        System.out.println("service2: "+ service2);
        
        return ExecutionState.SUCCESS;
    }
    

    public String getRoot() {
        return root;
    }


    public void setRoot(String root) {
        this.root = root;
    }


    public String getFilter() {
        return filter;
    }


    public void setFilter(String filter) {
        this.filter = filter;
    }


    public static void main(String[] args) {
        new BuilderFactory().create(args).withTarget(SpringExample.class).build().execute();
    }

}