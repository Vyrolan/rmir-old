package com.hifiremote.jp1;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;

// TODO: Auto-generated Javadoc
/**
 * The Class LearnedSignalPanel.
 */
public class LearnedSignalPanel extends RMTablePanel< LearnedSignal >
{

  /**
   * Instantiates a new learned signal panel.
   */
  public LearnedSignalPanel()
  {
    super( new LearnedSignalTableModel() );
    
    convertToUpgradeItem = new JMenuItem( "Convert to Device Upgrade" );
    convertToUpgradeItem.addActionListener( this );
    convertToUpgradeItem.setEnabled( false );
    popup.add( convertToUpgradeItem );
    
    convertToUpgradeButton = new JButton( "Convert to Device Upgrade" );
    convertToUpgradeButton.addActionListener( this );
    convertToUpgradeButton.setToolTipText( "Convert the selected item to a Device Upgrade." );
    convertToUpgradeButton.setEnabled( false );
    buttonPanel.add( convertToUpgradeButton );
  }

  /**
   * Sets the.
   * 
   * @param remoteConfig
   *          the remote config
   */
  @Override
  public void set( RemoteConfiguration remoteConfig )
  {
    this.remoteConfig = remoteConfig;
    ( ( LearnedSignalTableModel )model ).set( remoteConfig );
    table.initColumns( model );
    newButton.setEnabled( remoteConfig != null && remoteConfig.getRemote().getLearnedAddress() != null );
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.hifiremote.jp1.RMTablePanel#createRowObject(java.lang.Object)
   */
  @Override
  public LearnedSignal createRowObject( LearnedSignal learnedSignal )
  {
    LearnedSignal newSignal = null;
    if ( learnedSignal != null )
    {
      newSignal = new LearnedSignal( learnedSignal );
    }
    return LearnedSignalDialog.showDialog( SwingUtilities.getRoot( this ), newSignal, remoteConfig );
  }
  
  public void actionPerformed( ActionEvent e )
  {
    Object source = e.getSource();
    if ( source == convertToUpgradeButton || source == convertToUpgradeItem )
    {
      finishEditing();
      int row = 0;
      if ( source.getClass() == JButton.class )
        row = table.getSelectedRow();
      else
        row = popupRow;
      if ( row != -1 )
        convertToDeviceUpgrade( getRowObject( row ) );
    }
    else
      super.actionPerformed( e );
  }
  
  private void convertToDeviceUpgrade( LearnedSignal signal )
  {
    if (!signal.getDecodes().isEmpty())
    {
      LearnedSignalDecode d = signal.getDecodes().get( 0 );
      System.err.println( d.protocolName + ": " + d.device + " " + d.obc );
    }
    else
    {
      JOptionPane.showMessageDialog( RemoteMaster.getFrame(), "Unable to convert the selected Learned Signal to a Device Upgrade since the signal cannot be decoded by DecodeIR.", "Unable to conver to Device Upgrade", JOptionPane.PLAIN_MESSAGE );
    }
  }
  
  public void valueChanged( ListSelectionEvent e )
  {
    super.valueChanged( e );
    if ( !e.getValueIsAdjusting() )
    {
      RemoteMaster rm = ( RemoteMaster )SwingUtilities.getAncestorOfClass( RemoteMaster.class, this );
      rm.highlightAction.setEnabled( table.getSelectedRowCount() > 0 );
      if ( table.getSelectedRowCount() >= 1 )
      {
        int row = table.getSelectedRow();
        boolean selected = row != -1;
        convertToUpgradeButton.setEnabled( selected );
        convertToUpgradeItem.setEnabled( selected );
      }
      else
      {
        convertToUpgradeButton.setEnabled( false );
        convertToUpgradeItem.setEnabled( false );
      }
    }
  }

  private RemoteConfiguration remoteConfig = null;
  
  private JButton convertToUpgradeButton = null;
  private JMenuItem convertToUpgradeItem = null;
}
