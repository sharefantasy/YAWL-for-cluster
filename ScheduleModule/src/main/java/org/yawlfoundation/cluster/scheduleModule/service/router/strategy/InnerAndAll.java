package org.yawlfoundation.cluster.scheduleModule.service.router.strategy;

import org.yawlfoundation.cluster.scheduleModule.entity.Spec;
import org.yawlfoundation.cluster.scheduleModule.entity.Tenant;
import org.yawlfoundation.cluster.scheduleModule.entity.User;
import org.yawlfoundation.cluster.scheduleModule.repo.SpecRepo;
import org.yawlfoundation.cluster.scheduleModule.repo.UserRepo;
import org.yawlfoundation.cluster.scheduleModule.service.router.RoutingRule;
import org.yawlfoundation.cluster.scheduleModule.util.SchedulerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yawlfoundation.yawl.elements.YSpecification;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;
import org.yawlfoundation.yawl.unmarshal.YMarshal;
import org.yawlfoundation.yawl.util.PasswordEncryptor;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Created by fantasy on 2016/6/20.
 */
@Component
public class InnerAndAll extends RoutingRule {
    @Autowired
    private AllEngineInTenant allEngineInTenant;
    @Autowired
    private SpecRepo specRepo;
    @Autowired
    private UserRepo userRepo;

    protected static InnerAndAll instance = new InnerAndAll();

    public static InnerAndAll getInstance() {
        return instance;
    }

    @Override
    public String send(Tenant tenant, Map<String, String> params, String interfce) {
        switch (params.get("action")) {
            case "upload":
                List<YSpecification> specifications;
                try {
                    specifications = YMarshal.unmarshalSpecifications(params.get("specXML"));
                } catch (YSyntaxException e) {
                    return SchedulerUtils.failure("Inappropriate specification");
                }
                YSpecification spec = specifications.get(0);
                Spec s = new Spec(tenant, spec.getID(), spec.getSpecVersion(), spec.getURI());
                specRepo.save(s);
                break;
            case "unload":
                Spec unspec = specRepo.findOne(params.get("specidentifier"));
                if (unspec == null || !unspec.getOwner().equals(tenant)) {
                    return SchedulerUtils.failure("no such specification");
                }
                specRepo.delete(unspec);
                break;
            case "createAccount":
                String password;
                try {
                    password = PasswordEncryptor.encrypt(params.get(("password")));
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
                User user = new User(params.get("userid"), password, tenant);
                userRepo.save(user);
                break;
            case "deleteAccount":
                userRepo.delete(params.get("userid"));
                break;
            case "updateAccount":
                User user1 = userRepo.findByUserName(params.get("userid"));
                user1.setPassword(params.get("password"));
                user1.setPassword(params.get("doco"));
                userRepo.save(user1);
                break;
        }
        return allEngineInTenant.send(tenant, params, interfce);
    }
}
