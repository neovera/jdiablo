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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neovera.jdiablo.BuilderFactory;
import org.neovera.jdiablo.Executor;
import org.neovera.jdiablo.OptionProperty;
import org.neovera.jdiablo.environment.Help;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LaunchableAnnotationsTest {


    /**
     * Test the @Environment via the LevelTwoALaunchable class, inherited from its LevelOneLaunchable base class.
     */
    public void testAnnotationEnvironmentHelp() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;
        
        Assert.assertNotNull(executorImpl.getEnvironment(Help.class));
    }

    /**
     * Test the @SkipEnvironment via the LevelTwoBLaunchable class, overriding its LevelOneLaunchable base class.
     */
    public void testAnnotationSkipEnvironmentHelp() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoBLaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;

        Assert.assertNull(executorImpl.getEnvironment(Help.class));
    }
    

    
    /**
     * Test the annotated control over the selection of one spring context  
     */
    public void testPropertyPlaceHolder() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;
        
        executor.execute();  // the injection occurs within the execution phase.
        
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(executorImpl.getLaunchable());
        
        Assert.assertEquals(launchable.getOption1c(),"Default value from spring environment via a resource file");
    }
    
    
    

    public void testAnnotationOptionInheritance() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;

        Map<String, OptionAnnotatedProperty> optionsByShortName = new HashMap<String, OptionAnnotatedProperty>();

        List<OptionAnnotatedProperty> propertyOptions = executorImpl.getPropertyOptions();
        {
            for (OptionAnnotatedProperty optionAnnotatedProperty : propertyOptions) {
                optionsByShortName.put(optionAnnotatedProperty.getOption().shortOption(), optionAnnotatedProperty);
            }
        }

        Assert.assertTrue(optionsByShortName.containsKey("a"));
        Assert.assertTrue(optionsByShortName.containsKey("b"));
        Assert.assertTrue(optionsByShortName.containsKey("x"));
        Assert.assertTrue(optionsByShortName.containsKey("h"));
    }
    
    
    /**
     * Test the injection of the RawOptions list back into the Launchable instance via the @RawOptions annotation is used.
     */
    public void testAnnotationRawOptions() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        
        executor.execute();  // the injection occurs within the execution phase.
        
        Map<String, OptionProperty> rawOptionByShortName = new HashMap<String, OptionProperty>();
        {
            LevelTwoALaunchable launchable = (LevelTwoALaunchable)(((BuilderImpl)executor).getLaunchable());
            List<? extends OptionProperty> propertyOptions = launchable.getRawOptions();
    
            {
                for (OptionProperty optionAnnotatedProperty : propertyOptions) {
                    rawOptionByShortName.put(optionAnnotatedProperty.getOption().shortOption(), optionAnnotatedProperty);
                }
            }
        }
        Assert.assertTrue(rawOptionByShortName.containsKey("a"));
        Assert.assertTrue(rawOptionByShortName.containsKey("b"));
        Assert.assertTrue(rawOptionByShortName.containsKey("x"));
    }
    
    /**
     * Test the injection of the builder into the launchable via the @Builder annotation.
     */
    public void testAnnotationData() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        
        executor.execute();  // the injection occurs within the execution phase.
        
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(((BuilderImpl)executor).getLaunchable());

        Assert.assertNotNull(launchable.getBuilder());
        Assert.assertEquals(executor, launchable.getBuilder()); // not a rigid constraint but useful to know if this changes.
    }
    
    /**
     * Test that arguments passed to the target take precedence over the values from a ClasspathOptions provider.
     */
    public void testArgumentOptionOverridesPQClassPathOption() {
        String option1bArgValue = "ABCDE";
        String[] args = {"--option1a",option1bArgValue};
        
        Executor executor = new BuilderFactory().create(args).withTarget(LevelTwoALaunchable.class).build();
        executor.execute();  // the injection occurs within the execution phase.
        
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(((BuilderImpl)executor).getLaunchable());

        Assert.assertEquals(launchable.getOption1a(), option1bArgValue);
    }
    
    /**
     * Test that arguments passed to the target take precedence over the values from a ClasspathOptions provider.
     */
    public void testArgumentOptionOverridesClassPathOption() {
        String option1bArgValue = "ABCDE";
        String[] args = {"--option1b",option1bArgValue};
        
        Executor executor = new BuilderFactory().create(args).withTarget(LevelTwoALaunchable.class).build();
        executor.execute();  // the injection occurs within the execution phase.
        
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(((BuilderImpl)executor).getLaunchable());

        Assert.assertEquals(launchable.getOption1b(), option1bArgValue);
    }
    
    /**
     * Test that arguments passed to the target take precedence over the values from a PropertyPlaceholder.
     */
    public void testArgumentOptionOverridesPropertyPlaceholderOption() {
        String option1cArgValue = "ABCDE";
        String[] args = {"--option1c",option1cArgValue};
        
        Executor executor = new BuilderFactory().create(args).withTarget(LevelTwoALaunchable.class).build();
        executor.execute();  // the injection occurs within the execution phase.
        
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(((BuilderImpl)executor).getLaunchable());

        Assert.assertEquals(launchable.getOption1c(), option1cArgValue);
    }
   
    
    /**
     * Test the @UseOptionValueProviders ClasspathOptions : resource location option 1
     */
    public void testUseOptionValueProvidersClasspathOptionsPackageLocatedResource() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(executorImpl.getLaunchable());
        
        Assert.assertEquals(launchable.getOption1a(),"Default value From classpath resource file in package folder");
        
    }
    
    /**
     * Test the @UseOptionValueProviders ClasspathOptions : resource location option 2
     */
    public void testUseOptionValueProvidersClasspathOptionsPackageNamedResource() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoALaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;
        LevelTwoALaunchable launchable = (LevelTwoALaunchable)(executorImpl.getLaunchable());
        
        Assert.assertEquals(launchable.getOption1b(),"Default value from classpath resource file located in root package using package-qualified-class naming convention");
        
    }
    
    /**
     * Test the @UseOptionValueProviders ClasspathOptions : for all supported option types.
     */
    public void testUseOptionValueProvidersClasspathOptionsAllTypes() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoCLaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;
        LevelTwoCLaunchable launchable = (LevelTwoCLaunchable)(executorImpl.getLaunchable());
                
        // Failing for two reasons:
        //  1. ClasspathOptions needs to support longOption option name rather than bean property.
        //  2. Only String type options are being handled.
        Assert.assertEquals(launchable.getStringOption(),"stringFromCPProps");
        Assert.assertEquals(launchable.getBigDecimalOption(),new BigDecimal("11.11111"));
        Assert.assertEquals(launchable.getIntegerOption(),new Integer(11));
        Assert.assertEquals(launchable.getLongOption(),new Long(111));
        Assert.assertEquals(launchable.getBooleanOption(),Boolean.TRUE);
        Assert.assertEquals(Arrays.asList(launchable.getStringArrayOption()),Arrays.asList("string1FromCPProps","string2FromCPProps","string3FromCPProps"));
    }
    
    /**
     * Test the @UseOptionValueProviders ClasspathOptions : using longOption key instead of property name
     */
    public void testUseOptionValueProvidersClasspathOptions() {
        Executor executor = new BuilderFactory().create().withTarget(LevelTwoDLaunchable.class).build();
        BuilderImpl executorImpl = (BuilderImpl)executor;
        LevelTwoDLaunchable launchable = (LevelTwoDLaunchable)(executorImpl.getLaunchable());
                
        Assert.assertEquals(launchable.getStringOption(),"stringFromCPProps");
    }

}