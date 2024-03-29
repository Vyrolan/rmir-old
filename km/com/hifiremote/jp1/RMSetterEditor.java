package com.hifiremote.jp1;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;

import com.hifiremote.jp1.RemoteConfiguration.KeySpec;

public class RMSetterEditor< T, C extends JComponent & RMSetter< T > > 
extends DefaultCellEditor implements TableCellEditor, ActionListener
{
  public RMSetterEditor( Class< C > panelClass )
  {
    super( new JTextField() );
    setClickCountToStart( RMConstants.ClickCountToStart );
    this.panelClass = panelClass;
    button = new JButton();
    button.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
    button.setHorizontalAlignment( SwingConstants.LEADING );
    button.setActionCommand( EDIT );
    button.addActionListener( this );
    button.setBorderPainted( false );
  }
  
  public void setRemoteConfiguration( RemoteConfiguration remoteConfig )
  {
    this.remoteConfig = remoteConfig;
  }
  
  @Override
  public Object getCellEditorValue()
  {
    return value;
  }
  
  @SuppressWarnings( "unchecked" )
  @Override
  public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
  {
    if ( value == null )
    {
      return null;
    }
    this.value = ( T )value;
    this.table = table;
    if ( panelClass == ( Class<?> )MacroDefinitionBox.class )
    {
      if ( value instanceof Hex )
      {
        button.setText( Macro.getValueString( ( Hex )value , remoteConfig ) );
      }
      else
      {
        button.setText( Macro.getValueString( ( List< KeySpec > )value ) );
      }
    }
    else
    {
      button.setText( this.value.toString() );
    }
    return button;
  }

  @Override
  public void actionPerformed( ActionEvent e )
  {
    if ( EDIT.equals( e.getActionCommand() ) )
    {
      // The user has clicked the cell, so
      // bring up the dialog.
      
      RMSetterDialog<T> dialog = new RMSetterDialog<T>();
      dialog.setTitle( title );
      dialog.setButtonEnabler( buttonEnabler );
      T result = dialog.showDialog(button, remoteConfig, panelClass, value);
      if ( ( result != null ) && ! result.toString().equals( value.toString() ))
      {
        value = result;
        fireEditingStopped();
        // Fire any list selection listeners by clearing and reselecting cell
        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();
        if ( row >= 0 && col >= 0 )
        {
          table.clearSelection();
          table.setRowSelectionInterval( row, row );
          table.setColumnSelectionInterval( col, col );
        }
      }
      else
      {
        fireEditingCanceled();
      }
    }
  }
  
  public void setTitle( String title )
  {
    this.title = title;
  }

  public void setButtonEnabler( ButtonEnabler buttonEnabler )
  {
    this.buttonEnabler = buttonEnabler;
  }

  private JButton button = null;
  private RemoteConfiguration remoteConfig = null;
  private String title = null;
  
  private T value = null;
  private Class< C > panelClass = null;
  private ButtonEnabler buttonEnabler = null;
  private JTable table = null;
  
  protected static final String EDIT = "edit";

}
