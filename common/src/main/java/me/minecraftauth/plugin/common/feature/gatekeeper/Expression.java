/*
 * Copyright 2021-2022 MinecraftAuth.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.minecraftauth.plugin.common.feature.gatekeeper;

import lombok.Getter;

public class Expression extends com.udojava.evalex.Expression {

    @Getter int successCount = 0;

    public Expression(String expression) {
        super(expression);
    }

    public int incrementSuccessCount() {
        return ++successCount;
    }

}
