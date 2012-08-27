package com.hifiremote.jp1;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.ListIterator;

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
    if ( source == convertToUpgradeButton )
    {
      finishEditing();
      int[] rows = table.getSelectedRows();
      ArrayList<LearnedSignal> signals = new ArrayList<LearnedSignal>();
      for ( int i =0; i < rows.length; i++ )
      {
        LearnedSignal s = getRowObject(rows[i]);
        if ( s.getDecodes().isEmpty() )
          System.err.println("Skipping Learned Signal '" + s.getNotes() + "' since it has no decodes.");
        else
          signals.add(s);
      }
      System.err.println( "Found " + signals.size() + " learned signals to conver to device upgrade." );
      if ( !signals.isEmpty() )
        convertToDeviceUpgrade( signals.toArray(new LearnedSignal[signals.size()]) );
    }
    else
      super.actionPerformed( e );
  }
  
  private void convertToDeviceUpgrade( LearnedSignal[] signals )
  {
    System.err.println( "Converting Learned Signals to upgrade..." );
    DeviceUpgrade upgrade = new DeviceUpgrade( signals, remoteConfig );
    remoteConfig.getDeviceUpgrades().add( upgrade );
    remoteConfig.getOwner().getDeviceUpgradePanel().model.fireTableDataChanged();

    /*
    for ( LearnedSignal s: signals )
    {
      LearnedSignalDecode d = s.getDecodes().get( 0 );
      System.err.println( d.protocolName + ": device " + d.device + " with obc " + d.obc + " on key " + s.getKeyCode() + " on device " + remoteConfig.getRemote().getDeviceButton(s.getDeviceButtonIndex()).getName() );
    }
    if (!signal.getDecodes().isEmpty())
    {
      LearnedSignalDecode d = signal.getDecodes().get( 0 );
      System.err.println( d.protocolName + ": " + d.device + " " + d.obc );
    }
    else
    {
      JOptionPane.showMessageDialog( RemoteMaster.getFrame(), "Unable to convert the selected Learned Signal to a Device Upgrade since the signal cannot be decoded by DecodeIR.", "Unable to conver to Device Upgrade", JOptionPane.PLAIN_MESSAGE );
    }
    */
  }
  
  public void valueChanged( ListSelectionEvent e )
  {
    super.valueChanged( e );
    if ( !e.getValueIsAdjusting() )
      convertToUpgradeButton.setEnabled( table.getSelectedRowCount() >= 1 );
  }

  private RemoteConfiguration remoteConfig = null;
  
  private JButton convertToUpgradeButton = null;
}
