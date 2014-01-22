package org.realityforge.gwt.eventsource.example.server;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.media.sse.SseFeature;

@ApplicationPath( "/api" )
public class JaxRsActivator
  extends Application
{
  @Override
  public Set<Class<?>> getClasses()
  {
    final Set<Class<?>> classes = new HashSet<>();
    classes.add( TimeResource.class );
    return classes;
  }

  @Override
  public Set<Object> getSingletons()
  {
    final Set<Object> singletons = new HashSet<>();
    singletons.add( new SseFeature() );
    return singletons;
  }
}
