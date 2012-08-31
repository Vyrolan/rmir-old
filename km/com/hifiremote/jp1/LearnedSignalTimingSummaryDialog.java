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
    
    buttonPanel.add( new JLabel( "(Note: Rounding has no effect on Bi-Phase signals unless it still yields a legal Bi-Phase signal.)  Round To: " ) );
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
  
  private boolean appendDurations( StringBuilder summary, String durations, int leadInTime1, int leadInTime2, boolean indent )
  {
    String leadIn = " \\+" + leadInTime1 + " -" + leadInTime2;
    System.err.println( "Replacing for newlines with: " + leadIn );
    System.err.println( "Durations: " + durations );
    String temp = durations.replaceAll( leadIn, "\n"+leadIn );
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
    boolean roundingSet = false; 
    if ( roundText != null && !roundText.isEmpty() )
    {
      try
      {
        r = Integer.parseInt( roundText );
        roundingSet = true;
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
        
        BiPhaseAnalyzer biPhase = new BiPhaseAnalyzer( ul );
        if ( roundingSet )
        {
          BiPhaseAnalyzer tempBiPhase = new BiPhaseAnalyzer( ul, r );
          if ( tempBiPhase.getIsBiPhase() )
            biPhase = tempBiPhase;
        }
        
        if ( biPhase.getIsBiPhase() )
          r = biPhase.getRoundTo();

        //System.err.println( remote.getDeviceButton( s.getDeviceButtonIndex() ).getName() + " " + remote.getButtonName( s.getKeyCode() ) + ": " + ul.oneTime + ", " + ul.repeat + ", " + ul.extra );
        int leadInTime1 = ul.durations[0];
        int leadInTime2 = ul.durations[1];
        leadInTime1 = ((int) Math.round( (double)leadInTime1 / (double)r )) * r;
        leadInTime2 = ((int) Math.round( (double)leadInTime2 / (double)r )) * r;

        boolean indent = false;
        String temp;
        if ( ul.oneTime > 0 && ul.extra > 0 && ul.repeat == 0 )
        {
          if ( biPhase.getIsBiPhase() )
            temp = UnpackLearned.durationsToString( biPhase.getOneTimeDurations() , ";" );
          else
            temp = UnpackLearned.durationsToString( ul.getOneTimeDurations( r, true ), "" );
          if ( biPhase.getIsBiPhase() )
            temp += "; " + UnpackLearned.durationsToString( biPhase.getExtraDurations() , ";" );
          else
            temp += " " + UnpackLearned.durationsToString( ul.getExtraDurations( r, true ), "" );
          summary.append( "Once:\t" );
          appendDurations( summary, temp, leadInTime1, leadInTime2, indent );
        }
        else
        {
          if ( ul.oneTime > 0 )
          {
            indent = true;
            summary.append( "Once:\t" );
            if ( biPhase.getIsBiPhase() )
              temp = UnpackLearned.durationsToString( biPhase.getOneTimeDurations() , ";" );
            else
              temp = UnpackLearned.durationsToString( ul.getOneTimeDurations( r, true ), "" );            
            appendDurations( summary, temp, leadInTime1, leadInTime2, indent );
          }
          if ( ul.repeat > 0 )
          {
            if ( indent )
              summary.append( "\n\t\t\t\t\t" );
            else
              indent = true;
            summary.append( "Repeat:\t" );
            if ( biPhase.getIsBiPhase() )
              temp = UnpackLearned.durationsToString( biPhase.getRepeatDurations() , ";" );
            else
              temp = UnpackLearned.durationsToString( ul.getRepeatDurations( r, true ), "" );
            appendDurations( summary, temp, leadInTime1, leadInTime2, indent );
          }
          if ( ul.extra > 0 )
          {
            if ( indent )
              summary.append( "\n\t\t\t\t\t" );
            else
              indent = true;
            summary.append( "Extra:\t" );
            if ( biPhase.getIsBiPhase() )
              temp = UnpackLearned.durationsToString( biPhase.getExtraDurations() , ";" );
            else
              temp = UnpackLearned.durationsToString( ul.getExtraDurations( r, true ), "" );
            appendDurations( summary, temp, leadInTime1, leadInTime2, indent );
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
