package com.hifiremote.jp1;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class LearnedSignalTimingAnalyzerBase
{
  private UnpackLearned _Unpacked;
  protected UnpackLearned getUnpacked() { return _Unpacked; }
  
  protected int _RoundTo;
  public int getRoundTo() { return _RoundTo; }
  public void setRoundTo( int roundTo ) 
  { 
    if ( roundTo < 1 )
    {
      if ( !autoSetRounding() )
        _RoundTo = 1;
    }
    else
      _RoundTo = roundTo; 
  }
  
  private boolean _IsAutoRounding;
  public boolean getIsAutoRounding() { return _IsAutoRounding; }
  protected abstract int calcAutoRoundTo();
  public boolean autoSetRounding()
  {
    int r = calcAutoRoundTo();
    if ( r > 0 )
      _RoundTo = r;
    _IsAutoRounding = ( r > 0 );
    return _IsAutoRounding;
  }

  protected HashMap<String,LearnedSignalTimingAnalysis> _Analyses;

  public LearnedSignalTimingAnalyzerBase( UnpackLearned u )
  {
    _Unpacked = u;    
    _Analyses = new HashMap<String,LearnedSignalTimingAnalysis>();
  }
  
  public abstract boolean checkCandidacy();
  protected abstract void analyzeImpl();
  public void analyze()
  {
    _Analyses.clear();
    if ( checkCandidacy() )
      analyzeImpl();
  }
  
  public boolean hasAnalyses()
  {
    return ( _Analyses.size() > 0 );
  }
  public HashMap<String,LearnedSignalTimingAnalysis> getAnalyses()
  {
    return _Analyses;
  }
  protected void addAnalyzedSignal( LearnedSignalTimingAnalysis analysis )
  {
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
      if ( separator == null )
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
    
    i = 0;
    int[][] data = new int[results.size()][];
    for ( int[] r: results )
      data[i++] = r;
    
    return data;
  } 
}