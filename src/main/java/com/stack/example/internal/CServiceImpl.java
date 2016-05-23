package com.stack.example.internal;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;

import com.stack.example.CService;

@Component(policy = ConfigurationPolicy.REQUIRE)
@Service
public class CServiceImpl implements CService {

    String pid = "";
    
    @Activate
    public void activate(BundleContext context, Map<String, Object> config) {
        this.pid = (String) config.get("cpid");
        System.out.println("C > " + this.pid + " > activate");        
    }
    
    @Modified
    public void modified(BundleContext context, Map<String, Object> config) {
        System.out.println("C > " + this.pid + " > modified");
    }
    
    @Deactivate
    public void deactivate(BundleContext context) {
        System.out.println("C > " + this.pid + " > deactivate");
    }
    
    @Override
    public void hello() {
        System.out.println("C > " + this.pid + " > hello");
    }
    
    @Override
    public String getPid() {
        return this.pid;
    }
}
