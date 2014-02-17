package org.realityforge.gwt.eventsource.example.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.realityforge.gwt.eventsource.client.EventSource;
import org.realityforge.gwt.eventsource.client.event.CloseEvent;
import org.realityforge.gwt.eventsource.client.event.ErrorEvent;
import org.realityforge.gwt.eventsource.client.event.MessageEvent;
import org.realityforge.gwt.eventsource.client.event.OpenEvent;

public final class Example
  implements EntryPoint
{
  private static final Logger LOG = Logger.getLogger( Example.class.getName() );

  private HTML _messages;
  private ScrollPanel _scrollPanel;
  private Button _close;
  private Button _open;

  public void onModuleLoad()
  {
    final EventSource eventSource = EventSource.newEventSourceIfSupported();
    if ( null == eventSource )
    {
      Window.alert( "EventSource not available!" );
    }
    else
    {
      registerListeners( eventSource );
      final TextBox url = new TextBox();
      final String moduleBaseURL = GWT.getModuleBaseURL();
      final String moduleName = GWT.getModuleName();
      final String eventSourceURL =
        moduleBaseURL.substring( 0, moduleBaseURL.length() - moduleName.length() - 1 ) + "api/time";
      url.setValue( eventSourceURL );

      _open = new Button( "Open", new ClickHandler()
      {
        @Override
        public void onClick( final ClickEvent event )
        {
          _open.setEnabled( false );
          eventSource.open( url.getValue() );
          log( eventSource, "Opening EventSource." );
        }
      } );
      _close = new Button( "Close", new ClickHandler()
      {
        @Override
        public void onClick( ClickEvent event )
        {
          eventSource.close();
          _close.setEnabled( false );
          log( eventSource, "Closed EventSource." );
        }
      } );
      _close.setEnabled( false );

      _messages = new HTML();
      _scrollPanel = new ScrollPanel();
      _scrollPanel.setHeight( "250px" );
      _scrollPanel.add( _messages );
      RootPanel.get().add( _scrollPanel );

      {
        final FlowPanel controls = new FlowPanel();
        controls.add( url );
        controls.add( _open );
        controls.add( _close );
        RootPanel.get().add( controls );
      }
    }
  }

  private void registerListeners( final EventSource eventSource )
  {
    eventSource.addOpenHandler( new OpenEvent.Handler()
    {
      @Override
      public void onOpenEvent( @Nonnull final OpenEvent event )
      {
        appendText( "open", "silver" );
        _close.setEnabled( true );
        log( eventSource, "EventSource Open Complete." );
      }
    } );
    eventSource.addCloseHandler( new CloseEvent.Handler()
    {
      @Override
      public void onCloseEvent( @Nonnull final CloseEvent event )
      {
        appendText( "close", "silver" );
        _open.setEnabled( true );
        _close.setEnabled( false );
        log( eventSource, "EventSource Close Complete." );
      }
    } );
    eventSource.addErrorHandler( new ErrorEvent.Handler()
    {
      @Override
      public void onErrorEvent( @Nonnull final ErrorEvent event )
      {
        appendText( "error", "red" );
        _open.setEnabled( false );
        _close.setEnabled( false );
        log( eventSource, "EventSource Error." );
      }
    } );
    eventSource.addMessageHandler( new MessageEvent.Handler()
    {
      @Override
      public void onMessageEvent( @Nonnull final MessageEvent event )
      {
        appendText( event.getMessageType() + ": " + event.getData(), "black" );
        log( eventSource, "EventSource Message: " + event.getData() +
                          " LastEventId=" + event.getLastEventId() +
                          " Type=" + event.getMessageType() + "." );
      }
    } );
  }

  private void appendText( final String text, final String color )
  {
    final DivElement div = Document.get().createDivElement();
    div.setInnerText( text );
    div.setAttribute( "style", "color:" + color );
    _messages.getElement().appendChild( div );
    _scrollPanel.scrollToBottom();
  }

  private void log( final EventSource eventSource, final String message )
  {
    final String suffix;
    if ( EventSource.ReadyState.CLOSED != eventSource.getReadyState() )
    {
      suffix = " URL: " + eventSource.getURL() +
               " WithCredentials: " + eventSource.getWithCredentials();
    }
    else
    {
      suffix = "";
    }

    LOG.warning( message + " ReadyState: " + eventSource.getReadyState().name() + suffix );
  }
}
