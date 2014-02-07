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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.neovera.jdiablo.AbstractEnvironment;
import org.neovera.jdiablo.Environment;
import org.neovera.jdiablo.OptionProperty;
import org.neovera.jdiablo.annotation.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Bootstraps Spring and provides option values who's fields are annotated with PropertyPlaceholder (provided there is
 * a bean of the type PropertyPlaceholderProvider in Spring). If your context has an instance of 
 * AutowiredAnnotationBeanPostProcessor all fields marked with the @Inject annotation are automatically set. In addition,
 * if the Launchable class implements the BeanFactoryAware interface, the bean factory is also set.
 */
public class SpringEnvironment extends AbstractEnvironment {

    @Option(shortOption = "scf", longOption = "springContext", description = "Spring context file", hasArgs = true, valueSeparator = ',', required = true)
    private String[] _springContextFiles = new String[0];
    private ApplicationContext _context;

    private static Logger _logger = LoggerFactory.getLogger(SpringEnvironment.class);

    public String[] getSpringContextFiles() {
        return _springContextFiles;
    }

    public void setSpringContextFiles(String[] springContextFiles) {
        _springContextFiles = springContextFiles;
    }

    @Override
    public boolean start() {
        _context = new ClassPathXmlApplicationContext(getSpringContextFiles());
        return true;
    }

    @Override
    public void stop() {
        ((AbstractApplicationContext)_context).close();
    }

    @Override
    public void initialize(Environment environment, List<? extends OptionProperty> properties) {
        injectDependencies(environment);
    }

    @Override
    public void initialize(Object target, List<? extends OptionProperty> properties) {
        injectDependencies(target);
    }

    private void injectDependencies(Object object) {
        injectBeans(object);
        injectBeanFactory(object);
        injectProperties(object);
    }

    private void injectProperties(Object object) {
        Map<String, PropertyPlaceholderProvider> map = _context.getBeansOfType(PropertyPlaceholderProvider.class);
        PropertyPlaceholderProvider ppp = null;
        if (map.size() != 0) {
            ppp = map.values().iterator().next();
        }

        // Analyze members to see if they are annotated.
        Map<String, String> propertyNamesByField = new HashMap<String, String>();
        Class<?> clz = object.getClass();
        while (!clz.equals(Object.class)) {
            for (Field field : clz.getDeclaredFields()) {
                if (field.isAnnotationPresent(PropertyPlaceholder.class)) {
                    propertyNamesByField.put(field.getName().startsWith("_") ? field.getName().substring(1) : field.getName(), field.getAnnotation(PropertyPlaceholder.class)
                            .value());
                }
            }
            clz = clz.getSuperclass();
        }

        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(object.getClass());
        for (PropertyDescriptor pd : descriptors) {
            if (propertyNamesByField.keySet().contains(pd.getName())) {
                if (ppp == null) {
                    _logger.error("Field {} is annotated with PropertyPlaceholder but no bean of type "
                            + "PropertyPlaceholderProvider is defined in the Spring application context.", pd.getName());
                    break;
                } else {
                    setValue(pd, object, ppp.getProperty(propertyNamesByField.get(pd.getName())));
                }
            } else if (pd.getReadMethod() != null && pd.getReadMethod().isAnnotationPresent(PropertyPlaceholder.class)) {
                if (ppp == null) {
                    _logger.error("Field {} is annotated with PropertyPlaceholder but no bean of type "
                            + "PropertyPlaceholderProvider is defined in the Spring application context.", pd.getName());
                    break;
                } else {
                    setValue(pd, object, ppp.getProperty(pd.getReadMethod().getAnnotation(PropertyPlaceholder.class).value()));
                }
            }
        }
    }

    private void setValue(PropertyDescriptor pd, Object target, String value) {
        Method writeMethod = pd.getWriteMethod();
        if (writeMethod == null) {
            throw new RuntimeException("No write method found for property " + pd.getName());
        }
        try {
            if (writeMethod.getParameterTypes()[0].equals(Boolean.class) || "boolean".equals(writeMethod.getParameterTypes()[0].getName())) {
                writeMethod.invoke(target, value == null ? false : "true".equals(value));
            } else if (writeMethod.getParameterTypes()[0].equals(Integer.class) || "int".equals(writeMethod.getParameterTypes()[0].getName())) {
                writeMethod.invoke(target, value == null ? 0 : Integer.parseInt(value));
            } else if (writeMethod.getParameterTypes()[0].equals(Long.class) || "long".equals(writeMethod.getParameterTypes()[0].getName())) {
                writeMethod.invoke(target, value == null ? 0 : Long.parseLong(value));
            } else if (writeMethod.getParameterTypes()[0].equals(BigDecimal.class)) {
                writeMethod.invoke(target, value == null ? null : new BigDecimal(value));
            } else if (writeMethod.getParameterTypes()[0].equals(String.class)) {
                writeMethod.invoke(target, value);
            } else if (writeMethod.getParameterTypes()[0].isArray() && writeMethod.getParameterTypes()[0].getName().contains(String.class.getName())) {
                writeMethod.invoke(target, (Object[])value.split(","));
            } else {
                throw new RuntimeException("Could not resolve parameter type for " + writeMethod.toString());
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void injectBeanFactory(Object object) {
        if (object instanceof BeanFactoryAware) {
            ((BeanFactoryAware)object).setBeanFactory(_context);
        }
    }

    private void injectBeans(Object object) {
        AutowiredAnnotationBeanPostProcessor proc = _context.getBean(AutowiredAnnotationBeanPostProcessor.class);
        if (proc == null) {
            throw new RuntimeException("Could not find an autowired annotation bean post processor of type {} " + AutowiredAnnotationBeanPostProcessor.class.getName());
        } else {
            proc.processInjection(object);
        }
    }

}