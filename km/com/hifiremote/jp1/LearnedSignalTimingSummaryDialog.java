package com.hifiremote.jp1;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    
    buttonPanel.add( new JLabel( " Round To: " ) );
    buttonPanel.add( burstRoundBox );
    buttonPanel.add( new JLabel( "          ") );
    burstRoundBox.setColumns( 8 );
    burstRoundBox.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        generateSummary();
      }
      public void removeUpdate(DocumentEvent e) {
        generateSummary();
      }
      public void insertUpdate(DocumentEvent e) {
        generateSummary();
      }
    });
    
    okButton.addActionListener( this );
    okButton.setToolTipText( "Close the Summary" );
    buttonPanel.add( okButton );
  }
  
  private static String durationsToString( int[] data )
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
        str.append( data[i] );
      }
    }
    if ( str.length() == 0 )
      return "** No signal **";

    return str.toString();
  }

  private boolean appendDurations( StringBuilder summary, String durations, int leadOutTime, boolean indent )
  {
    String leadOut = "-" + leadOutTime + " ";
    String temp = durations.replaceAll( leadOut, leadOut+"\n" );
    String[] lines = temp.split( "\n" );
    for ( int l = 0; l < lines.length; l++ )
    {
      if ( l > 0 )
      {
        if ( indent )
          summary.append( "\n\t\t\t\t\t" );
        else
          indent = true;
        summary.append( "More:\t" );
      }
      summary.append(lines[l]);
    }
    return indent;
  }
  private void generateSummary()
  {
    int r = 1;
    String roundText = burstRoundBox.getText();
    if ( roundText != null && !roundText.isEmpty() )
    {
      try
      {
        r = Integer.parseInt( roundText );
      }
      catch (NumberFormatException e)
      {
        r = 1;
      }
    }
    
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

        //System.err.println( remote.getDeviceButton( s.getDeviceButtonIndex() ).getName() + " " + remote.getButtonName( s.getKeyCode() ) + ": " + ul.oneTime + ", " + ul.repeat + ", " + ul.extra );
        int leadOutTime = ul.durations[ul.durations.length - 1];
        leadOutTime = ((int) Math.round( (double)leadOutTime / (double)r )) * r;

        boolean indent = false;
        if ( ul.oneTime > 0 && ul.extra > 0 && ul.repeat == 0 )
        {
          String temp = durationsToString( ul.getOneTimeDurations( r ) );
          temp += " ";
          temp += durationsToString( ul.getExtraDurations( r ) );
          summary.append( "Once:\t" );
          appendDurations( summary, temp, leadOutTime, indent );
        }
        else
        {
          if ( ul.oneTime > 0 )
          {
            indent = true;
            summary.append( "Once:\t" );
            appendDurations( summary, durationsToString( ul.getOneTimeDurations( r ) ), leadOutTime, indent );
          }
          if ( ul.repeat > 0 )
          {
            if ( indent )
              summary.append( "\n\t\t\t\t\t" );
            else
              indent = true;
            summary.append( "Repeat:\t" );
            appendDurations( summary, durationsToString( ul.getRepeatDurations( r ) ), leadOutTime, indent );
          }
          if ( ul.extra > 0 )
          {
            if ( indent )
              summary.append( "\n\t\t\t\t\t" );
            else
              indent = true;
            summary.append( "Extra:\t" );
            appendDurations( summary, durationsToString( ul.getExtraDurations( r ) ), leadOutTime, indent );
          }
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

  private JTextField burstRoundBox = new JTextField();
  
  /** The ok button. */
  private JButton okButton = new JButton( "OK" );

  private JTextArea summaryTextArea = new JTextArea( 30, 80 );

  /** The dialog. */
  private static LearnedSignalTimingSummaryDialog dialog = null;
    
}
