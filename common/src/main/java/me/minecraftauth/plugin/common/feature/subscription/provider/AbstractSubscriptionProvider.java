/*-
 * LICENSE
 * MinecraftAuth Plugin - Common
 * -------------
 * Copyright (C) 2021 MinecraftAuth.me
 * -------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * END
 */

package me.minecraftauth.plugin.common.feature.subscription.provider;

import alexh.weak.Dynamic;
import me.minecraftauth.plugin.common.feature.subscription.RequireSubscriptionFeature;

public abstract class AbstractSubscriptionProvider implements MembershipProvider {

    public String getServerToken(RequireSubscriptionFeature feature, Dynamic providerConfig) {
        Dynamic authenticationDynamic = providerConfig.dget("Authentication");
        return authenticationDynamic.isPresent() ? authenticationDynamic.convert().intoString() : feature.getService().getServerToken();
    }

}
