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
package org.neovera.jdiablo.environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.neovera.jdiablo.Environment;
import org.neovera.jdiablo.Launchable;
import org.neovera.jdiablo.OptionPropertyAccessor;
import org.neovera.jdiablo.OptionValueProvider;
import org.neovera.jdiablo.internal.TargetOptionBinder;
import org.neovera.jdiablo.internal.convert.StringLiteralOptionValueFacets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhances the target object by injecting default values for the target's class. These values are
 * managed in classpath property files for this class (and any superclasses). This allows runtime
 * environment default values to be applied to many targets in an environment agnostic manner, by
 * providing a classpath resource for the common project-specific superclass of many target
 * classes. Property files are managed as classpath resources using one of these conventions: 
 *   1. /full.pkg.qualified.Class-options.properties (at the root package level) 
 *   2. /full/pkg/qualified/Class-options.properties 
 *     ie /com.company.proj.MailServerMain-options.properties or
 *        /com/company/proj/MailServerMain-options.properties
 */
public class ClasspathOptions implements OptionValueProvider {

    private final static String CLASS_PROPERTYFILE_SUFFIX = "-options.properties";

    List<Map<String, String>> _optionsBySuperclasses;

    private static Logger _logger = LoggerFactory.getLogger(ClasspathOptions.class);

    public void provideOptionValues(Class<? extends Environment> clz, Environment environment, List<? extends OptionPropertyAccessor> list,
            Class<? extends Launchable> launchableClass, Launchable launchable) {
        provideOptionValues(environment, list);
    }

    public void provideOptionValues(Class<? extends Launchable> launchableClass, Launchable launchable, List<? extends OptionPropertyAccessor> launchProperties) {
        provideOptionValues(launchable, launchProperties);
    }

    private void provideOptionValues(Object target, List<? extends OptionPropertyAccessor> optionProperties) {

        if (!optionProperties.isEmpty()) {

            List<Map<String, String>> optionsBySuperclasses = loadOptionsHierarchy(target.getClass());

            for (OptionPropertyAccessor optionProperty : optionProperties) {
                String optionValue = getOptionValue(optionProperty, optionsBySuperclasses);
                if (optionValue != null) {
                    try {
                        TargetOptionBinder optionBinder = new TargetOptionBinder(optionProperty);
                        optionBinder.bind(new StringLiteralOptionValueFacets(optionValue), target);
                    } catch (Exception e) {
                        String msg = "While initializing target with class options loaded from classpath";
                        _logger.error(msg, e);
                        throw new RuntimeException(msg, e);
                    }
                }
            }
        }
    }

    /**
     * Finds the most specific value of a given option from the hierarchy of Property sets loaded as
     * classpath resources.
     *
     * @param optionProperty
     * @param optionsBySuperclasses
     * @return most specific property file managed value, or null if nothing found
     */
    private String getOptionValue(OptionPropertyAccessor optionProperty, List<Map<String, String>> optionsBySuperclasses) {
        String value = null;
        // ordering of these class maps is from specific to general, up the class hierarchy.
        for (Map<String, String> map : optionsBySuperclasses) {
            value = map.get(optionProperty.getPropertyName());
            if (value != null) {
                _logger.info("Using option: " + optionProperty.getPropertyName() + " specified in classpath properties file.");
                break;
            }
        }
        return value;
    }

    /**
     * Load the property files into a list of maps according to the class hierachy of the specified
     * leafClass, from most specific, to the top level.
     */
    private List<Map<String, String>> loadOptionsHierarchy(Class<?> leafClass) {
        List<Map<String, String>> optionsBySuperclasses = new ArrayList<Map<String, String>>();

        Class<?> clz = leafClass;
        while (clz != null) {
            Map<String, String> aClzOptions = loadOptions(clz);
            if (aClzOptions != null) {
                optionsBySuperclasses.add(aClzOptions);
            }
            clz = clz.getSuperclass();
        }

        return optionsBySuperclasses;
    }

    private Map<String, String> loadOptions(Class<?> exactClass) {
        List<String> resourceFilesToTry = new ArrayList<String>(2);

        resourceFilesToTry.add(exactClass.getCanonicalName().replace(".", "/") + CLASS_PROPERTYFILE_SUFFIX);
        resourceFilesToTry.add(exactClass.getCanonicalName() + CLASS_PROPERTYFILE_SUFFIX);

        Map<String, String> optionsForClass = null;

        InputStream is = null;
        for (String aPath : resourceFilesToTry) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(aPath);
            if (is != null) {
                try {
                    optionsForClass = loadPropertiesFromStream(is);
                } catch (IOException e) {
                    String msg = "While loading resource " + aPath + " via class loader";
                    _logger.error(msg, e);
                    throw new RuntimeException(msg, e);
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                }
                break;
            }
        }
        return optionsForClass;
    }

    /**
     * To avoid using Properties.
     *
     * @param is
     * @return Properties as a map
     * @throws IOException
     */
    private Map<String, String> loadPropertiesFromStream(InputStream is) throws IOException {
        Map<String, String> data = new HashMap<String, String>();

        Properties props = new Properties();
        props.load(is);

        for (Iterator<Object> iter = props.keySet().iterator(); iter.hasNext();) {
            String o = (String)iter.next();
            data.put(o, (String)(props.get(o)));
        }

        return data;
    }

}
