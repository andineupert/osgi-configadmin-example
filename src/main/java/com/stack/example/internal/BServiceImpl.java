package com.stack.example.internal;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;

import com.stack.example.BService;
import com.stack.example.CService;

@Component(policy = ConfigurationPolicy.REQUIRE)
@Service
public class BServiceImpl implements BService {

    String pid = "";
    
    @Reference(target = "(service.pid=*)")
    public CService cService;
    
    public void bindCService(CService cservice) {
        this.cService = cservice;
        System.out.println("B > " + pid + " > bindC > " + cservice.getPid());
    }
    
    public void unbindCService(CService cservice) {
        System.out.println("B > " + pid + " > unbindC > " + cservice.getPid());
        if (this.cService == cservice) {
            this.cService = null;
            System.out.println("B > " + pid + " > unbindC > equal");
        }
    }
    
    @Activate
    public void activate(BundleContext context, Map<String, Object> config) {        
        this.pid = (String) config.get("cpid");        
        System.out.println("B > " + pid + " > activate");
    }
    
    @Modified
    public void modified(BundleContext context, Map<String, Object> config) {
        System.out.println("B > " + pid + " > modified");
    }
    
    @Deactivate
    public void deactivate(BundleContext context) {
        System.out.println("B > " + pid + " > deactivate");
    }
    
    @Override
    public void hello() {
        System.out.println("B > " + pid + " > hello > wait");     
        double y = 0.0;
        try {
            synchronized (this) {
                this.wait(5000);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("B > " + pid + " > hello > finished: " + y);
        this.cService.hello();
    }
    
    @Override
    public String getPid() {
        return this.pid;
    }
}
