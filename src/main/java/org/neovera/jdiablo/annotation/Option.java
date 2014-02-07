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
package org.neovera.jdiablo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation against properties in your Launchable class (or Environment) to define command line options
 * accepted by the program.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Option {

    /**
     * @return Usually a single character (occasionally two characters) option that is specified
     *         with a single hyphen.
     */
    public String shortOption();

    /**
     * @return A more descriptive option in camelCase specified with two hyphens in the command
     *         line.
     */
    public String longOption() default "";

    /**
     * @return Description of the option. This is echoed in the help output.
     */
    public String description();

    /**
     * @return if true the program will not execute without this option, if false the option is not
     *         required.
     */
    public boolean required() default false;

    /**
     * @return Number of arguments. Defaults to zero.
     */
    public int args() default 0;

    /**
     * @return Set to true to specify unlimited number of arguments (as opposed to args that
     *         specifies a specific number).
     */
    public boolean hasArgs() default false;

    /**
     * @return This option can have the specified number of optional arguments.
     */
    public int optionalArgs() default 0;

    /**
     * @return Name of the argument. Used in command line help display.
     */
    public String argName() default "";

    /**
     * @return Defines character to use to separate multi-valued arguments.
     */
    public char valueSeparator() default ' ';
}
