package com.hifiremote.jp1;

public class LearnedSignalTimingAnalysis
{
  private String _Name;
  private String _Message;
  private int[] _Bursts;
  private int[][] _Durations;
  private int[][] _OneTimeDurations;
  private int[][] _RepeatDurations;
  private int[][] _ExtraDurations;
  private String _Separator;
  private boolean _SeparateOdd;
  
  public String getName() { return _Name; }
  public String getMessage() { return _Message; }
  public int[] getBursts() { return _Bursts; }
  public int[][] getDurations() { return _Durations; }
  public int[][] getOneTimeDurations() { return _OneTimeDurations; }
  public int[][] getRepeatDurations() { return _RepeatDurations; }
  public int[][] getExtraDurations() { return _ExtraDurations; }
  
  public LearnedSignalTimingAnalysis( String name, int[] bursts, int[][] durations, int[][] oneTime, int[][] repeat, int[][] extra, String sep, boolean sepOdd, String message )
  {
    _Name = name;
    _Bursts = bursts;
    _Durations = durations;
    _OneTimeDurations = oneTime;
    _RepeatDurations = repeat;
    _ExtraDurations = extra;
    _Separator = sep;
    _SeparateOdd = sepOdd;
    _Message = message;
  }
  
  private String[] makeDurationStringList( int[][] durations )
  {
    int r = 0;
    String[] results = new String[durations.length];
    for ( int[] d: durations )
      results[r++] = durationsToString( d, _Separator, _SeparateOdd );
    return results;
  }
  public String[] getDurationStringList()
  {
    return makeDurationStringList( getDurations() );
  }
  public String[] getOneTimeDurationStringList()
  {
    return makeDurationStringList( getOneTimeDurations() );
  }
  public String[] getRepeatDurationStringList()
  {
    return makeDurationStringList( getRepeatDurations() );
  }
  public String[] getExtraDurationStringList()
  {
    return makeDurationStringList( getExtraDurations() );
  }
  
  public String getBurstString()
  {
    return durationsToString( getBursts(), _Separator, _SeparateOdd );
  }
  public String getDurationString()
  {
    return durationsToString( joinDurations( getDurations() ), _Separator, _SeparateOdd );
  }
  public String getOneTimeDurationString()
  {
    return durationsToString( joinDurations( getOneTimeDurations() ), _Separator, _SeparateOdd );
  }
  public String getRepeatDurationString()
  {
    return durationsToString( joinDurations( getRepeatDurations() ), _Separator, _SeparateOdd );
  }
  public String getExtraDurationString()
  {
    return durationsToString( joinDurations( getExtraDurations() ), _Separator, _SeparateOdd );
  }

  public static int[] joinDurations( int[][] durations )
  {
    int num = 0;
    for ( int[] d: durations )
      num += d.length;
    
    int r = 0;
    int[] result = new int[num];
    for ( int[] duration: durations )
      for ( int d: duration )
        result[r++] = d;
        
    return result;
  }
  
  public static String durationsToString( int[] data, String sep, boolean sepOdd )
  {
    StringBuilder str = new StringBuilder();
    if ( data != null && data.length != 0 )
    {
      boolean isSigned = false;
      for ( int d: data )
        if ( d < 0 )
        {
          isSigned = true;
          break;
        }
  
      for ( int i = 0; i < data.length; i++ )
      {
        if ( i > 0 )
          str.append( ' ' );
        if ( !isSigned )
          str.append( ( i & 1 ) == 0 ? "+" : "-" );
        else if ( data[i] > 0 )
            str.append( '+' );
        str.append( data[i] );
        if ( ( i % 2 ) == ( sepOdd ? 0 : 1 ) )
          str.append( sep );
      }
    }
    if ( str.length() == 0 )
      return "** No signal **";
  
    return str.toString();
  }
}