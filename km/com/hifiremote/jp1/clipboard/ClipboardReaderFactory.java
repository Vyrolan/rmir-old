/**
 * 
 */
package com.hifiremote.jp1.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Greg
 */
public class ClipboardReaderFactory
{
  public static ClipboardReader getClipboardReader( Transferable transferable, boolean fromIE )
      throws UnsupportedFlavorException, IOException
  {
    DataFlavor[] flavors = transferable.getTransferDataFlavors();
    ClipboardReader reader = null;
    for ( DataFlavor tentative : flavors )
    {
      if ( tentative.getPrimaryType().equals( "text" ) )
      {
        Class< ? > clazz = tentative.getRepresentationClass();
        if ( clazz == Reader.class || clazz == InputStream.class )
        {
          String subType = tentative.getSubType();
          if ( fromIE && subType.equals( "html" ) )
          {
            reader = new HTMLClipboardReader( tentative.getReaderForText( transferable ) );
            break;
          }
          else if ( subType.equals( "plain" ) )
          {
            reader = new PlainTextClipboardReader( tentative.getReaderForText( transferable ) );
          }
        }
      }
    }
    return reader;
  }
}
