/*
 * R : A Computer Language for Statistical Data Analysis
 * Copyright (C) 1995, 1996  Robert Gentleman and Ross Ihaka
 * Copyright (C) 1997--2008  The R Development Core Team
 * Copyright (C) 2003, 2004  The R Foundation
 * Copyright (C) 2010 bedatadriven
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package r.compiler.ir.tree.builders;

import r.compiler.ReservedWords;
import r.compiler.ir.tree.*;
import r.lang.SEXP;
import r.lang.Symbol;

public class DefaultCallBuilder implements ReservedWords.IRBuilder {
  @Override
  public Statement buildStm(IRFactory factory, r.lang.FunctionCall call) {
    return new ExpStm( buildExp(factory, call));
  }

  @Override
  public Exp buildExp(IRFactory factory, r.lang.FunctionCall call) {
    ExpList expList = new ExpList();
    for(SEXP node : call.getArguments().values()) {
      expList.add( factory.buildExp( node) );
    }
    if(call.getFunction() instanceof Symbol) {
      return new CallExp(new NameExp((Symbol) call.getFunction()), expList);
    } else {
      throw new UnsupportedOperationException("Cannot build IR for function call with function of type " +
          call.getFunction().getClass().getName());
    }
  }
}
