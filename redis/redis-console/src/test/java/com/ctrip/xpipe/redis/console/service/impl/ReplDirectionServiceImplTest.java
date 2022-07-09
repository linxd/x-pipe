package com.ctrip.xpipe.redis.console.service.impl;

import com.ctrip.xpipe.redis.console.model.ClusterTbl;
import com.ctrip.xpipe.redis.console.model.ReplDirectionInfoModel;
import com.ctrip.xpipe.redis.console.model.ReplDirectionTbl;
import com.ctrip.xpipe.utils.ObjectUtils;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ReplDirectionServiceImplTest extends AbstractServiceImplTest{

    @Autowired
    ReplDirectionServiceImpl replDirectionService;

    @Autowired
    ClusterServiceImpl clusterService;

    @Test
    public void testFindReplDirectionTblById(){
        ReplDirectionTbl replDirectionTbl = replDirectionService.findReplDirectionTblById(1);
        Assert.assertNotNull(replDirectionTbl);
        Assert.assertEquals(1, replDirectionTbl.getId());
        Assert.assertEquals(7,replDirectionTbl.getClusterId());
        Assert.assertEquals(1,replDirectionTbl.getSrcDcId());
        Assert.assertEquals(1,replDirectionTbl.getFromDcId());
        Assert.assertEquals(2,replDirectionTbl.getToDcId());

        replDirectionTbl = replDirectionService.findReplDirectionTblById(30);
        Assert.assertNull(replDirectionTbl);
    }

    @Test
    public void testFindAllReplDirectionByCluster() {
        List<ReplDirectionTbl> replDirectionTbls = replDirectionService.findAllReplDirectionTblsByCluster(18);
        Assert.assertEquals(0, replDirectionTbls.size());

        replDirectionTbls = replDirectionService.findAllReplDirectionTblsByCluster(7);
        Assert.assertEquals(2, replDirectionTbls.size());
    }

    @Test
    public void testReplDirectionInfoModelByClusterAndSrcDcAndToDc() {
        ReplDirectionInfoModel expect = new ReplDirectionInfoModel().setId(2L).setClusterName("hetero-cluster")
                                                .setSrcDcName("jq").setFromDcName("jq").setToDcName("fra");
        ReplDirectionInfoModel infoModel
                = replDirectionService.findReplDirectionInfoModelByClusterAndSrcToDc("hetero-cluster", "jq", "fra");
        Assert.assertEquals(true, ObjectUtils.equals(expect, infoModel));

        infoModel = replDirectionService.findReplDirectionInfoModelByClusterAndSrcToDc("hetero-cluster", "oy", "fra");
        Assert.assertNull(infoModel);

        try {
            infoModel = replDirectionService.findReplDirectionInfoModelByClusterAndSrcToDc("none", "jq", "fra");
        } catch (Exception e) {
            Assert.assertEquals( String.format("cluster %s does not exist", "none"), e.getMessage());
        }

        try {
            infoModel = replDirectionService.findReplDirectionInfoModelByClusterAndSrcToDc("hetero-cluster", "none", "fra");
        } catch (Exception e) {
            Assert.assertEquals(String.format("src dc %s or to dc %s does not exist", "none", "fra"), e.getMessage());
        }
    }

    @Test
    public void testReplDirectionInfoModelsByClusterAndToDc(){

        List<ReplDirectionInfoModel> infoModels
                = replDirectionService.findReplDirectionInfoModelsByClusterAndToDc("hetero-cluster",  "fra");
        Assert.assertEquals(1, infoModels.size());

        infoModels = replDirectionService.findReplDirectionInfoModelsByClusterAndToDc("hetero-cluster2", "fra");
        Assert.assertEquals(2, infoModels.size());

        try {
            infoModels = replDirectionService.findReplDirectionInfoModelsByClusterAndToDc("none", "fra");
        } catch (Exception e) {
            Assert.assertEquals( String.format("cluster %s does not exist", "none"), e.getMessage());
        }

        try {
            infoModels = replDirectionService.findReplDirectionInfoModelsByClusterAndToDc("hetero-cluster", "none");
        } catch (Exception e) {
            Assert.assertEquals(String.format("dc %s does not exist", "none"), e.getMessage());
        }
    }

    @Test
    public void testFindAllReplDirectionInfoModelsByCluster() {
        String clusterName = "hetero-cluster";
        ReplDirectionInfoModel replDirectionInfoModel1 = new ReplDirectionInfoModel().setId(1)
                .setClusterName(clusterName).setSrcDcName("jq").setFromDcName("jq").setToDcName("oy");
        ReplDirectionInfoModel replDirectionInfoModel2 = new ReplDirectionInfoModel().setId(2)
                .setClusterName(clusterName).setSrcDcName("jq").setFromDcName("jq").setToDcName("fra");

        List<ReplDirectionInfoModel> replDirectionInfoModels =
                replDirectionService.findAllReplDirectionInfoModelsByCluster(clusterName);
        Assert.assertEquals(Lists.newArrayList(replDirectionInfoModel1, replDirectionInfoModel2), replDirectionInfoModels);
    }

    @Test
    public void testUpdateReplDirection() {
        String heteroClusterName = "hetero-cluster";
        String wrongClusterName = "hetero-cluster2";
        long heteroClusterId = 7;
        ClusterTbl clusterTbl = clusterService.find(heteroClusterName);

        ReplDirectionTbl replDirectionTbl = replDirectionService.findReplDirectionTblById(1L);
        Assert.assertEquals(2, replDirectionTbl.getToDcId());
        replDirectionTbl = replDirectionService.findReplDirectionTblById(2L);
        Assert.assertEquals(3, replDirectionTbl.getToDcId());

        ReplDirectionInfoModel replDirectionInfoModel1 = new ReplDirectionInfoModel().setClusterName(heteroClusterName)
                .setSrcDcName("jq").setFromDcName("jq").setToDcName("oy").setId(2L);
        ReplDirectionInfoModel replDirectionInfoModel2 = new ReplDirectionInfoModel().setClusterName(heteroClusterName)
                .setSrcDcName("jq").setFromDcName("jq").setToDcName("fra").setId(1L);

        replDirectionService.updateClusterReplDirections(clusterTbl, Lists.newArrayList(replDirectionInfoModel1, replDirectionInfoModel2));

        replDirectionTbl = replDirectionService.findReplDirectionTblById(1L);
        Assert.assertEquals(3, replDirectionTbl.getToDcId());
        replDirectionTbl = replDirectionService.findReplDirectionTblById(2L);
        Assert.assertEquals(2, replDirectionTbl.getToDcId());

        try {
            replDirectionService.updateClusterReplDirections(null, Lists.newArrayList(replDirectionInfoModel1, replDirectionInfoModel2));
        } catch (Exception e) {
            Assert.assertEquals("[updateClusterReplDirections] cluster can not be null!", e.getMessage());
        }

        replDirectionInfoModel1.setClusterName(wrongClusterName);
        try {
            replDirectionService.updateClusterReplDirections(clusterTbl, Lists.newArrayList(replDirectionInfoModel1, replDirectionInfoModel2));
        } catch (Exception e) {
            Assert.assertEquals("[updateClusterReplDirections] repl direction should belong to cluster:7, but belong to cluster:8", e.getMessage());
        }

        replDirectionInfoModel1.setClusterName(heteroClusterName).setSrcDcName("oy");
        try {
            replDirectionService.updateClusterReplDirections(clusterTbl, Lists.newArrayList(replDirectionInfoModel1, replDirectionInfoModel2));
        } catch (Exception e) {
            Assert.assertEquals("[updateClusterReplDirections] repl direction should copy from src dc:1, but from 2", e.getMessage());
        }

    }

    @Override
    protected String prepareDatas() throws IOException {
        return  prepareDatasFromFile("src/test/resources/hetero-cluster-test.sql");
    }
}