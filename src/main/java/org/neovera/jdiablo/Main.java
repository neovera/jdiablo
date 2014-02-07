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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an alternate JVM entry point for diablo managed processes that allows passing in the
 * class of the Launchable as the first argument. Command line call: java &lt;vm-options&gt;
 * org.neovera.jdiablo.internal.Main &lt;FQCN of diablo launch target&gt; &lt;command line options for launch
 * target&gt;
 */
public class Main {

    private static Logger _logger = LoggerFactory.getLogger(Main.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        Main main = new Main();
        main.execute(args);
    }

    @SuppressWarnings("unchecked")
    private void execute(String[] args) {
        if (args.length == 0) {
            _logger.error("No diablo target specified. The first argument should " + "be the fully qualified class name of the diablo launch class "
                    + "followed by arguments for the diablo launch class.");
            return;
        }

        try {
            Class<? extends Launchable> clz = (Class<? extends Launchable>)Class.forName(args[0]);

            String[] a = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                a[i - 1] = args[i];
            }

            new BuilderFactory().create(a).withTarget(clz).build().execute();
        } catch (ClassNotFoundException e) {
            _logger.error(e.getMessage(), e);
        }
    }

}