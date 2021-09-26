/*
 * Logisim-evolution - digital logic design tool and simulator
 * Copyright by the Logisim-evolution developers
 *
 * https://github.com/logisim-evolution/
 *
 * This is free software released under GNU GPLv3 license
 */

package com.cburch.logisim.std.ttl;

import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.fpga.designrulecheck.Netlist;
import com.cburch.logisim.fpga.hdlgenerator.AbstractHDLGeneratorFactory;
import com.cburch.logisim.fpga.hdlgenerator.HDL;
import com.cburch.logisim.fpga.hdlgenerator.HDLPorts;
import com.cburch.logisim.instance.Port;
import com.cburch.logisim.util.LineBuffer;
import java.util.ArrayList;

public class Ttl74175HDLGenerator extends AbstractHDLGeneratorFactory {

  public Ttl74175HDLGenerator() {
    super();
    myWires
        .addWire("CurState", 4)
        .addWire("NextState", 4);
    myPorts
        .add(Port.CLOCK, HDLPorts.CLOCK, 1, 7)
        .add(Port.INPUT, "nCLR", 1, 0)
        .add(Port.INPUT, "D1", 1, 3)
        .add(Port.INPUT, "D2", 1, 4)
        .add(Port.INPUT, "D3", 1, 10)
        .add(Port.INPUT, "D4", 1, 11)
        .add(Port.OUTPUT, "nQ1", 1, 2)
        .add(Port.OUTPUT, "Q1", 1, 1)
        .add(Port.OUTPUT, "nQ2", 1, 5)
        .add(Port.OUTPUT, "Q2", 1, 6)
        .add(Port.OUTPUT, "nQ3", 1, 9)
        .add(Port.OUTPUT, "Q3", 1, 8)
        .add(Port.OUTPUT, "nQ4", 1, 12)
        .add(Port.OUTPUT, "Q4", 1, 13);
  }

  @Override
  public ArrayList<String> GetModuleFunctionality(Netlist TheNetlist, AttributeSet attrs) {
    return LineBuffer.getBuffer()
        .pair("CLK", HDLPorts.CLOCK)
        .pair("tick", HDLPorts.TICK)
        .add("""
            NextState <= CurState WHEN {{tick}} = '0' ELSE
                         D4&D3&D2&D1;
            
            dffs : PROCESS( {{CLK}} , nCLR ) IS
               BEGIN
                  IF (nCLR = '0') THEN CurState <= "0000";
                  ELSIF (rising_edge({{CLK}})) THEN
                     CurState <= NextState;
                  END IF;
               END PROCESS dffs;
            
            nQ1 <= NOT(CurState(0));
            Q1  <= CurState(0);
            nQ2 <= NOT(CurState(1));
            Q2  <= CurState(1);
            nQ3 <= NOT(CurState(2));
            Q3  <= CurState(2);
            nQ4 <= NOT(CurState(3));
            Q4  <= CurState(3);")
            """)
        .getWithIndent();
  }

  @Override
  public boolean isHDLSupportedTarget(AttributeSet attrs) {
    /* TODO: Add support for the ones with VCC and Ground Pin */
    if (attrs == null) return false;
    return (!attrs.getValue(TtlLibrary.VCC_GND) && HDL.isVHDL());
  }
}
