package org.bcia.javachain.core.ssc.vssc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.BaseJunit4Test;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.exception.SysSmartContractException;
import org.bcia.javachain.common.util.CommConstant;
import org.bcia.javachain.common.util.proto.EnvelopeHelper;
import org.bcia.javachain.common.util.proto.ProposalResponseUtils;
import org.bcia.javachain.common.util.proto.ProposalUtils;
import org.bcia.javachain.common.util.proto.TxUtils;
import org.bcia.javachain.core.smartcontract.shim.ISmartContract;
import org.bcia.javachain.core.smartcontract.shim.ISmartContractStub;
import org.bcia.javachain.core.smartcontract.shim.impl.MockStub;
import org.bcia.javachain.core.ssc.essc.MockMSP;
import org.bcia.javachain.core.ssc.essc.MockMspManager;
import org.bcia.javachain.core.ssc.essc.MockSigningIdentity;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.mgmt.GlobalMspManagement;
import org.bcia.javachain.msp.mgmt.MspManager;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Policies;
import org.bcia.javachain.protos.node.ProposalPackage;
import org.bcia.javachain.protos.node.ProposalResponsePackage;
import org.bcia.javachain.protos.node.Smartcontract;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * VSSC单元测试类
 *
 * @author sunianle
 * @date 4/16/18
 * @company Dingxuan
 */
public class VSSCTest extends BaseJunit4Test {
    @Autowired
    private VSSC vssc;
    private MockStub mockStub;

    @Before
    public void beforeTest(){
        mockStub = new MockStub(CommConstant.VSSC, vssc);
    }

    @Test
    public void init() {
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void invoke() {
        ISmartContract.SmartContractResponse smartContractResponse =mockStub.mockInit("1",new LinkedList<ByteString>());
        assertThat(smartContractResponse.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));

        Common.Envelope tx= null;
        try {
            tx = createTx(false);
        } catch (JavaChainException e) {
            e.printStackTrace();
            System.out.println("Create Trade failed,testing exits with error.");
            return;
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            System.out.println("Create Trade failed,testing exits with error.");
            return;
        }
        byte[] envBytes=TxUtils.getBytesEnvelope(tx);
        String mspid="snl";
        byte[] policyBytes=getSignedByMSPMemberPolicy(mspid);

        List<ByteString> args= new LinkedList<ByteString>();
        args.add(ByteString.copyFromUtf8("dv"));
        args.add(ByteString.copyFrom(envBytes));
        args.add(ByteString.copyFrom(policyBytes));

        ISmartContract.SmartContractResponse smartContractResponse2 =mockStub.mockInvoke("1",args);
        assertThat(smartContractResponse2.getStatus(), is(ISmartContract.SmartContractResponse.Status.SUCCESS));
    }

    @Test
    public void testInvalidFunction(){

    }

    @Test
    public void testRWSetTooBig(){

    }

    @Test
    public void testValidateDeployFail(){

    }

    @Test
    public void testAlreadyDeployed(){

    }

    @Test
    public void testValidateDeployNoLedger(){

    }

    @Test
    public void testValidateDeployOK(){

    }

    @Test
    public void testValidateDeployWithPolicies(){

    }

    @Test
    public void testInvalidUpgrade(){

    }

    @Test
    public void testValidateUpgradeOK(){

    }

    @Test
    public void testInvalidateUpgradeBadVersion(){

    }

    @Test
    public void testValidateUpgradeWithPoliciesOK(){

    }

    @Test
    public void testValidateUpgradeWithNewFailAllIP(){

    }

    private void validateUpgradeWithNewFailAllIP(boolean v11capability, boolean expecterr){

    }

    @Test
    public void testValidateUpgradeWithPoliciesFail(){

    }

    @Test
    public void testValidateDeployRWSetAndCollection(){

    }

    private byte[] getSignedByMSPMemberPolicy(String mspid) {
        String policy="(Alice and Bob)";
        return policy.getBytes();
    }

    /**
     * 创建模拟的交易
     * @param bEndorsedByDuplicatedIdentity
     * @return
     * @throws JavaChainException
     * @throws InvalidProtocolBufferException
     */
    private Common.Envelope createTx(boolean bEndorsedByDuplicatedIdentity) throws JavaChainException, InvalidProtocolBufferException {
        //创建SmartContractID
        Smartcontract.SmartContractID smartContractID=Smartcontract.SmartContractID.newBuilder().
                                                       setName("foo").setVersion("v1").build();
        //创建SmartContractSpec
        Smartcontract.SmartContractSpec spec=Smartcontract.SmartContractSpec.newBuilder().
                                                   setSmartContractId(smartContractID).build();
        //创建SmartContractInvocationSpec
        Smartcontract.SmartContractInvocationSpec invokeSpec=Smartcontract.SmartContractInvocationSpec.newBuilder().
                                                   setSmartContractSpec(spec).build();

        ISigningIdentity identity = GlobalMspManagement.getLocalMsp().getDefaultSigningIdentity();
        byte[] creator = identity.serialize();

        String groupID="testGroup";
        //创建Proposal
        ProposalPackage.Proposal proposal=ProposalUtils.createProposalFromInvocationSpec(
                                 Common.HeaderType.ENDORSER_TRANSACTION, groupID,
                                 invokeSpec,creator);
        //创建response
        ProposalResponsePackage.Response response=ProposalResponsePackage.Response.newBuilder().setStatus(200).build();
        //先使用一个模拟的签名实体，后面和msp对接
        IMsp localMSP = GlobalMspManagement.getLocalMsp();
        ISigningIdentity signingEndorser=localMSP.getDefaultSigningIdentity();
        String results="res";
        //生成一个ProposalResponse
        ProposalResponsePackage.ProposalResponse presp=ProposalResponseUtils.buildProposalResponse(
                proposal.getHeader().toByteArray(),
                proposal.getPayload().toByteArray(),
                response,
                results.getBytes(),
                null,
                smartContractID,
                null,
                signingEndorser
        );
        //构建发送给Conserter的Envelope
        Common.Envelope env=null;
        //两个重复实体签名
        if(bEndorsedByDuplicatedIdentity){
            env=EnvelopeHelper.createSignedTxEnvelope(proposal,signingEndorser,presp,presp);
        }
        //单个实体签名
        else{
            env=EnvelopeHelper.createSignedTxEnvelope(proposal,signingEndorser,presp);
        }
        return env;
    }

    private byte[] processSignedCDS(Smartcontract.SmartContractDeploymentSpec cds,
                                    Policies.SignaturePolicyEnvelope policy)throws SysSmartContractException{
        return null;
    }

    private Smartcontract.SmartContractDeploymentSpec constructDeploymentSpec(String name,String path,String version,
                                                                              List<ByteString> initArgs,
                                                                              boolean bCreateFS)throws SysSmartContractException{
        return null;
    }

    private byte[] createSCDataRWset(String nameK,String nameV,String version,byte[] policy)throws SysSmartContractException{
        return null;
    }

    private Common.Envelope createLSSCTx(String ccname,String ccver,String f,byte[] res)throws SysSmartContractException{
        return null;
    }

    private Common.Envelope createLSSCTxPutCds(String scName,String scVersion,String f,byte[]res,byte[] cdsbytes,boolean putcds)throws SysSmartContractException{
        return null;
    }

    private byte[] getSignedByOneMemberTwicePolicy(String mspID){
        return null;
    }

    private byte[] getSignedByMSPAdminPolicy(String mspID){
        return null;
    }
}