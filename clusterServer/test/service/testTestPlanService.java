package service;

import cluster.hostTester.entity.TestPlanEntity;
import cluster.hostTester.service.TestPlanService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by fantasy on 2016/2/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class testTestPlanService {
    @Autowired
    private TestPlanService testPlanService;

    @Test
    public void testTestPlan() {
        TestPlanEntity tp = new TestPlanEntity();
        tp.setEndTime(new Date(99999999));
//        testPlanService.createTestPlan(tp);
        Assert.assertNotNull(tp.getEndTime());
        Assert.assertNotNull(tp.getStartTime());
        Assert.assertTrue(tp.getEndTime().getTime() - tp.getStartTime().getTime() > 0);
        Assert.assertNotNull(tp.getTestTenant());
    }

}
