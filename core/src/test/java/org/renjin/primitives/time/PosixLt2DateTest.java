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
package org.renjin.primitives.time;

import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.renjin.EvalTestCase;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class PosixLt2DateTest extends EvalTestCase {

  @Test
  public void asDateWithFormat() {

    DateTimeZone.setDefault(DateTimeZone.forID("Pacific/Pago_Pago"));

    eval(" dateString <- '2016-06-1'  ");
    
    //eval(" thisDate2 <- as.Date(dateString,'%Y-%m-%d')");

    // The above is equivalent to:
    
    // First convert date string to calendar, WITH a timezone attribute
    eval(" lt <- strptime(dateString, '%Y-%m-%d', tz='GMT') ");
    
    assertThat(eval("lt$sec"), equalTo(c_i(0)));
    assertThat(eval("lt$min"), equalTo(c_i(0)));
    assertThat(eval("lt$hour"), equalTo(c_i(0)));
    assertThat(eval("lt$mday"), equalTo(c_i(1)));
    assertThat(eval("lt$mon"), equalTo(c_i(5)));
    assertThat(eval("lt$year"), equalTo(c_i(116)));
    assertThat(eval("lt$wday"), equalTo(c_i(3)));
    assertThat(eval("lt$yday"), equalTo(c_i(152)));
    assertThat(eval("lt$isdst"), equalTo(c_i(0)));
    assertThat(eval("class(lt)"), equalTo(c("POSIXlt", "POSIXt")));
    assertThat(eval("attr(lt,'tzone')"), equalTo(c("GMT")));
    
    // Now convert from lt to Date class
    eval("d <- as.Date(lt)");
    assertThat(eval("d"), equalTo(c(16953)));

  }
}
