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

import org.neovera.jdiablo.ExecutionState;
import org.neovera.jdiablo.annotation.Option;

public class LevelTwoALaunchable extends LevelOneLaunchable {
    
    @Option(shortOption = "x", longOption="option2x", description = "Description for level 2 option x", args=1)
    private String _option2x;

    public String getOption2x() {
        return _option2x;
    }

    public void setOption2x(String option2x) {
        _option2x = option2x;
    }

    public ExecutionState mainEntryPoint() {
        return ExecutionState.SUCCESS;
    }

}