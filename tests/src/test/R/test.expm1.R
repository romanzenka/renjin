#
# Renjin : JVM-based interpreter for the R language for the statistical analysis
# Copyright © 2010-2016 BeDataDriven Groep B.V. and contributors
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, a copy is available at
# https://www.gnu.org/licenses/gpl-2.0.txt
#

# Generated by gen-math-unary-tests.R using GNU R version 3.2.0 (2015-04-16)
library(hamcrest)
expm1.foo <- function(...) 41
Math.bar <- function(...) 44
test.expm1.1 <- function() assertThat(expm1(-0.01), identicalTo(-0.00995016625083195, tol = 0.000100))
test.expm1.2 <- function() assertThat(expm1(-0.1), identicalTo(-0.0951625819640404, tol = 0.000100))
test.expm1.3 <- function() assertThat(expm1(-1), identicalTo(-0.632120558828558, tol = 0.000100))
test.expm1.4 <- function() assertThat(expm1(-1.5), identicalTo(-0.77686983985157, tol = 0.000100))
test.expm1.5 <- function() assertThat(expm1(-2), identicalTo(-0.864664716763387, tol = 0.000100))
test.expm1.6 <- function() assertThat(expm1(-2.5), identicalTo(-0.917915001376101, tol = 0.000100))
test.expm1.7 <- function() assertThat(expm1(-4), identicalTo(-0.981684361111266, tol = 0.000100))
test.expm1.8 <- function() assertThat(expm1(-10), identicalTo(-0.999954600070238, tol = 0.000100))
test.expm1.9 <- function() assertThat(expm1(-100), identicalTo(-1))
test.expm1.10 <- function() assertThat(expm1(-0.785398), identicalTo(-0.544061797734871, tol = 0.000100))
test.expm1.11 <- function() assertThat(expm1(-1.5708), identicalTo(-0.792121187232156, tol = 0.000100))
test.expm1.12 <- function() assertThat(expm1(-3.14159), identicalTo(-0.956785967064063, tol = 0.000100))
test.expm1.13 <- function() assertThat(expm1(-6.28319), identicalTo(-0.998132566031845, tol = 0.000100))
test.expm1.14 <- function() assertThat(expm1(0.01), identicalTo(0.0100501670841681, tol = 0.000100))
test.expm1.15 <- function() assertThat(expm1(0.1), identicalTo(0.105170918075648, tol = 0.000100))
test.expm1.16 <- function() assertThat(expm1(1), identicalTo(1.71828182845905, tol = 0.000100))
test.expm1.17 <- function() assertThat(expm1(1.5), identicalTo(3.48168907033806, tol = 0.000100))
test.expm1.18 <- function() assertThat(expm1(2), identicalTo(6.38905609893065, tol = 0.000100))
test.expm1.19 <- function() assertThat(expm1(2.5), identicalTo(11.1824939607035, tol = 0.000100))
test.expm1.20 <- function() assertThat(expm1(4), identicalTo(53.5981500331442, tol = 0.000100))
test.expm1.21 <- function() assertThat(expm1(10), identicalTo(22025.4657948067, tol = 0.000100))
test.expm1.22 <- function() assertThat(expm1(100), identicalTo(2.68811714181614e+43, tol = 0.000100))
test.expm1.23 <- function() assertThat(expm1(0.785398), identicalTo(1.19327969236168, tol = 0.000100))
test.expm1.24 <- function() assertThat(expm1(1.5708), identicalTo(3.81049505086787, tol = 0.000100))
test.expm1.25 <- function() assertThat(expm1(3.14159), identicalTo(22.140631226955, tol = 0.000100))
test.expm1.26 <- function() assertThat(expm1(6.28319), identicalTo(534.494168496834, tol = 0.000100))
test.expm1.27 <- function() assertThat(expm1(NULL), throwsError())
test.expm1.28 <- function() assertThat(expm1(c(0.01, 0.1, 1, 1.5)), identicalTo(c(0.0100501670841681, 0.105170918075648, 1.71828182845905, 3.48168907033806), tol = 0.000100))
test.expm1.29 <- function() assertThat(expm1(integer(0)), identicalTo(numeric(0)))
test.expm1.30 <- function() assertThat(expm1(numeric(0)), identicalTo(numeric(0)))
test.expm1.31 <- function() assertThat(expm1(NaN), identicalTo(NaN))
test.expm1.32 <- function() assertThat(expm1(NA_real_), identicalTo(NA_real_))
test.expm1.33 <- function() assertThat(expm1(Inf), identicalTo(Inf))
test.expm1.34 <- function() assertThat(expm1(-Inf), identicalTo(-1))
test.expm1.35 <- function() assertThat(expm1(c(1L, 4L)), identicalTo(c(1.71828182845905, 53.5981500331442), tol = 0.000100))
test.expm1.36 <- function() assertThat(expm1(structure(1, class = "foo")), identicalTo(41))
test.expm1.37 <- function() assertThat(expm1(structure(1, class = "bar")), identicalTo(44))
test.expm1.38 <- function() assertThat(expm1(structure(list("a"), class = "foo")), identicalTo(41))
test.expm1.39 <- function() assertThat(expm1(structure(list("b"), class = "bar")), identicalTo(44))
test.expm1.40 <- function() assertThat(expm1(structure(c(1, 2, 3), .Names = c("a", "b", "c"))), identicalTo(structure(c(1.71828182845905, 6.38905609893065, 19.0855369231877), .Names = c("a", "b", "c")), tol = 0.000100))
test.expm1.41 <- function() assertThat(expm1(structure(c(1, 2), .Names = c("x", ""))), identicalTo(structure(c(1.71828182845905, 6.38905609893065), .Names = c("x", "")), tol = 0.000100))
test.expm1.42 <- function() assertThat(expm1(structure(1:12, .Dim = 3:4)), identicalTo(structure(c(1.71828182845905, 6.38905609893065, 19.0855369231877, 53.5981500331442, 147.413159102577, 402.428793492735, 1095.63315842846, 2979.95798704173, 8102.08392757538, 22025.4657948067, 59873.1417151978, 162753.791419004), .Dim = 3:4), tol = 0.000100))
test.expm1.43 <- function() assertThat(expm1(structure(0, rando.attr = 4L)), identicalTo(structure(0, rando.attr = 4L)))
test.expm1.44 <- function() assertThat(expm1(structure(0, class = "zinga")), identicalTo(structure(0, class = "zinga")))
