package com.hifiremote.jp1.assembler;

public class P6805data
{
  public static final String[][] AddressModes = {
    { "Nil", "B0", "" },
    { "Dir", "B1Z1", "$%02X" },
    { "Dir2", "B2R2Z1", "$%02X, $%04X" }, 
    { "Rel", "B1R1", "$%04X" },
    { "Ix1", "B1Z1", "$%02X, X" },
    { "Ix2", "B2A1", "$%02X%02X, X" }, 
    { "Ix", "B0", "X" },
    { "Imm", "B1", "#$%02X" }, 
    { "Ext", "B2A1", "$%02X%02X" },
    { "EQU4", "", "$%04X" },
    { "EQU2", "", "$%02X" }
  };

  public static final String[][] Instructions = {
    { "BRSET0", "Dir2" },     { "BRCLR0", "Dir2" },
    { "BRSET1", "Dir2" },     { "BRCLR1", "Dir2" },
    { "BRSET2", "Dir2" },     { "BRCLR2", "Dir2" },
    { "BRSET3", "Dir2" },     { "BRCLR3", "Dir2" },
    { "BRSET4", "Dir2" },     { "BRCLR4", "Dir2" },
    { "BRSET5", "Dir2" },     { "BRCLR5", "Dir2" },
    { "BRSET6", "Dir2" },     { "BRCLR6", "Dir2" },
    { "BRSET7", "Dir2" },     { "BRCLR7", "Dir2" },

    { "BSET0", "Dir" },       { "BCLR0", "Dir" },
    { "BSET1", "Dir" },       { "BCLR1", "Dir" },
    { "BSET2", "Dir" },       { "BCLR2", "Dir" },
    { "BSET3", "Dir" },       { "BCLR3", "Dir" },
    { "BSET4", "Dir" },       { "BCLR4", "Dir" },
    { "BSET5", "Dir" },       { "BCLR5", "Dir" },
    { "BSET6", "Dir" },       { "BCLR6", "Dir" },
    { "BSET7", "Dir" },       { "BCLR7", "Dir" },

    { "BRA", "Rel" },         { "BRN", "Rel" },
    { "BHI", "Rel" },         { "BLS", "Rel" },
    { "BCC", "Rel" },         { "BCS", "Rel" },
    { "BNE", "Rel" },         { "BEQ", "Rel" },
    { "BHCC", "Rel" },        { "BHCS", "Rel" },
    { "BPL", "Rel" },         { "BMI", "Rel" },
    { "BMC", "Rel" },         { "BMS", "Rel" },
    { "BIL", "Rel" },         { "BIH", "Rel" },

    { "NEG", "Dir" },         { "*", "Nil" },
    { "*", "Nil" },           { "COM", "Dir" },
    { "LSR", "Dir" },         { "*", "Nil" },
    { "ROR", "Dir" },         { "ASR", "Dir" },
    { "ASL", "Dir" },         { "ROL", "Dir" },
    { "DEC", "Dir" },         { "*", "Nil" },
    { "INC", "Dir" },         { "TST", "Dir" },
    { "*", "Nil" },           { "CLR", "Dir" },

    { "NEGA", "Nil" },        { "*", "Nil" },
    { "MUL", "Nil" },         { "COMA", "Nil" },
    { "LSRA", "Nil" },        { "*", "Nil" },
    { "RORA", "Nil" },        { "ASRA", "Nil" },
    { "ASLA", "Nil" },        { "ROLA", "Nil" },
    { "DECA", "Nil" },        { "*", "Nil" },
    { "INCA", "Nil" },        { "TSTA", "Nil" },
    { "*", "Nil" },           { "CLRA", "Nil" },

    { "NEGX", "Nil" },        { "*", "Nil" },
    { "*", "Nil" },           { "COMX", "Nil" },
    { "LSRX", "Nil" },        { "*", "Nil" },
    { "RORX", "Nil" },        { "ASRX", "Nil" },
    { "ASLX", "Nil" },        { "ROLX", "Nil" },
    { "DECX", "Nil" },        { "*", "Nil" },
    { "INCX", "Nil" },        { "TSTX", "Nil" },
    { "*", "Nil" },           { "CLRX", "Nil" },

    { "NEG", "Ix1" },         { "*", "Nil" },
    { "*", "Nil" },           { "COM", "Ix1" },
    { "LSR", "Ix1" },         { "*", "Nil" },
    { "ROR", "Ix1" },         { "ASR", "Ix1" },
    { "ASL", "Ix1" },         { "ROL", "Ix1" },
    { "DEC", "Ix1" },         { "*", "Nil" },
    { "INC", "Ix1" },         { "TST", "Ix1" },
    { "*", "Nil" },           { "CLR", "Ix1" },

    { "NEG", "Ix" },          { "*", "Nil" },
    { "*", "Nil" },           { "COM", "Ix" },
    { "LSR", "Ix" },          { "*", "Nil" },
    { "ROR", "Ix" },          { "ASR", "Ix" },
    { "ASL", "Ix" },          { "ROL", "Ix" },
    { "DEC", "Ix" },          { "*", "Nil" },
    { "INC", "Ix" },          { "TST", "Ix" },
    { "*", "Nil" },           { "CLR", "Ix" },

    { "RTI", "Nil" },         { "RTS", "Nil" },
    { "*", "Nil" },           { "SWI", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "STOP", "Nil" },        { "WAIT", "Nil" },

    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "*", "Nil" },
    { "*", "Nil" },           { "TAX", "Nil" },
    { "CLC", "Nil" },         { "SEC", "Nil" },
    { "CLI", "Nil" },         { "SEI", "Nil" },
    { "RSP", "Nil" },         { "NOP", "Nil" },
    { "*", "Nil" },           { "TXA", "Nil" },

    { "SUB", "Imm" },         { "CMP", "Imm" },
    { "SBC", "Imm" },         { "CPX", "Imm" },
    { "AND", "Imm" },         { "BIT", "Imm" },
    { "LDA", "Imm" },         { "*", "Nil" },
    { "EOR", "Imm" },         { "ADC", "Imm" },
    { "ORA", "Imm" },         { "ADD", "Imm" },
    { "*", "Nil" },           { "BSR", "Rel" },
    { "LDX", "Imm" },         { "*", "Nil" },

    { "SUB", "Dir" },         { "CMP", "Dir" },
    { "SBC", "Dir" },         { "CPX", "Dir" },
    { "AND", "Dir" },         { "BIT", "Dir" },
    { "LDA", "Dir" },         { "STA", "Dir" },
    { "EOR", "Dir" },         { "ADC", "Dir" },
    { "ORA", "Dir" },         { "ADD", "Dir" },
    { "JMP", "Dir" },         { "JSR", "Dir" },
    { "LDX", "Dir" },         { "STX", "Dir" },

    { "SUB", "Ext" },         { "CMP", "Ext" },
    { "SBC", "Ext" },         { "CPX", "Ext" },
    { "AND", "Ext" },         { "BIT", "Ext" },
    { "LDA", "Ext" },         { "STA", "Ext" },
    { "EOR", "Ext" },         { "ADC", "Ext" },
    { "ORA", "Ext" },         { "ADD", "Ext" },
    { "JMP", "Ext" },         { "JSR", "Ext" },
    { "LDX", "Ext" },         { "STX", "Ext" },

    { "SUB", "Ix2" },         { "CMP", "Ix2" },
    { "SBC", "Ix2" },         { "CPX", "Ix2" },
    { "AND", "Ix2" },         { "BIT", "Ix2" },
    { "LDA", "Ix2" },         { "STA", "Ix2" },
    { "EOR", "Ix2" },         { "ADC", "Ix2" },
    { "ORA", "Ix2" },         { "ADD", "Ix2" },
    { "JMP", "Ix2" },         { "JSR", "Ix2" },
    { "LDX", "Ix2" },         { "STX", "Ix2" },

    { "SUB", "Ix1" },         { "CMP", "Ix1" },
    { "SBC", "Ix1" },         { "CPX", "Ix1" },
    { "AND", "Ix1" },         { "BIT", "Ix1" },
    { "LDA", "Ix1" },         { "STA", "Ix1" },
    { "EOR", "Ix1" },         { "ADC", "Ix1" },
    { "ORA", "Ix1" },         { "ADD", "Ix1" },
    { "JMP", "Ix1" },         { "JSR", "Ix1" },
    { "LDX", "Ix1" },         { "STX", "Ix1" },

    { "SUB", "Ix" },          { "CMP", "Ix" },
    { "SBC", "Ix" },          { "CPX", "Ix" },
    { "AND", "Ix" },          { "BIT", "Ix" },
    { "LDA", "Ix" },          { "STA", "Ix" },
    { "EOR", "Ix" },          { "ADC", "Ix" },
    { "ORA", "Ix" },          { "ADD", "Ix" },
    { "JMP", "Ix" },          { "JSR", "Ix" },
    { "LDX", "Ix" },          { "STX", "Ix" }
  };
  
  public static final String[][] absLabels_RC16 = {
    { "XmitIR", "01AF" },
    { "SetupXmitIR", "01B2" },
    { "IRMarkSpaceByPtr", "01B5" },
    { "IRSpaceByReg", "01C1" },
    { "XmitSplitIR", "01C4" },
    { "TestRptReqd", "0189" },
    { "ChkPowerKey", "019D" },
    { "ChkRecordKey", "01A1" },
    { "ChkVolKeys", "01A5" },
    { "ChkPwrRecVol", "018C" },
    { "ChkVolChFFKeys", "0183" },
    { "SetCarrier", "01B8" }
  };


  public static final String[][] absLabels_C9= {
    { "XmitIR", "0183" },
    { "ChkVolKeys", "01BC" },
    { "SetCarrier", "019B" }
  };
  
  public static final String[][] zeroLabels_RC16 = {
    { "DCBUF", "5A", "DCBUF+", "0A" },
    { "PF0", "7B", "PF", "05" },
    { "PD00", "67", "PD", "14" },
    { "DBYTES", "66" },
    { "FLAGS", "57" },
    { "RPT", "80" }
  };

  
  public static final String[][] zeroLabels_C9 = {
    { "DCBUF", "5A", "DCBUF+", "0A" },
    { "PF0", "7B", "PF", "03" },
    { "PD00", "67", "PD", "14" },
    { "DBYTES", "66" },
    { "FLAGS", "57" } };
  
  public static final int[] oscData_C9 = { 2000000, 15, 5 };
  
  public static final int[] oscData_RC16 = { 2000000, 0, 0 };
}

