package com.hifiremote.jp1;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceUpgradeEditor.
 */
public class DeviceUpgradeEditor extends JFrame implements ActionListener
{
  /**
   * Instantiates a new device upgrade editor.
   * 
   * @param owner
   *          the owner
   * @param deviceUpgrade
   *          the device upgrade
   * @param remotes
   *          the remotes
   */
  public DeviceUpgradeEditor( RemoteMaster owner, DeviceUpgrade deviceUpgrade, Collection< Remote > remotes,
      Integer row, RMPanel panel )
  {
    super( "Device Upgrade Editor" );
    this.owner = owner;
    this.row = row;
    this.panel = panel;
    focusWindowAdapter = new WindowAdapter()
    {
      @Override
      public void windowGainedFocus( WindowEvent e )
      {
        toFront();
      }
    };

    // Check consistency of device upgrade
    Protocol p = deviceUpgrade.getProtocol();
    Remote remote = deviceUpgrade.getRemote();
    String proc = remote.getProcessor().getEquivalentName();
    Hex pCode = p.getCode( remote );
    if ( pCode != null
        && pCode.length() > 4
        && ( p.getFixedDataLength() != Protocol.getFixedDataLengthFromCode( proc, pCode ) || p.getDefaultCmd().length() != Protocol
            .getCmdLengthFromCode( proc, pCode ) ) )
    {
      String title = "Device Upgrade Editor";
      String message = "The code of the protocol for this device upgrade is not consistent\n"
          + "with the protocol default parameters, so the device upgrade cannot\n"
          + "be edited.  You need to edit the protocol code to correct this, before\n"
          + "you can edit the device upgrade";
      JOptionPane.showMessageDialog( this, message, title, JOptionPane.ERROR_MESSAGE );
      return;
    }

    owner.addWindowFocusListener( focusWindowAdapter );

    addWindowStateListener( new WindowAdapter()
    {
      @Override
      public void windowStateChanged( WindowEvent e )
      {
        if ( e.getNewState() == JFrame.ICONIFIED )
        {
          if ( DeviceUpgradeEditor.this.owner.getState() != Frame.ICONIFIED )
          {
            setExtendedState( NORMAL );
            toFront();
          }
        }
      }
    } );

    setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    addWindowListener( new WindowAdapter()
    {
      @Override
      public void windowClosing( WindowEvent event )
      {
        cancelButton.doClick();
        DeviceUpgradeEditor.this.owner.removeWindowFocusListener( focusWindowAdapter );
      }
    } );
    editorPanel = new DeviceEditorPanel( this, deviceUpgrade, remotes );
    add( editorPanel, BorderLayout.CENTER );

    Box buttonBox = Box.createHorizontalBox();
    buttonBox.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    add( buttonBox, BorderLayout.SOUTH );

    buttonBox.add( loadButton );
    buttonBox.add( Box.createHorizontalStrut( 5 ) );
    buttonBox.add( importButton );
    buttonBox.add( Box.createHorizontalStrut( 5 ) );
    buttonBox.add( saveAsButton );
    buttonBox.add( Box.createHorizontalGlue() );
    buttonBox.add( okButton );
    buttonBox.add( Box.createHorizontalStrut( 5 ) );
    buttonBox.add( cancelButton );

    loadButton.setToolTipText( "Open a (RM or KM) device upgrade file." );
    importButton.setToolTipText( "Import a Raw (or IR-formatted) device upgrade from the clipboard." );
    saveAsButton.setToolTipText( "Save the device upgrade to a file." );
    okButton.setToolTipText( "Commit any changes and dismiss the dialog." );
    cancelButton.setToolTipText( "Dismiss the dialog without commiting changes." );

    loadButton.addActionListener( this );
    importButton.addActionListener( this );
    saveAsButton.addActionListener( this );
    okButton.addActionListener( this );
    cancelButton.addActionListener( this );
    pack();
    editorPanel.setAltPIDReason();
    setLocationRelativeTo( owner );
//    DeviceButton devBtn = deviceUpgrade.getButtonRestriction();
//    HashMap< Button, String > softButtonNames = null;
//    if ( devBtn != null && ( softButtonNames = devBtn.getSoftButtonNames() ) != null )
//    {
//      for ( Button btn : softButtonNames.keySet() )
//      {
//        btn.setName( softButtonNames.get( btn ) );
//      }
//    }
    setVisible( true );
  }

  /**
   * Gets the device upgrade.
   * 
   * @return the device upgrade
   */
  public DeviceUpgrade getDeviceUpgrade()
  {
    if ( cancelled )
    {
      return null;
    }

    return editorPanel.getDeviceUpgrade();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed( ActionEvent e )
  {
    try
    {
      Object source = e.getSource();
      if ( source == okButton || source == cancelButton )
      {
//        SetupPanel setupPanel = editorPanel.getSetupPanel();
        DeviceUpgrade upgrade = editorPanel.getDeviceUpgrade();
        RemoteConfiguration remoteConfig = upgrade.getRemoteConfig();
        
        if ( source == cancelButton )
        {
          cancelled = true;
          Protocol p = upgrade.convertedProtocol;
          if ( p != null )
          {
            ProtocolManager.getProtocolManager().remove( p );
          }
          p = upgrade.originalProtocol;
          if ( p instanceof ManualProtocol )
          {
            ProtocolManager.getProtocolManager().add( p );
          }
        }
        else if ( panel instanceof DeviceUpgradePanel )
        {
          DeviceUpgradePanel dup = ( DeviceUpgradePanel )panel;
//          RemoteConfiguration remoteConfig = dup.getRemoteConfig();

          for ( DeviceUpgrade du : remoteConfig.getDeviceUpgrades() )
          {
            if ( du == dup.getOldUpgrade() )
            {
              continue;
            }
            DeviceType type = upgrade.getDeviceType();
            int code = upgrade.getSetupCode();
            if ( du.getDeviceType() == type && du.getSetupCode() == code )
            {
              String title = "Setup Code Conflict";
              String message = "Setup code " + new SetupCode( code ) + " has already been used in another upgrade for device type "
                    + type.getName() + ".\nPlease change it to a unique value.";
              JOptionPane.showMessageDialog( this, message, title, JOptionPane.ERROR_MESSAGE );
              return;
            }
          }
        }
        
        if ( !cancelled && remoteConfig.getRemote().usesEZRC() )
        {
          upgrade.classifyButtons();
//          remoteConfig.assignUpgrades();
        }
        
        setVisible( false );
        dispose();
        editorPanel.releasePanels();
        if ( panel instanceof GeneralPanel )
        {
          ( ( GeneralPanel )panel ).endEdit( this, row );
        }
        else if ( panel instanceof KeyMovePanel )
        {
          ( ( KeyMovePanel )panel ).endEditUpgrade( );
        }
        else if ( panel instanceof DeviceUpgradePanel )
        {
          DeviceUpgradePanel dup = ( DeviceUpgradePanel )panel;
          if ( !cancelled )
          {
//            RemoteConfiguration remoteConfig = dup.getRemoteConfig();
            Protocol pOrig = upgrade.originalProtocol;
            ManualProtocol pConv = upgrade.convertedProtocol;
            for ( DeviceUpgrade du : remoteConfig.getDeviceUpgrades() )
            {
              du.changeProtocol( pOrig, pConv );
            }
            
            if ( pOrig != null && !( pOrig instanceof ManualProtocol ) )
            {
              pOrig.customCode.remove( remoteConfig.getRemote().getProcessor().getEquivalentName() );
            }
          }
          dup.endEdit( this, row );
        }
      }
      else if ( source == loadButton )
      {
        load();
      }
      else if ( source == importButton )
      {
        importFromClipboard();
      }
      else if ( source == saveAsButton )
      {
        save();
      }
    }
    catch ( Exception ex )
    {
      ex.printStackTrace( System.err );
    }
  }

  /**
   * Load.
   * 
   * @throws Exception
   *           the exception
   */
  public void load() throws Exception
  {
    System.err.println( "DeviceUpgradeEditor.load()" );
    File file = null;
    RMFileChooser chooser = new RMFileChooser();
    try
    {
      chooser.setAcceptAllFileFilterUsed( false );
    }
    catch ( Exception e )
    {
      e.printStackTrace( System.err );
    }
    String[] allEndings =
    {
        ".rmdu", ".txt"
    };
    String[] rmEndings =
    {
      ".rmdu"
    };
    String[] kmEndings =
    {
      ".txt"
    };
    EndingFileFilter allFilter = new EndingFileFilter( "All device upgrade files (*.rmdu,*.txt)", allEndings );
    chooser.addChoosableFileFilter( allFilter );
    chooser.addChoosableFileFilter( new EndingFileFilter( "RemoteMaster device upgrade files (*.rmdu)", rmEndings ) );
    chooser.addChoosableFileFilter( new EndingFileFilter( "KeyMapMaster device upgrade files (*.txt)", kmEndings ) );
    chooser.setFileFilter( allFilter );

    RemoteMaster rm = ( RemoteMaster )SwingUtilities.getAncestorOfClass( RemoteMaster.class, this );
    String dir = JP1Frame.getProperties().getProperty( "UpgradePath" );
    if ( dir != null )
    {
      chooser.setCurrentDirectory( new File( dir ) );
    }
    while ( true )
    {
      if ( chooser.showOpenDialog( rm ) == RMFileChooser.APPROVE_OPTION )
      {
        file = chooser.getSelectedFile();

        if ( !file.exists() )
        {
          JOptionPane.showMessageDialog( rm, file.getName() + " doesn't exist.", "File doesn't exist.",
              JOptionPane.ERROR_MESSAGE );
        }
        else if ( file.isDirectory() )
        {
          JOptionPane.showMessageDialog( rm, file.getName() + " is a directory.", "File doesn't exist.",
              JOptionPane.ERROR_MESSAGE );
        }
        else
        {
          break;
        }
      }
      else
      {
        return;
      }
    }

    System.err.println( "Opening " + file.getCanonicalPath() + ", last modified "
        + DateFormat.getInstance().format( new Date( file.lastModified() ) ) );
    DeviceUpgrade deviceUpgrade = editorPanel.getDeviceUpgrade();

    Remote remote = deviceUpgrade.getRemote();
    deviceUpgrade.reset();
    deviceUpgrade.load( file );
    JP1Frame.getProperties().put( "UpgradePath", file.getParent() );
    if ( deviceUpgrade.getRemote() != remote )
    {
      deviceUpgrade.setRemote( remote );
    }
    editorPanel.refresh();
  }

  /**
   * Import from clipboard.
   */
  public void importFromClipboard()
  {
    DeviceUpgrade deviceUpgrade = editorPanel.getDeviceUpgrade();
    ImportRawUpgradeDialog dialog = new ImportRawUpgradeDialog( this, deviceUpgrade, true );
    dialog.setVisible( true );
    editorPanel.refresh();
  }

  /**
   * Save.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void save() throws IOException
  {
    System.err.println( "DeviceUpgradeEditor.save()" );
    DeviceUpgrade deviceUpgrade = editorPanel.getDeviceUpgrade();
    RMFileChooser chooser = new RMFileChooser();
    String[] endings =
    {
      ".rmdu"
    };
    chooser.setFileFilter( new EndingFileFilter( "RemoteMaster device upgrade files (*.rmdu)", endings ) );
    RemoteMaster rm = ( RemoteMaster )SwingUtilities.getAncestorOfClass( RemoteMaster.class, this );
    File f = deviceUpgrade.getFile();
    if ( f != null )
    {
      chooser.setSelectedFile( f );
    }
    else
    {
      String path = JP1Frame.getProperties().getProperty( "UpgradePath" );
      if ( path != null )
      {
        chooser.setCurrentDirectory( new File( path ) );
      }
    }

    int returnVal = chooser.showSaveDialog( rm );
    if ( returnVal == RMFileChooser.APPROVE_OPTION )
    {
      String name = chooser.getSelectedFile().getAbsolutePath();
      if ( !name.toLowerCase().endsWith( ".rmdu" ) )
      {
        name = name + ".rmdu";
      }
      File file = new File( name );
      int rc = JOptionPane.YES_OPTION;
      if ( file.exists() )
      {
        rc = JOptionPane.showConfirmDialog( rm, file.getName() + " already exists.  Do you want to replace it?",
            "Replace existing file?", JOptionPane.YES_NO_OPTION );
      }
      if ( rc == JOptionPane.YES_OPTION )
      {
        deviceUpgrade.store( file );
      }
    }
  }

  /** The cancelled. */
  private boolean cancelled = false;

  /** The editor panel. */
  private DeviceEditorPanel editorPanel = null;

  /** The load button. */
  private JButton loadButton = new JButton( "Open" );

  /** The import button. */
  private JButton importButton = new JButton( "Import Raw" );

  /** The save as button. */
  protected JButton saveAsButton = new JButton( "Save as" );

  /** The ok button. */
  protected JButton okButton = new JButton( "OK" );

  /** The cancel button. */
  private JButton cancelButton = new JButton( "Cancel" );

  private RemoteMaster owner = null;

  private RMPanel panel = null;

  private WindowAdapter focusWindowAdapter = null;

  // private RemoteMaster rm = null;

  private Integer row = null;

}
