package com.hifiremote.jp1;

import java.util.Iterator;

public class LearnedSignalTimingAnalyzer
{
  private LearnedSignalTimingAnalyzerBase[] _Analyzers;
  private String[] _AnalyzerNames;
  private UnpackLearned _Data;

  protected int _RoundTo;
  public int getRoundTo() { return _RoundTo; }
  public void setRoundTo( int roundTo ) 
  { 
    if ( _RoundTo != roundTo )
    {
      _RoundTo = roundTo;
      for ( LearnedSignalTimingAnalyzerBase a: _Analyzers )
        a.setRoundTo( roundTo );
    }
  }
  
  public LearnedSignalTimingAnalyzer( UnpackLearned u )
  {
    _Data = u;
    _Analyzers = new LearnedSignalTimingAnalyzerBase[]
        {
          new LearnedSignalTimingAnalyzerBiPhase( u ),
          new LearnedSignalTimingAnalyzerRaw( u )
        };
    _AnalyzerNames = new String[_Analyzers.length];
    for ( int n = 0; n < _Analyzers.length; n++ )
      _AnalyzerNames[n] = _Analyzers[n].getName(); 
  }
  
  public boolean getIsValid()
  {
    return _Data.ok;
  }
  
  public String[] getAnalyzerNames()
  {
    return _AnalyzerNames;
  }
  
  public LearnedSignalTimingAnalyzerBase getAnalyzer( String name )
  {
    for ( LearnedSignalTimingAnalyzerBase a: _Analyzers )
      if ( a.getName().equals(  name  ) )
        return a;
    // no match
    return null;
  }
  
  public LearnedSignalTimingAnalyzerBase getAnalyzer( int i )
  {
    return _Analyzers[i];
  }
  
  public LearnedSignalTimingAnalyzerBase getPreferredAnalyzer()
  {
    for ( LearnedSignalTimingAnalyzerBase a: _Analyzers )
      if ( a.hasAnalyses() )
        return a;
    // can't actually happen since raw analyzer will always have one
    return null;
  }
    
  public int getNumAnalyzers()
  {
    return _Analyzers.length;
  }
  
  public Iterator<LearnedSignalTimingAnalyzerBase> getAnalyzers()
  {
    return new Iterator<LearnedSignalTimingAnalyzerBase>() 
    {
      private int i = 0;      
      @Override
      public boolean hasNext() { return (i+1 < _Analyzers.length); }
      @Override
      public LearnedSignalTimingAnalyzerBase next() { return _Analyzers[++i]; }
      @Override
      public void remove() { }
    };
  }
}
