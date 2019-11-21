package railway_system.server;

import railway_system.filters.AuthenticationFilter;
import railway_system.filters.CorsFilter;
import railway_system.filters.Logged;
import railway_system.filters.LoggingFilter;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;



@ApplicationPath("/services")

public class RailwayApplication extends Application {
    private Set<Object> singletons = new HashSet<Object>();
    private Set<Class<?>> empty = new HashSet<Class<?>>();
    
    public RailwayApplication() {
        singletons.add(new RoutesService());
        singletons.add(new StationService());
        singletons.add(new AuthorizationService());
        singletons.add(new PassengerService());
        singletons.add(new AuthenticationFilter());
        singletons.add(new AgentService());
        singletons.add(new ManagerService());
        singletons.add(new CorsFilter());
        singletons.add(new LoggingFilter());
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
