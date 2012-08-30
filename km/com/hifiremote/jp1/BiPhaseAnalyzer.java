package com.hifiremote.jp1;

import java.util.ArrayList;
import java.util.HashMap;

public class BiPhaseAnalyzer
{
  private int RoundTo = 1;
  private UnpackLearned Data = null;
  private int Unit = 0;
  private boolean IsBiPhase = false;
  
  private int[] oneTimeDurations;
  private int[] repeatDurations;
  private int[] extraDurations;
  
  public int getRoundTo()
  {
    return RoundTo;
  }
  public int getUnit()
  {
    return Unit;
  }
  public boolean getIsBiPhase()
  {
    return (Unit > 0 && IsBiPhase);
  }
  public int[] getOneTimeDurations()
  {
    return oneTimeDurations;
  }
  public int[] getRepeatDurations()
  {
    return repeatDurations;
  }
  public int[] getExtraDurations()
  {
    return extraDurations;
  }
  public String getOneTimeDurationsString()
  {
    return UnpackLearned.durationsToString( oneTimeDurations, ";" );
  }
  public String getRepeatDurationsString()
  {
    return UnpackLearned.durationsToString( repeatDurations, ";"  );
  }
  public String getExtraDurationsString()
  {
    return UnpackLearned.durationsToString( extraDurations, ";" );
  }

  public BiPhaseAnalyzer( UnpackLearned data )
  {
    Data = data;
    Unit = 0;
    AutoAnalyze();
  }
  
  public BiPhaseAnalyzer( UnpackLearned data, int roundTo )
  {
    Data = data;
    RoundTo = roundTo;
    
    Unit = CheckForBiPhase();
    Analyze();
  }
  
  private int CheckForBiPhase()
  {
    int[] durations = Data.getDurations( RoundTo, false );
    
    HashMap<Integer,Integer> hist = new HashMap<Integer,Integer>();
    
    for ( int i = 2; i < durations.length - 2; i++ )
    {
      int value = Math.abs( durations[i] );
      if ( !hist.containsKey( value ) )
        hist.put( value, 1 );
      else
        hist.put( value, hist.get( value ) + 1 );
    }   
    
    int min = Integer.MAX_VALUE;
    for ( int d: hist.keySet() )
      if ( d < min )
        min = d;

    for ( int d: hist.keySet() )
      if ( d % min != 0 )
        return 0;
    
    return min;
  }
  
  private void AutoAnalyze()
  {
    HashMap<Integer,Integer> hist = new HashMap<Integer,Integer>();
    
    int[] durations = Data.getDurations( 1, false );
    for ( int i = 2; i < durations.length - 2; i++ )
    {
      int value = Math.abs( durations[i] );
      if ( !hist.containsKey( value ) )
        hist.put( value, 1 );
      else
        hist.put( value, hist.get( value ) + 1 );
    }
    int max = Integer.MIN_VALUE;
    for ( int k: hist.keySet() )
      if ( k > max )
        max = k;
    
    RoundTo = 0;
    while ( Unit == 0 && RoundTo < max + 100 )
    {
      RoundTo += 10;
      Unit = CheckForBiPhase();
    }

    Analyze();
  }
  
  private void Analyze()
  {
    
    if ( Unit == 0 )
      return;
    int[] temp = Data.getOneTimeDurations( RoundTo, true );
    oneTimeDurations = AnalyzeDurations( temp );
    if ( temp != null && temp.length > 0 && oneTimeDurations == null )
    {
      IsBiPhase = false;
      return;
    }
    temp = Data.getRepeatDurations( RoundTo, true );
    repeatDurations = AnalyzeDurations( temp );
    if ( temp != null && temp.length > 0 && repeatDurations == null )
    {
      IsBiPhase = false;
      return;
    }
    temp = Data.getExtraDurations( RoundTo, true );
    extraDurations = AnalyzeDurations( temp );
    if ( temp != null && temp.length > 0 && extraDurations == null )
    {
      IsBiPhase = false;
      return;
    }
    IsBiPhase = true;
  }
  
  private int[] AnalyzeDurations( int[] durations )
  {
    //int[] durations = Data.getDurations( RoundTo );
    if ( durations == null || durations.length == 0 )
      return null;
    
    if ( durations.length > 3 )
      System.err.println( "BiPhaseAnalyze: Analyzing set of " + durations.length + " durations... ( " + durations[0] + " " + durations[1] + " " + durations[2] + " " + durations[3] + " ... )");
    else
      System.err.println( "BiPhaseAnalyze: Analyzing set of " + durations.length + " durations...");
    
    int[] leadIn = new int[2];
    leadIn[0] = durations[0];
    leadIn[1] = durations[1];
    System.err.println( "BiPhaseAnalyze: LeadIn = " + leadIn[0] + " " + leadIn[1]);
    
    int[] leadOut = new int[2];
    leadOut[0] = durations[durations.length -1];
    leadOut[1] = 0;
    System.err.println( "BiPhaseAnalyze: LeadOut = " + leadOut[0]);

    // first we try straight data not bothering the lead in    
    int[] temp = new int[durations.length - 3];
    for ( int i = 2; i < durations.length - 1; i++ )
      temp[i-2] = durations[i];
    
    // attempt to analyze...note leadOut may be changed to finish final pair
    ArrayList<int[]> biPhase = AnalyzeData(temp, leadOut);
    
    // if failed, next we try taking from the lead in
    if ( biPhase == null && Math.abs( leadIn[1] ) >= Math.abs( temp[0] ))
    {
      int num = Math.abs( leadIn[1] ) / Unit;
      if ( num > Math.abs( temp[0] ) / Unit )
        num = Math.abs( temp[0] ) / Unit;
      for ( int n = 1; n <= num; n++ )
      {
        int[] newTemp = new int[temp.length + 1];
        newTemp[0] = -Unit * n;
        for ( int i = 2; i < durations.length - 1; i++ )
          newTemp[i-1] = durations[i];
        
        biPhase = AnalyzeData(temp, leadOut);
        if ( biPhase != null )
        {
          // we analyzed successfully, so subtract from lead in
          leadIn[1] += Unit * n;
          break;                  
        }
      }
    }
    
    if ( biPhase != null )
    {
      int[] data = new int[biPhase.size() * 2 + ( leadOut[1] != 0 ? 4 : 3 ) ];
      int i = 0;
      data[i++] = leadIn[0];
      data[i++] = leadIn[1];
      for ( int[] r: biPhase )
      {
        data[i++] = r[0];
        data[i++] = r[1];
      }
      data[i++] = leadOut[0];
      if ( leadOut[1] != 0 )
        data[i++] = leadOut[1];
      return data;
    }
  
    return null;
  }
  
  private ArrayList<int[]> AnalyzeData(int[] durations, int[] leadOut)
  {
    if ( durations.length > 3 )
      System.err.println( "BiPhaseAnalyze: Analyzing data set of " + durations.length + " durations... ( " + durations[0] + " " + durations[1] + " " + durations[2] + " " + durations[3] + " ... )");
    else
      System.err.println( "BiPhaseAnalyze: Analyzing data set of " + durations.length + " durations...");      
    ArrayList<int[]> results = new ArrayList<int[]>();

    int[] p = null;
    for ( int d: durations )
    {
      // starting a new pair
      if (p == null)
      {
        p = new int[2];
        p[0] = d;
      }
      // next finishes our current pair
      else if ( p[0] == -d )
      {
        p[1] = d;
        results.add(p);
        p = null;
      }
      // next needs to be split to finish our pair and start the next
      else if ( Math.abs( p[0] ) < Math.abs( d ) && Math.abs( d ) % Math.abs( p[0] ) == 0)
      {
        p[1] = -p[0];
        results.add(p);        
        d += p[0];
        p = new int[2];
        p[0] = d;
      }
      // error...unable to parse input durations as bi-phase
      else
      {
        return null;
      }
    }
    
    System.err.println("Found " + results.size() + " result pairs...");
    
    // try to finish last pair from the lead out
    // we only do this if we ended with a positive pulse
    // and we have an odd number of pairs already
    if ( p != null && p[0] > 0 && p[0] < -leadOut[0] && results.size()%2 == 1 )
    {
      System.err.println("Using leadout to finish final pair...");
      p[1] = -p[0];
      leadOut[0] += p[0];
      results.add(p);
      p = null;
    }
    // if we had an even number of pairs or leadout was not big enough to finish a pair, 
    // we assume our final positive is part of leadout
    else if ( p!= null && p[0] > 0 )
    {
      System.err.println("Assuming final On time is part of leadout...");
      leadOut[1] = leadOut[0];
      leadOut[0] = p[0];
      p = null;
    }
    // otherwise error condition
    else if ( p != null )
      return null;

    // return the results
    return results;
  }
  
}
