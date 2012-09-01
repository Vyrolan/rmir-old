package com.hifiremote.jp1;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class LearnedSignalTimingAnalyzerBase
{
  // collection of successful analyses
  private HashMap<String,LearnedSignalTimingAnalysis> _Analyses;
  
  // data from the learned signal
  private UnpackLearned _Unpacked;
  protected UnpackLearned getUnpacked() { return _Unpacked; }
  
  // rounding info...rounding can be set or it can be automatically determined by the analyzer
  private int _RoundTo = -1;
  public int getRoundTo() 
  {
    if ( _RoundTo == -1 )
      autoSetRounding();
    return _RoundTo; 
  }
  public void setRoundTo( int roundTo ) 
  { 
    if ( roundTo > 0 && _RoundTo != roundTo && checkCandidacy( roundTo ) )
    {
      _RoundTo = roundTo;
      _Analyses = null; // force reanalyze on next access
    }
  }
  private boolean _IsAutoRounding = false;
  public boolean getIsAutoRounding() { return _IsAutoRounding; }
  public boolean autoSetRounding()
  {
    int r = calcAutoRoundTo();
    if ( r > 0 && _RoundTo != r )
    {
      _RoundTo = r;
      _Analyses = null; // force reanalyze on next access
    }
    _IsAutoRounding = ( r > 0 );
    return _IsAutoRounding;
  }

  // simple constructor
  public LearnedSignalTimingAnalyzerBase( UnpackLearned u )
  {
    _Unpacked = u;
  }
  
  // provide a name for the analyzer
  public abstract String getName();
  // calculate a preferred optimal rounding
  protected abstract int calcAutoRoundTo();
  // do a quick check if the signal can be analyzed with the given rounding
  public abstract boolean checkCandidacy( int roundTo );
  // analyze the symbol
  protected abstract void analyzeImpl();
  // get the preferred analysis that is the "best match"
  protected abstract String getPreferredAnalysisName();  
  
  private void analyze()
  {
    synchronized (this)
    {
      if ( _Analyses != null ) return; // another thread did it
      int r = getRoundTo();
      _Analyses = new HashMap<String,LearnedSignalTimingAnalysis>();
      if ( checkCandidacy( r ) )
        analyzeImpl();
    }
  }
  
  public boolean hasAnalyses()
  {
    return ( getAnalyses().size() > 0 );
  }
  public LearnedSignalTimingAnalysis getPreferredAnalysis()
  {
    return getAnalysis( getPreferredAnalysisName() );
  }
  public LearnedSignalTimingAnalysis getAnalysis( String name )
  {
    return getAnalyses().get( name );
  }
  public HashMap<String,LearnedSignalTimingAnalysis> getAnalyses()
  {
    if ( _Analyses == null )
      analyze();
    return _Analyses;
  }
  protected void addAnalyzedSignal( LearnedSignalTimingAnalysis analysis )
  {
    if ( _Analyses == null ) return; // how did this even happen?  should only be called from within analyze()
    _Analyses.put( analysis.getName(), analysis );
  }

  protected static int[] arrayListToArray( ArrayList<Integer> data )
  {
    int r = 0;
    int[] result = new int[data.size()];
    for ( int d: data )
      result[r++] = d;
    return result;
  }

  public static int[][] splitDurationsBeforeLeadIn( int[] durations )
  {
    int[][] seps = new int[1][];
    seps[0] = new int[2];
    seps[0][0] = durations[0];
    seps[0][1] = durations[1];    
    return splitDurations( durations, seps, false );
  }
  public static int[][] splitDurations( int[] durations, int[][] separators, boolean splitAfter )
  {
    ArrayList<int[]> results = new ArrayList<int[]>();
    ArrayList<Integer> list = new ArrayList<Integer>();
    
    int i = 0;
    while ( i < durations.length )
    {
      // look ahead for our separators
      int[] separator = null;
      for ( int[] tempSeparator: separators )
      {
        separator = tempSeparator;
        boolean found = false;
        if ( separator != null && separator.length > 0 && durations[i] == separator[0] )
        {
          found = true;
          for ( int s = 1; s < separator.length; s++ )
            found = ( found && i+s < durations.length && durations[i+s] == separator[s] );
        }
        if ( found )
          break;
        separator = null;
      }
      
      // if no separator, just add to list and move on
      if ( separator == null || ( i == 0 && !splitAfter ) )
      {
        list.add( durations[i++] );
      }
      else
      {
        if ( splitAfter )
        {
          // split after the separator, so fill out list with separator
          for ( int s = 0; s < separator.length; s++ )
            list.add( durations[i++] );
          // add completed split component to results
          results.add( arrayListToArray( list ) );
          // clear for next
          list.clear();
        }
        else
        {
          // split before the separator, so list is a complete split component
          results.add( arrayListToArray( list ) );
          // clear for next
          list.clear();
          // we know we can add all the separator pieces, so do that now to skip past them
          for ( int s = 0; s < separator.length; s++ )
            list.add( durations[i++] );         
        }
      }
    }
    
    // add final list
    if ( !list.isEmpty() )
      results.add( arrayListToArray( list ) );
    
    i = 0;
    int[][] data = new int[results.size()][];
    for ( int[] r: results )
      data[i++] = r;
    
    return data;
  } 
}