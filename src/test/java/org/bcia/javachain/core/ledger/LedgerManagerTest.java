/*
 * Copyright Dingxuan. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.bcia.javachain.core.ledger;

import com.google.protobuf.ByteString;
import com.google.protobuf.util.JsonFormat;
import org.bcia.javachain.common.exception.LedgerException;
import org.bcia.javachain.common.genesis.GenesisBlockFactory;
import org.bcia.javachain.common.ledger.ILedger;
import org.bcia.javachain.common.ledger.blkstorage.fsblkstorage.Config;
import org.bcia.javachain.core.ledger.ledgermgmt.LedgerManager;
import org.bcia.javachain.csp.gm.sm3.SM3;
import org.bcia.javachain.protos.common.Common;
import org.bcia.javachain.protos.common.Configtx;
import org.junit.Test;
import scala.Byte;

import java.io.File;
import java.util.Random;

/**
 * 类描述
 *
 * @author sunzongyu
 * @date 2018/04/17
 * @company Dingxuan
 */
public class LedgerManagerTest {
    INodeLedger l = null;
    private static final byte[] COMPOSITE_KEY_SEP = {0x00};

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Test
    public void delete(){
        System.out.println(deleteDir(new File(Config.getPath())));
    }

    @Test
    public void createLedger() throws Exception {
        GenesisBlockFactory factory = new GenesisBlockFactory(Configtx.ConfigTree.getDefaultInstance());
        System.out.println(deleteDir(new File(Config.getPath())));
        long before = System.currentTimeMillis();
        LedgerManager.initialize(null);
        Common.Block block = factory.getGenesisBlock("MyGroup");
        l = LedgerManager.createLedger(block);
//        List<String> list = LedgerManager.getLedgerIDs();
//        list.forEach((s) -> {
//            System.out.println(s);
//        });
//        ITxSimulator simulator = l.newTxSimulator("MyGroup");
//        simulator.
        l.getTransactionByID("asdal");
    }

    @Test
    public void openLedger() throws Exception{
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("MyGroup");
    }

    @Test
    public void commitBlock() throws LedgerException {
        LedgerManager.initialize(null);
        l = LedgerManager.openLedger("MyGroup");
        long i = 0;
        while(true){
            if(l.getBlockByNumber(i) == null){
                break;
            }
            i++;
        }
        System.out.println("Start Block Number is " + i);
        long startTime = System.currentTimeMillis();
        Common.BlockData data = null;
        ByteString preHash = ByteString.copyFrom(new SM3().hash(l.getBlockByNumber(i - 1).getData().toByteArray()));
        for (int j = 0; j < 2; j++) {
            BlockAndPvtData bap = new BlockAndPvtData();
            data = Common.BlockData.getDefaultInstance();
            bap.setBlock(Common.Block.newBuilder()
                    .setHeader(Common.BlockHeader.newBuilder()
                            .setNumber(i + j)
                            .setDataHash(ByteString.copyFrom(new SM3().hash(data.toByteArray())))
                            .setPreviousHash(preHash)
                            .build())
                    .setData(data)
                    .setMetadata(Common.BlockMetadata.newBuilder()
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .addMetadata(ByteString.EMPTY)
                            .build())
                    .build());
            l.commitWithPvtData(bap);
            preHash = ByteString.copyFrom(new SM3().hash(data.toByteArray()));
        }
        long endTime = System.currentTimeMillis();
        System.out.println("耗时: " + String.valueOf(endTime - startTime) + "ms");
    }

    @Test
    public void showBlocks() throws Exception{
        LedgerManager.initialize(null);
        String groupId = "MyGroup";
        l = LedgerManager.openLedger(groupId);
        for (int i = 0;; i++) {
            Common.Block block = l.getBlockByNumber(i);
            if(block == null){
                break;
            }
            System.out.println(block);
        }
    }

    @Test
    public void newTxSimulator() throws Exception {
        LedgerManager.initialize(null);
        String groupId = "MyGroup";
        l = LedgerManager.openLedger(groupId);
        System.out.println(l.getBlockchainInfo());
    }

    private static void soutBytes(byte[] bytes){
        int i = 0;
        for(byte b : bytes){
            System.out.print(b + " ");
        }
        System.out.println();
    }
}
