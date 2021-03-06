/**
 * Renjin : JVM-based interpreter for the R language for the statistical analysis
 * Copyright © 2010-2016 BeDataDriven Groep B.V. and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, a copy is available at
 * https://www.gnu.org/licenses/gpl-2.0.txt
 */
package org.renjin.gcc.codegen.type.record.unit;

import org.renjin.gcc.codegen.MethodGenerator;
import org.renjin.gcc.codegen.expr.*;
import org.renjin.gcc.codegen.fatptr.FatPtr;
import org.renjin.gcc.codegen.type.record.RecordValue;
import org.renjin.repackaged.asm.Label;
import org.renjin.repackaged.asm.Type;

public class RecordUnitPtr implements RefPtrExpr {
  
  private JExpr ref;
  private FatPtr address;

  public RecordUnitPtr(JExpr ref) {
    this.ref = ref;
  }

  public RecordUnitPtr(JExpr ref, FatPtr address) {
    this.ref = ref;
    this.address = address;
  }

  public Type getJvmType() {
    return ref.getType();
  }
  
  @Override
  public void store(MethodGenerator mv, GExpr rhs) {
    ((JLValue) ref).store(mv, ((RecordUnitPtr) rhs).ref);
  }

  @Override
  public GExpr addressOf() {
    if(address == null) {
      throw new NotAddressableException();
    }
    return address;
  }

  public JExpr unwrap() {
    return ref;
  }

  @Override
  public void jumpIfNull(MethodGenerator mv, Label label) {
    ref.load(mv);
    mv.ifnull(label);
  }

  @Override
  public GExpr valueOf() {
    return new RecordValue(ref);
  }
}
