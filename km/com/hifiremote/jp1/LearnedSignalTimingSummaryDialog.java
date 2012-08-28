package com.hifiremote.jp1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

// TODO: Auto-generated Javadoc
/**
 * The Class LearnedSignalTimingSummaryDialog.
 */
public class LearnedSignalTimingSummaryDialog extends JDialog implements ActionListener
{
  public static void showDialog( Component locationComp, RemoteConfiguration config )
  {
    if ( dialog == null )
      dialog = new LearnedSignalTimingSummaryDialog( locationComp );
    dialog.config = config;   
    dialog.generateSummary();
    dialog.pack();
    dialog.setLocationRelativeTo( locationComp );
    dialog.setVisible( true );
  }
  
  public static void reset()
  {
    if ( dialog != null )
    {
      dialog.dispose();
      dialog = null;
    }
  }

  private LearnedSignalTimingSummaryDialog( Component c )
  {
    super( ( JFrame )SwingUtilities.getRoot( c ) );
    setTitle( "Learned Signal Timing Summary" );
    setModal( true );

    JComponent contentPane = ( JComponent )getContentPane();
    contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    summaryTextArea.setEditable( false );
    summaryTextArea.setLineWrap( false );
    JScrollPane scrollPane = new JScrollPane( summaryTextArea );
    scrollPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( "Timing Summary" ), scrollPane.getBorder() ) );
    contentPane.add( scrollPane, BorderLayout.CENTER );
       
    Box bottomBox = Box.createVerticalBox();
    contentPane.add( bottomBox, BorderLayout.PAGE_END );
    
    // Add the action buttons
    JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    bottomBox.add( buttonPanel );
    
    okButton.addActionListener( this );
    okButton.setToolTipText( "Close the Summary" );
    buttonPanel.add( okButton );
  }
  
  private static String durationsToString( int[] data, int r )
  {
    StringBuilder str = new StringBuilder();
    if ( data != null && data.length != 0 )
    {
      for ( int i = 0; i < data.length; i++ )
      {
        // Format changed to allow pasting to IRScope as timing list
        if ( i > 0 /* && ( i & 1 ) == 0 */ )
          str.append( ' ' );

        str.append( ( i & 1 ) == 0 ? "+" : "-" );
        if (r > 1)
          str.append( Math.round( (double)data[i] / (double)r ) * r );
        else
          str.append( data[i] );
      }
    }
    if ( str.length() == 0 )
      return "** No signal **";

    return str.toString();
  }

  private void generateSummary()
  {
    List<LearnedSignal> signals = this.config.getLearnedSignals();
    Remote remote = this.config.getRemote();
    
    StringBuilder summary = new StringBuilder();
    summary.append( "LEARNED SIGNALS:\nLEARNED RAW DATA:\n" );
    summary.append( "#\tDevice\tKey\tNotes\tFreq\tRaw Timing Data\n" );
    int i = 1;
    for ( LearnedSignal s: signals )
    {
      UnpackLearned ul = s.getUnpackLearned();
      summary.append( i++ );
      summary.append( '\t' );
      summary.append( remote.getDeviceButton( s.getDeviceButtonIndex() ).getName() );
      summary.append( '\t' );
      summary.append( remote.getButtonName( s.getKeyCode() ) );
      summary.append( '\t' );
      summary.append( (s.getNotes() == null ? "" : s.getNotes()) );
      if ( ul.ok )
      {
        summary.append( '\t' );
        summary.append( ul.frequency );
        summary.append( '\t' );
        boolean indent = false;
        if ( ul.oneTime > 0 )
        {
          if ( indent )
            summary.append( "\n\t\t\t\t\t" );
          else
            indent = true;
          summary.append( "Once:\t" );
          summary.append( durationsToString( ul.getOneTimeDurations(), 1 ) );
        }
        if ( ul.repeat > 0 )
        {
          if ( indent )
            summary.append( "\n\t\t\t\t\t" );
          else
            indent = true;
          summary.append( "Repeat:\t" );
          summary.append( durationsToString( ul.getRepeatDurations(), 1 ) );
        }
        if ( ul.extra > 0 )
        {
          if ( indent )
            summary.append( "\n\t\t\t\t\t" );
          else
            indent = true;
          summary.append( "Extra:\t" );
          summary.append( durationsToString( ul.getExtraDurations(), 1 ) );
        }
      }
      else
        summary.append( "** No signal **" );
      summary.append( '\n' );
    }
    
    summaryTextArea.setText( summary.toString() );
  }
  
  
  /*
   * (non-Javadoc)
   * 
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed( ActionEvent event )
  {
    Object source = event.getSource();
    if ( source == okButton )
    {
      setVisible( false );
    }
  }
  
  private RemoteConfiguration config = null;

  /** The ok button. */
  private JButton okButton = new JButton( "OK" );

  private JTextArea summaryTextArea = new JTextArea( 30, 80 );

  /** The dialog. */
  private static LearnedSignalTimingSummaryDialog dialog = null;
}