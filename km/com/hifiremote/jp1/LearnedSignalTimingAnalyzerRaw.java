package com.hifiremote.jp1;

public class LearnedSignalTimingAnalyzerRaw extends LearnedSignalTimingAnalyzerBase
{
  public LearnedSignalTimingAnalyzerRaw( UnpackLearned u )
  {
    super( u );
  }

  @Override
  public String getName()
  {
    return "Raw Data";
  }

  @Override
  protected int calcAutoRoundTo()
  {
    return 1;
  }

  @Override
  public boolean checkCandidacy( int roundTo )
  {
    return true;
  }

  @Override
  protected void analyzeImpl()
  {
    System.err.println( "RawAnalyzer: (" + this.hashCode() +") Analyze" );
    addAnalysis( 
        new LearnedSignalTimingAnalysis( 
            "Raw", 
            getUnpacked().getBursts(), 
            new int[][] { getUnpacked().getOneTimeDurations( getRoundTo(), true ) }, 
            new int[][] { getUnpacked().getRepeatDurations( getRoundTo(), true ) }, 
            new int[][] { getUnpacked().getExtraDurations( getRoundTo(), true ) }, 
            "", 
            false, 
            ""
        ) 
    );
  }

  @Override
  protected String getPreferredAnalysisName()
  {
    return "Raw";
  }
}
