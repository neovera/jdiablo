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

import org.neovera.jdiablo.AbstractEnvironment;
import org.neovera.jdiablo.HelpProvider;
import org.neovera.jdiablo.annotation.Option;
import org.neovera.jdiablo.annotation.State;

/**
 * Add this environment to enable a -h (or --help) option that what options are available and the description of the 
 * options.
 */
public class Help extends AbstractEnvironment {

    @Option(shortOption = "h", longOption = "help", description = "Command line help")
    private boolean _help;
    @State
    private HelpProvider _helpProvider;

    public boolean isHelp() {
        return _help;
    }

    public void setHelp(boolean help) {
        _help = help;
    }

    public HelpProvider getHelpProvider() {
        return _helpProvider;
    }

    public void setHelpProvider(HelpProvider helpProvider) {
        _helpProvider = helpProvider;
    }

    @Override
    public boolean start() {
        if (isHelp()) {
            getHelpProvider().showHelp();
            return false;
        }
        return true;
    }

    @Override
    public void stop() {
    }

}
