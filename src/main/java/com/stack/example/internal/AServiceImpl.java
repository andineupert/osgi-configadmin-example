package com.stack.example.internal;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;

import com.stack.example.AService;
import com.stack.example.BService;
import com.stack.example.CService;

@Component(policy = ConfigurationPolicy.REQUIRE)
@Service
public class AServiceImpl implements AService {
    
    String pid = "";
    
    @Reference(target = "(service.pid=*)")
    BService bService;
    
    @Reference(target = "(service.pid=*)")
    CService cService;
    
    public void bindBService(BService bservice) {
        this.bService = bservice;
    }
    
    public void unbindBService(BService bservice) {
        if (this.bService == bservice) {
            this.bService = null;
        }
    }
    
    public void bindCService(CService cservice) {
        this.cService = cservice;
    }
    
    public void unbindCService(CService cservice) {
        if (this.cService == cservice) {
            this.cService = null;
        }
    }
    
    @Activate
    public void activate(BundleContext context, Map<String, Object> config) {
        this.pid = (String) config.get("cpid");
        System.out.println("A > " + pid + " > activate");
    }
    
    @Modified
    public void modified(BundleContext context, Map<String, Object> config) {
        System.out.println("A > " + pid + " > modified");
    }
    
    @Deactivate
    public void deactivate(BundleContext context) {
        System.out.println("A > " + pid + " > deactivate");
    }
    
    @Override
    public void hello() {
        System.out.println("A > " + pid + " > hello > waiting");
        
        Callable<?> runnable = () -> {
            this.bService.hello();
            return true;
        };
        
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(runnable);
        
        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        System.out.println("A > " + pid + " > hello > finished waiting");
        
        this.cService.hello();
        
    }

    @Override
    public String getPid() {
        return this.pid;
    }
}
