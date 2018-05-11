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
package org.bcia.javachain.node.common.helper;

import com.google.protobuf.Message;
import org.bcia.javachain.common.groupconfig.value.*;
import org.bcia.javachain.common.groupconfig.GroupConfigConstant;
import org.bcia.javachain.common.policies.IConfigPolicy;
import org.bcia.javachain.common.policies.ImplicitMetaAnyPolicy;
import org.bcia.javachain.common.policies.ImplicitMetaMajorityPolicy;
import org.bcia.javachain.common.policies.SignaturePolicy;
import org.bcia.javachain.msp.util.MspConfigHelper;
import org.bcia.javachain.protos.common.Configtx;
import org.bcia.javachain.protos.common.Policies;
import org.bcia.javachain.protos.msp.MspConfigPackage;
import org.bcia.javachain.protos.node.Configuration;
import org.bcia.javachain.tools.configtxgen.entity.GenesisConfig;

import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.tools.configtxgen.entity.GenesisConfig.DEFAULT_ADMIN_PRINCIPAL;

/**
 * 类描述
 *
 * @author zhouhui
 * @date 2018/3/9
 * @company Dingxuan
 */
public class ConfigTreeHelper {
    /**
     * 为ConfigTree的构造器增加策略
     *
     * @param configTreeBuilder
     * @param key
     * @param policy
     * @param modPolicy
     */
    private static void addPolicy(Configtx.ConfigTree.Builder configTreeBuilder, String key, Policies.Policy policy,
                                  String modPolicy) {
        //首先构造ConfigPolicy
        Configtx.ConfigPolicy.Builder configPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        configPolicyBuilder.setPolicy(policy);
        configPolicyBuilder.setModPolicy(modPolicy);
        Configtx.ConfigPolicy configPolicy = configPolicyBuilder.build();

        //基于原ConfigTree构造新的ConfigTree
        configTreeBuilder.putPolicies(key, configPolicy);
    }

    /**
     * 为ConfigTree的构造器增加Value
     *
     * @param configTreeBuilder
     * @param key
     * @param message
     * @param modPolicy
     */
    private static void addValue(Configtx.ConfigTree.Builder configTreeBuilder, String key, Message message,
                                 String modPolicy) {
        //首先构造ConfigValue
        Configtx.ConfigValue.Builder configValueBuilder = Configtx.ConfigValue.newBuilder();
        configValueBuilder.setValue(message.toByteString());
        configValueBuilder.setModPolicy(modPolicy);
        Configtx.ConfigValue configValue = configValueBuilder.build();

        configTreeBuilder.putValues(key, configValue);
    }

    /**
     * 为ConfigTree的构造器增加Value
     *
     * @param configTreeBuilder
     * @param configValue
     * @param modPolicy
     */
    private static void addValue(Configtx.ConfigTree.Builder configTreeBuilder, IConfigValue configValue, String
            modPolicy) {
        addValue(configTreeBuilder, configValue.getKey(), configValue.getValue(), modPolicy);
    }

    /**
     * 获取默认的权限体系
     *
     * @return
     */
    private static Map<String, Configtx.ConfigPolicy> getDefaultImplicitMetaPolicy() {
        //首先创建默认的权限体系
        Map<String, Configtx.ConfigPolicy> defaultPolicies = new HashMap<String, Configtx.ConfigPolicy>();

        //构造管理员的内置策略(需要过半子策略被满足)
        Configtx.ConfigPolicy.Builder adminPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        adminPolicyBuilder.setPolicy(new ImplicitMetaMajorityPolicy(GroupConfigConstant.POLICY_ADMINS).getValue());
        adminPolicyBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
        defaultPolicies.put(GroupConfigConstant.POLICY_ADMINS, adminPolicyBuilder.build());

        //构造可读人员的内置策略(任意子策略被满足即可)
        Configtx.ConfigPolicy.Builder readerPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        adminPolicyBuilder.setPolicy(new ImplicitMetaAnyPolicy(GroupConfigConstant.POLICY_READERS).getValue());
        adminPolicyBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
        defaultPolicies.put(GroupConfigConstant.POLICY_READERS, readerPolicyBuilder.build());

        //构造可写人员的内置策略(任意子策略被满足即可)
        Configtx.ConfigPolicy.Builder writerPolicyBuilder = Configtx.ConfigPolicy.newBuilder();
        adminPolicyBuilder.setPolicy(new ImplicitMetaAnyPolicy(GroupConfigConstant.POLICY_WRITERS).getValue());
        adminPolicyBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
        defaultPolicies.put(GroupConfigConstant.POLICY_WRITERS, writerPolicyBuilder.build());

        return defaultPolicies;
    }

    /**
     * 构造群组级别的子树
     *
     * @param profile
     * @return
     */
    public static Configtx.ConfigTree buildGroupTree(GenesisConfig.Profile profile) {
        //构建最终的应用ConfigTree
        Configtx.ConfigTree.Builder groupTreeBuilder = Configtx.ConfigTree.newBuilder();

        //填充默认的权限体系
        Map<String, Configtx.ConfigPolicy> defaultPolicies = getDefaultImplicitMetaPolicy();
        groupTreeBuilder.putAllPolicies(defaultPolicies);

        addValue(groupTreeBuilder, new HashingAlgorithmValue(), GroupConfigConstant.POLICY_ADMINS);
        addValue(groupTreeBuilder, new BlockDataHashingStructureValue(), GroupConfigConstant.POLICY_ADMINS);

        IConfigValue consenterAddressesValue = new ConsenterAddressesValue(profile.getConsenter().getAddresses());
        addValue(groupTreeBuilder, consenterAddressesValue, GroupConfigConstant.CONSENTER_ADMINS_POLICY_NAME);

        if(profile.getConsortium() != null){

        }

//        //填充能力集
//        if (application.getCapabilities() != null && !application.getCapabilities().isEmpty()) {
//            IConfigValue configValue = new CapabilitiesValue(application.getCapabilities());
//            addValue(applicationTreeBuilder, configValue.getKey(), configValue.getValue(), GroupConfigConstant.POLICY_ADMINS);
//        }
//
//        //填充子树信息
//        if (application.getOrganizations() != null && application.getOrganizations().length > 0) {
//            for (GenesisConfig.Organization org : application.getOrganizations()) {
//                applicationTreeBuilder.putChilds(org.getName(), buildOrgTree(org));
//            }
//        }
//
//        //填充更改策略人
//        applicationTreeBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);
//
        return groupTreeBuilder.build();
    }

    /**
     * 构造应用级别的子树
     *
     * @param application
     * @return
     */
    public static Configtx.ConfigTree buildApplicationTree(GenesisConfig.Application application) {
        //构建最终的应用ConfigTree
        Configtx.ConfigTree.Builder applicationTreeBuilder = Configtx.ConfigTree.newBuilder();

        //填充默认的权限体系
        Map<String, Configtx.ConfigPolicy> defaultPolicies = getDefaultImplicitMetaPolicy();
        applicationTreeBuilder.putAllPolicies(defaultPolicies);

        //填充能力集
        if (application.getCapabilities() != null && !application.getCapabilities().isEmpty()) {
            IConfigValue configValue = new CapabilitiesValue(application.getCapabilities());
            addValue(applicationTreeBuilder, configValue.getKey(), configValue.getValue(), GroupConfigConstant.POLICY_ADMINS);
        }

        //填充子树信息
        if (application.getOrganizations() != null && application.getOrganizations().length > 0) {
            for (GenesisConfig.Organization org : application.getOrganizations()) {
                applicationTreeBuilder.putChilds(org.getName(), buildOrgTree(org));
            }
        }

        //填充更改策略人
        applicationTreeBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);

        return applicationTreeBuilder.build();
    }

    /**
     * 构造组织级别的子树
     *
     * @param org
     * @return
     */
    public static Configtx.ConfigTree buildOrgTree(GenesisConfig.Organization org) {
        //构建最终的应用ConfigTree
        Configtx.ConfigTree.Builder orgTreeBuilder = Configtx.ConfigTree.newBuilder();

        //填充签名策略
        addSignaturePolicyDefaults(orgTreeBuilder, org.getId(), !DEFAULT_ADMIN_PRINCIPAL.equals(org
                .getAdminPrincipal()));

        //填充MSP信息
        MspConfigPackage.MSPConfig mspConfig = MspConfigHelper.buildMspConfig(org.getMspDir(), org.getId());
        IConfigValue mspValue = new MspValue(mspConfig);
        addValue(orgTreeBuilder, mspValue.getKey(), mspValue.getValue(), GroupConfigConstant.POLICY_ADMINS);

        //填充AnchorNodes信息
        if (org.getAnchorNodes() != null && org.getAnchorNodes().length > 0) {
            Configuration.AnchorNode[] anchorNodes = new Configuration.AnchorNode[org.getAnchorNodes().length];
            for (int i = 0; i < org.getAnchorNodes().length; i++) {
                Configuration.AnchorNode.Builder anchorNodeBuilder = Configuration.AnchorNode.newBuilder();
                anchorNodeBuilder.setHost(org.getAnchorNodes()[i].getHost());
                anchorNodeBuilder.setPort(org.getAnchorNodes()[i].getPort());

                anchorNodes[i] = anchorNodeBuilder.build();
            }

            IConfigValue anchorNodesValue = new AnchorNodesValue(anchorNodes);
            addValue(orgTreeBuilder, anchorNodesValue.getKey(), anchorNodesValue.getValue(), GroupConfigConstant.POLICY_ADMINS);
        }

        //填充更改策略人
        orgTreeBuilder.setModPolicy(GroupConfigConstant.POLICY_ADMINS);

        return orgTreeBuilder.build();
    }

    /**
     * 增加默认的签名策略
     *
     * @param configTreeBuilder
     * @param mspId
     * @param devMode
     */
    private static void addSignaturePolicyDefaults(Configtx.ConfigTree.Builder configTreeBuilder, String mspId, boolean
            devMode) {
        Policies.SignaturePolicyEnvelope policyEnvelope = null;
        if (devMode) {
            policyEnvelope = MockCauthdsl.signedByMspMember(mspId);
        } else {
            policyEnvelope = MockCauthdsl.signedByMspAdmin(mspId);
        }

        IConfigPolicy adminPolicy = new SignaturePolicy(GroupConfigConstant.POLICY_ADMINS, policyEnvelope);
        addPolicy(configTreeBuilder, adminPolicy.getKey(), adminPolicy.getValue(), GroupConfigConstant.POLICY_ADMINS);

        IConfigPolicy readerPolicy = new SignaturePolicy(GroupConfigConstant.POLICY_READERS, MockCauthdsl.signedByMspMember(mspId));
        addPolicy(configTreeBuilder, readerPolicy.getKey(), readerPolicy.getValue(), GroupConfigConstant.POLICY_ADMINS);

        IConfigPolicy writerPolicy = new SignaturePolicy(GroupConfigConstant.POLICY_WRITERS, MockCauthdsl.signedByMspMember(mspId));
        addPolicy(configTreeBuilder, writerPolicy.getKey(), writerPolicy.getValue(), GroupConfigConstant.POLICY_ADMINS);
    }
}
