/**
 * Copyright Dingxuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.common.resourceconfig.config;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.peer.Resources;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * API配置对象
 *
 * @author zhouhui
 * @date 2018/4/19
 * @company Dingxuan
 */
public class ApiConfig implements IApiConfig {
    private static final String API_PATH_PREFIX = "/Resources/APIs/";
    private Map<String, String> policyRefs;

    public ApiConfig(Configtx.ConfigTree tree) throws ValidateException, InvalidProtocolBufferException {
        this.policyRefs = new HashMap<String, String>();

        if (tree != null && tree.getChildsCount() > 0) {
            throw new ValidateException("apis does not support child");
        }

        if (tree != null && tree.getValuesCount() > 0) {
            Iterator<Map.Entry<String, Configtx.ConfigValue>> iterator = tree.getValuesMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Configtx.ConfigValue> entry = iterator.next();
                String key = entry.getKey();
                Configtx.ConfigValue value = entry.getValue();

                Resources.APIResource apiResource = Resources.APIResource.parseFrom(value.getValue());
                if (apiResource.getPolicyRef().startsWith("/")) {
                    policyRefs.put(key, apiResource.getPolicyRef());
                } else {
                    policyRefs.put(key, API_PATH_PREFIX + apiResource.getPolicyRef());
                }
            }
        }
    }
}
