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
import javax.annotation.Nonnull;
import org.realityforge.gwt.eventsource.client.EventSource;
import org.realityforge.gwt.eventsource.client.event.CloseEvent;
import org.realityforge.gwt.eventsource.client.event.ErrorEvent;
import org.realityforge.gwt.eventsource.client.event.MessageEvent;
import org.realityforge.gwt.eventsource.client.event.OpenEvent;

public final class Example
  implements EntryPoint
{
  private HTML _messages;
  private ScrollPanel _scrollPanel;
  private Button _disconnect;
  private Button _connect;

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

      _connect = new Button( "Connect", new ClickHandler()
      {
        @Override
        public void onClick( final ClickEvent event )
        {
          _connect.setEnabled( false );
          eventSource.connect( url.getValue() );
        }
      } );
      _disconnect = new Button( "Disconnect", new ClickHandler()
      {
        @Override
        public void onClick( ClickEvent event )
        {
          eventSource.close();
          _disconnect.setEnabled( false );
        }
      } );
      _disconnect.setEnabled( false );

      _messages = new HTML();
      _scrollPanel = new ScrollPanel();
      _scrollPanel.setHeight( "250px" );
      _scrollPanel.add( _messages );
      RootPanel.get().add( _scrollPanel );

      {
        final FlowPanel controls = new FlowPanel();
        controls.add( url );
        controls.add( _connect );
        controls.add( _disconnect );
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
        _disconnect.setEnabled( true );
      }
    } );
    eventSource.addCloseHandler( new CloseEvent.Handler()
    {
      @Override
      public void onCloseEvent( @Nonnull final CloseEvent event )
      {
        appendText( "close", "silver" );
        _connect.setEnabled( true );
        _disconnect.setEnabled( false );
      }
    } );
    eventSource.addErrorHandler( new ErrorEvent.Handler()
    {
      @Override
      public void onErrorEvent( @Nonnull final ErrorEvent event )
      {
        appendText( "error", "red" );
        _connect.setEnabled( false );
        _disconnect.setEnabled( false );
      }
    } );
    eventSource.addMessageHandler( new MessageEvent.Handler()
    {
      @Override
      public void onMessageEvent( @Nonnull final MessageEvent event )
      {
        appendText( "message: " + event.getData(), "black" );
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
}
