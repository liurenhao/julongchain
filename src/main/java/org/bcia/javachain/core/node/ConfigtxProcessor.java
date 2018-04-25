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
package org.bcia.javachain.core.node;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.exception.ValidateException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.common.util.ValidateUtils;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.core.ledger.ITxSimulator;
import org.bcia.javachain.core.ledger.customtx.IProcessor;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;

/**
 * 对象
 *
 * @author zhouhui
 * @date 2018/4/25
 * @company Dingxuan
 */
public class ConfigtxProcessor implements IProcessor {
    private static JavaChainLog log = JavaChainLogFactory.getLog(NodeGrpcServer.class);

    private static final String RESOURCES_CONFIG_KEY = "resourcesconfigtx.RESOURCES_CONFIG_KEY";
    private static final String GROUP_CONFIG_KEY = "resourcesconfigtx.GROUP_CONFIG_KEY";
    private static final String NODE_NAMESPACE = "";

    @Override
    public void generateSimulationResults(Common.Envelope txEnvelope, ITxSimulator simulator, boolean
            initializingLedger)
            throws LedgerException {
        Common.Payload payload = null;
        try {
            payload = Common.Payload.parseFrom(txEnvelope.getPayload());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }

        Common.GroupHeader groupHeader = null;
        try {
            groupHeader = Common.GroupHeader.parseFrom(payload.getHeader().getGroupHeader());
        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }

        switch (groupHeader.getType()) {
            case Common.HeaderType.CONFIG_VALUE:
                processGroupConfigTx(groupHeader.getGroupId(), txEnvelope, simulator);
                break;
            case Common.HeaderType.NODE_RESOURCE_UPDATE_VALUE:
                processResourceConfigTx(groupHeader.getGroupId(), txEnvelope, simulator, initializingLedger);
                break;
            default:
                log.warn("UnSupport type yet");
                break;
        }
    }

    private void processGroupConfigTx(String groupId, Common.Envelope txEnvelope, ITxSimulator simulator) throws
            LedgerException {
        try {
            Configtx.ConfigEnvelope configEnvelope = EnvelopeHelper.getConfigEnvelopeFrom(txEnvelope);
            Configtx.Config groupConfig = configEnvelope.getConfig();
            ValidateUtils.isNotNull(groupConfig, "configEnvelope.getConfig can not be null");

            persistConfig(simulator, GROUP_CONFIG_KEY, groupConfig);


        } catch (InvalidProtocolBufferException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        } catch (ValidateException e) {
            log.error(e.getMessage(), e);
            throw new LedgerException(e);
        }

    }


    private void processResourceConfigTx(String groupId, Common.Envelope txEnvelope, ITxSimulator simulator, boolean
            initializingLedger) {
    }

    private void persistConfig(ITxSimulator simulator, String key, Configtx.Config groupConfig) throws LedgerException {
        simulator.setState(NODE_NAMESPACE, key, groupConfig.toByteArray());
    }
}
