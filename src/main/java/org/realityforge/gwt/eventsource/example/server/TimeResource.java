package org.realityforge.gwt.eventsource.example.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseBroadcaster;

@Path("/time")
@Singleton
public class TimeResource
{
  private final SseBroadcaster _broadcaster = new SseBroadcaster();
  private final ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();

  @PostConstruct
  public void postConstruct()
  {
    sch.scheduleWithFixedDelay( new TimeGenerator( _broadcaster ), 0, 5, TimeUnit.SECONDS );
  }

  @GET
  @Produces("text/event-stream")
  @Path("/")
  public EventOutput getMessages()
  {
    final EventOutput eventOutput = new EventOutput();
    _broadcaster.add( eventOutput );
    return eventOutput;
  }
}