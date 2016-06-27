package service;

import cluster.general.service.HostService;
import cluster.hostTester.entity.TestPlanEntity;
import cluster.hostTester.service.TestPlanService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by fantasy on 2016/2/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class misc {
    @Autowired
    private TestPlanService testPlanService;
    @Autowired
    private HostService hostService;

    @Test
    public void test() {
        testPlanService.createTestPlan(hostService.getHostById(1), 1, new Date());
    }
}
