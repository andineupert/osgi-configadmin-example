package com.stack.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.stack.example.internal.AServiceImpl;
import com.stack.example.internal.BServiceImpl;
import com.stack.example.internal.CServiceImpl;

@Component(enabled = true, immediate = true)
public class User {
    
    @Reference
    ConfigurationAdmin configAdmin;
    
    @Reference(cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,            
            policy = ReferencePolicy.DYNAMIC, 
            referenceInterface = com.stack.example.AService.class,
            bind = "bind",
            unbind = "unbind")
    volatile List<AService> aServices;
    
    private List<Configuration> config = new ArrayList<>();
    
    @Activate
    public void activate(BundleContext context, Map<String, Object> config) {
        
        Configuration serviceConfig;
        Dictionary<String, Object> props;
        try {
            // Instantiate aService
            serviceConfig = configAdmin.createFactoryConfiguration(AServiceImpl.class.getName(), null);            
            props = new Hashtable<>();
            props.put("cpid", "1");
            props.put("bService.target", "(cpid=1)");
            props.put("cService.target", "(cpid=1)");
            serviceConfig.update(props);            
            
            // Instantiate bService
            serviceConfig = configAdmin.createFactoryConfiguration(BServiceImpl.class.getName(), null);
            props = new Hashtable<>();
            props.put("cpid", "1");            
            props.put("cService.target", "(cpid=1)");
            serviceConfig.update(props);
            
            // Instantiate cService
            serviceConfig = configAdmin.createFactoryConfiguration(CServiceImpl.class.getName(), null);
            props = new Hashtable<>();
            props.put("cpid", "1");
            serviceConfig.update(props);
            
            // Instantiate aService
            serviceConfig = configAdmin.createFactoryConfiguration(AServiceImpl.class.getName(), null);
            props = new Hashtable<>();
            props.put("cpid", "2");
            props.put("bService.target", "(cpid=2)");
            props.put("cService.target", "(cpid=2)");
            serviceConfig.update(props);
            
            // Instantiate bService
            serviceConfig = configAdmin.createFactoryConfiguration(BServiceImpl.class.getName(), null);
            props = new Hashtable<>();
            props.put("cpid", "2");            
            props.put("cService.target", "(cpid=2)");
            serviceConfig.update(props);
            
            // Instantiate cService
            serviceConfig = configAdmin.createFactoryConfiguration(CServiceImpl.class.getName(), null);
            props = new Hashtable<>();
            props.put("cpid", "2");
            serviceConfig.update(props);
            
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Configuration[] getConfigSet(String pid) {
        String filter = "(cpid=" + pid + ")";
        Configuration[] configs = null;
        try {
            configs = this.configAdmin.listConfigurations(filter);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return configs;
    }
    
    public void bind(AService aService) {
        if (this.aServices == null) {
            this.aServices = new ArrayList<>();
        }
        this.aServices.add(aService);
        System.out.println("> Consumer > bind > " + aService.getPid());
        
        Supplier<AService> sup = () -> {
            aService.hello();
            return aService;
        };
        CompletableFuture<?> future = CompletableFuture.supplyAsync(sup);        
        future.whenComplete((Object o, Throwable t) -> {            
            
            AService service = (AService)o;
            
            Configuration[] configs = getConfigSet(service.getPid());
            for (int i = 0; i < configs.length; i++) {
                try {
                    System.out.println("Delete service: " + configs[i].getProperties().get("cpid") + " - " + configs[i].getPid());
                    configs[i].delete();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void unbind(AService aService) {
        if (this.aServices == null) {
            this.aServices = new ArrayList<>();
        }
        this.aServices.remove(aService);
        System.out.println("> Consumer > unbind > " + aService.getPid());
    }
}
