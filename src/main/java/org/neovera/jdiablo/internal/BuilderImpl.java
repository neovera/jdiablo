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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Factory;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.neovera.jdiablo.BuilderFactory;
import org.neovera.jdiablo.Environment;
import org.neovera.jdiablo.EnvironmentBuilder;
import org.neovera.jdiablo.ExecutionHandler;
import org.neovera.jdiablo.ExecutionState;
import org.neovera.jdiablo.Executor;
import org.neovera.jdiablo.HelpProvider;
import org.neovera.jdiablo.Launchable;
import org.neovera.jdiablo.OptionProperty;
import org.neovera.jdiablo.OptionValueProvider;
import org.neovera.jdiablo.Specialization;
import org.neovera.jdiablo.TargetBuilder;
import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.annotation.RawOptions;
import org.neovera.jdiablo.annotation.SkipEnvironment;
import org.neovera.jdiablo.annotation.State;
import org.neovera.jdiablo.annotation.UseEnvironment;
import org.neovera.jdiablo.annotation.UseOptionValueProviders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderImpl implements TargetBuilder, EnvironmentBuilder, Executor, HelpProvider, ExecutionHandler {

    private BuilderFactory _builderFactory;
    private BuilderImpl _previousBuilder;
    private String[] _args = new String[0];
    private Class<? extends Launchable> _launchableClass;
    private Launchable _launchable;
    private List<OptionValueProvider> _optionValueProviders = new ArrayList<OptionValueProvider>();
    private List<OptionPropertySpi> _launchProperties;
    private List<OptionAnnotatedProperty> _propertyOptions = new ArrayList<OptionAnnotatedProperty>();
    private Options _cliOptions = new Options();

    private Map<Class<? extends Environment>, EnvContainer> _environments = new LinkedHashMap<Class<? extends Environment>, EnvContainer>();
    private Map<Class<? extends Environment>, Environment> _specializedEnvByClass = new HashMap<Class<? extends Environment>, Environment>();

    private static Logger _logger = LoggerFactory.getLogger(BuilderImpl.class);

    public BuilderImpl(BuilderFactory builderFactory) {
        _builderFactory = builderFactory;
    }

    public BuilderImpl(String[] args, BuilderFactory builderFactory) {
        _args = args;
        _builderFactory = builderFactory;
    }

    private BuilderImpl getPreviousBuilder() {
        return _previousBuilder;
    }

    private void setPreviousBuilder(BuilderImpl previousBuilder) {
        _previousBuilder = previousBuilder;
    }

    @SuppressWarnings("unchecked")
    public <E extends Environment> E getEnvironment(Class<E> clz) {
        EnvContainer container = _environments.get(clz);
        return container == null ? null : (E)container.getEnvironment();
    }

    public String[] getArgs() {
        return _args;
    }

    public <L extends Launchable> EnvironmentBuilder withTarget(final Class<L> launchableClass) {
        return withTarget(launchableClass, new NoopSpecialization<L>());
    }

    public <L extends Launchable> EnvironmentBuilder withTarget(Class<L> launchableClass, Specialization<L> specialization) {

        // Get list of ordered environments and store in environments. If the previous builder
        // is null, then simply add instances to environments and new environments. Otherwise, if
        // the previous environment has an instance, use that. Else create a new one and store in
        // environments and new environments.
        UseEnvironment ue = launchableClass.getAnnotation(UseEnvironment.class);

        Set<Class<? extends Environment>> envToSkip = new HashSet<Class<? extends Environment>>();
        SkipEnvironment se = launchableClass.getAnnotation(SkipEnvironment.class);
        if (se != null) {
            for (Class<? extends Environment> c : se.value()) {
                envToSkip.add(c);
            }
        }

        if (ue != null) {
            for (Class<? extends Environment> clz : ue.value()) {
                if (envToSkip.contains(clz)) {
                    continue;
                }

                if (getPreviousBuilder() == null) {
                    Environment e = SpecializationUtil.createOptionAwareProxy(clz);
                    _environments.put(clz, new EnvContainer(e, true));
                } else {
                    Environment e = getPreviousBuilder().getEnvironment(clz);
                    if (e == null) {
                        e = SpecializationUtil.createOptionAwareProxy(clz);
                        _environments.put(clz, new EnvContainer(e, true));
                    } else {
                        _environments.put(clz, new EnvContainer(e));
                    }
                }
            }
        }

        // Create a new instance of the launchable target and specialize it.
        L launchable = SpecializationUtil.createOptionAwareProxy(launchableClass);
        specialization.specialize(launchable);

        _launchableClass = launchableClass;
        _launchable = launchable;

        UseOptionValueProviders ovp = launchableClass.getAnnotation(UseOptionValueProviders.class);
        if (ovp != null) {
            for (Class<? extends OptionValueProvider> ovpClass : ovp.value()) {
                try {
                    _optionValueProviders.add(ovpClass.newInstance());
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return this;
    }

    public EnvironmentBuilder withNewInstanceOf(Class<? extends Environment> clz) {
        // New instance needs to be created only if we don't already have a new instance.
        if (!_environments.get(clz).isNew()) {
            Environment e = SpecializationUtil.createOptionAwareProxy(clz);
            _environments.get(clz).setEnvironment(e);
            _environments.get(clz).setNew(true);
        }
        return this;
    }

    public <E extends Environment> EnvironmentBuilder withSpecialization(Class<E> clz, Specialization<E> specialization) {
        // Specialization is applied only if our instance is new. However at the point of this
        // method being called, we don't know if the user will subsequently call
        // withNewInstanceOf(). Therefore we simply store specializations in the specialization
        // map. We replace new instances which have specializations with the specialized instances
        // in the build method.
        E e = SpecializationUtil.createOptionAwareProxy(clz);
        specialization.specialize(e);
        _specializedEnvByClass.put(clz, e);
        return this;
    }

    public Executor build() {
        // Merge specializations for any environments.
        for (Class<? extends Environment> clz : _environments.keySet()) {
            if (_environments.get(clz).isNew()) {
                if (_specializedEnvByClass.containsKey(clz)) {
                    _environments.get(clz).setEnvironment(_specializedEnvByClass.get(clz));
                } else if (_builderFactory.getSpecializations().containsKey(clz)) {
                    _environments.get(clz).setEnvironment(_builderFactory.getSpecializations().get(clz));
                }
            }
        }

        // Go through all Option annotations to build up the option set.
        for (Class<? extends Environment> clz : _environments.keySet()) {
            List<OptionPropertySpi> list = buildOptions(clz, _environments.get(clz).getEnvironment());
            for (OptionValueProvider ovp : _optionValueProviders) {
                ovp.provideOptionValues(clz, _environments.get(clz).getEnvironment(), list, _launchableClass, _launchable);
            }
            modifySpecializedOptions(clz, _environments.get(clz).getEnvironment(), list);

            _environments.get(clz).setOptionProperties(list);
            for (OptionPropertySpi optionProperty : list) {
                _propertyOptions.add(new OptionAnnotatedProperty(optionProperty));
            }
        }
        _launchProperties = buildOptions(_launchableClass, _launchable);
        for (OptionValueProvider ovp : _optionValueProviders) {
            ovp.provideOptionValues(_launchableClass, _launchable, _launchProperties);
        }
        modifySpecializedOptions(_launchableClass, _launchable, _launchProperties);

        for (OptionPropertySpi optionProperty : _launchProperties) {
            _propertyOptions.add(new OptionAnnotatedProperty(optionProperty));
        }

        return this;
    }

    private void modifySpecializedOptions(Class<?> clz, Object instance, List<OptionPropertySpi> list) {
        SetterMethodInterceptor interceptor = (SetterMethodInterceptor)((Factory)instance).getCallback(0);
        for (OptionPropertySpi optionPropertySpi : list) {
            if (interceptor.isPropertyNotRequired(optionPropertySpi.getPropertyName())) {
                optionPropertySpi.setOptionNotRequiredOverride(true);
            }
        }
    }

    private List<OptionPropertySpi> buildOptions(final Class<?> claz, Object instance) {

        List<OptionPropertySpi> list = new ArrayList<OptionPropertySpi>();

        Class<?> clz = claz;
        while (clz != null) {

            for (Field field : clz.getDeclaredFields()) {
                Option option = field.getAnnotation(Option.class);
                if (option != null) {
                    String fieldName = field.getName();
                    if (fieldName.startsWith("_")) {
                        fieldName = fieldName.substring(1);
                    }

                    Method method = BeanUtils.getWriteMethod(fieldName, clz);
                    if (method != null) {
                        OptionPropertySpi po = new OptionPropertyImpl();
                        po.setOption(option);
                        po.setPropertyName(fieldName);
                        po.setSetterMethod(method);
                        list.add(po);
                    } else {
                        _logger.error("No setter method for " + fieldName);
                    }
                }
            } // for (Field field : ...

            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clz);
            for (Method method : clz.getDeclaredMethods()) {
                Option option = method.getAnnotation(Option.class);
                if (option != null) {
                    for (PropertyDescriptor propertyDescriptor : descriptors) {
                        if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getReadMethod().equals(method)) {
                            String fieldName = propertyDescriptor.getName();
                            if (propertyDescriptor.getWriteMethod() == null) {
                                _logger.error("No setter method for {}" + fieldName);
                            } else {
                                OptionPropertySpi po = new OptionPropertyImpl();
                                po.setOption(option);
                                po.setPropertyName(fieldName);
                                po.setSetterMethod(propertyDescriptor.getWriteMethod());
                                list.add(po);
                            } // end else
                            break;
                        }
                    } // for (PropertyDescriptor ...
                }
            } // for (Method method : ...
            clz = clz.getSuperclass();
        }

        return list;
    }

    List<OptionAnnotatedProperty> getPropertyOptions() {
        return _propertyOptions;
    }
    
    Launchable getLaunchable() {
        return _launchable;
    }

    public ExecutionState execute() {
        return execute(true);
    }

    public ExecutionState executeNoCatch() {
        return execute(false);
    }

    private ExecutionState execute(boolean catchAndLogException) {
        ExecutionState state = ExecutionState.FAILURE;
        try {
            CommandLineParser parser = new GnuParser();
            for (OptionAnnotatedProperty propertyOption : _propertyOptions) {
                _cliOptions.addOption(propertyOption.getCliOption());
            }
            CommandLine cmd = parser.parse(_cliOptions, getArgs());

            // Bind option values to new environments only.
            for (Class<? extends Environment> clz : _environments.keySet()) {
                if (_environments.get(clz).isNew()) {
                    bind(clz, _environments.get(clz).getEnvironment(), cmd, _environments.get(clz).getOptionProperties());
                }
            }

            // Run through and start the environments that have not yet been started and allow all
            // to initialize the rest of the environments.
            boolean abort = false;
            // Keep track of environments that were started to stop just those in case of
            // abort request.
            List<Environment> started = new ArrayList<Environment>();
            for (Class<? extends Environment> clz : _environments.keySet()) {
                Environment environment = _environments.get(clz).getEnvironment();
                if (_environments.get(clz).isNew()) {
                    try {
                        abort = !environment.start();
                        started.add(environment);
                    } catch (RuntimeException e) {
                        abort = true;
                        if (catchAndLogException) {
                            _logger.error(e.getMessage(), e);
                        } else {
                            throw e;
                        }
                    }
                }
                if (!abort) {
                    for (Class<? extends Environment> c : _environments.keySet()) {
                        if (_environments.get(c).isNew()) {
                            environment.initialize(_environments.get(c).getEnvironment(), _environments.get(c).getOptionProperties());
                        }
                    }
                    environment.initialize(_launchable, _launchProperties);
                } else {
                    break;
                }
            }
            
            // Bind option values to the launch target last, to give these settings highest precedence.
            if (!abort) {
                bind(_launchableClass, _launchable, cmd, _launchProperties);
            }

            // Start launchable if not aborting.
            try {
                if (!abort) {
                    state = _launchable.mainEntryPoint();
                }
            } catch (RuntimeException e) {
                state = ExecutionState.LAUNCH_ERROR;
                if (catchAndLogException) {
                    _logger.error(e.getMessage(), e);
                } else {
                    throw e;
                }
            } finally {
                // Stop environments that were started.
                Collections.reverse(started);
                for (Environment environment : started) {
                    environment.stop();
                }
            }

            return state;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            showHelp();
            return ExecutionState.LAUNCH_ERROR;
        }
    }

    private void bind(Class<?> clz, Object instance, CommandLine cmd, List<? extends OptionProperty> propertyList) {
        try {
            for (OptionAnnotatedProperty propertyOption : _propertyOptions) {
                propertyOption.bind(cmd, instance);
            }

            injectState(clz, instance, propertyList);
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void injectState(final Class<?> claz, Object instance, List<? extends OptionProperty> propertyList) {
        Class<?> clz = claz;
        while (clz != null) {
            for (Field field : clz.getDeclaredFields()) {

                if (field.isAnnotationPresent(State.class)) {
                    if (field.getType().equals(ExecutionHandler.class)) {
                        BeanUtils.setProperty(instance, field.getName(), this);
                    } else if (field.getType().equals(HelpProvider.class)) {
                        BeanUtils.setProperty(instance, field.getName(), this);
                    }
                } else if (field.isAnnotationPresent(RawOptions.class)) {
                    if (field.getType().equals(String[].class)) {
                        BeanUtils.setProperty(instance, field.getName(), getArgs());
                    } else if (field.getType().equals(List.class)) {
                        BeanUtils.setProperty(instance, field.getName(), propertyList);
                    }
                }

            } // end for loop

            clz = clz.getSuperclass();
        }
    }

    public TargetBuilder getChainedExecutor() {
        BuilderImpl builder = (BuilderImpl)_builderFactory.create();
        builder.setPreviousBuilder(this);
        return builder;
    }

    /**
     * Prints out command line help.
     */
    public void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java <vm-options> MainClass <arguments>", _cliOptions);
    }
}
