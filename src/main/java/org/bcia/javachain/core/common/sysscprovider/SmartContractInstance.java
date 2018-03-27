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
package org.bcia.javachain.core.common.sysscprovider;

/**
 * 类描述
 *
 * @author wanliangbing
 * @date 2018/3/18
 * @company Dingxuan
 */
public class SmartContractInstance {

    private String smartContractID;
    private String smartContractName;
    private String smartContractVersion;

    public String getSmartContractID() {
        return smartContractID;
    }

    public void setSmartContractID(String smartContractID) {
        this.smartContractID = smartContractID;
    }

    public String getSmartContractName() {
        return smartContractName;
    }

    public void setSmartContractName(String smartContractName) {
        this.smartContractName = smartContractName;
    }

    public String getSmartContractVersion() {
        return smartContractVersion;
    }

    public void setSmartContractVersion(String smartContractVersion) {
        this.smartContractVersion = smartContractVersion;
    }
}