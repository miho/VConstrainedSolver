/*
 * Copyright (c) 2015, Alex Birkett All rights reserved.
 * Copyright (c) 2018-2018 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of kiwi-java nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.mihosoft.vconstsol.kiwi.inequality;

import org.junit.Test;

import eu.mihosoft.vconstsol.kiwi.DuplicateConstraintException;
import eu.mihosoft.vconstsol.kiwi.Solver;
import eu.mihosoft.vconstsol.kiwi.Symbolics;
import eu.mihosoft.vconstsol.kiwi.UnsatisfiableConstraintException;
import eu.mihosoft.vconstsol.kiwi.Variable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by alex on 31/01/16.
 */
public class VariableConstantTest {
    private static double EPSILON = 1.0e-8;

    @Test
    public void lessThanEqualTo() throws DuplicateConstraintException, UnsatisfiableConstraintException {
        Variable x = new Variable("x");
        Solver solver = new Solver();
        solver.addConstraint(Symbolics.lessThanOrEqualTo(x, 100));
        solver.updateVariables();
        assertTrue(x.getValue() <= 100);
        solver.addConstraint(Symbolics.equals(x, 90));
        solver.updateVariables();
        assertEquals(x.getValue(), 90, EPSILON);
    }

    @Test(expected = UnsatisfiableConstraintException.class)
    public void lessThanEqualToUnsatisfiable() throws DuplicateConstraintException, UnsatisfiableConstraintException {
        Variable x = new Variable("x");
        Solver solver = new Solver();
        solver.addConstraint(Symbolics.lessThanOrEqualTo(x, 100));
        solver.updateVariables();
        assertTrue(x.getValue() <= 100);
        solver.addConstraint(Symbolics.equals(x, 110));
        solver.updateVariables();
    }

    @Test
    public void greaterThanEqualTo() throws DuplicateConstraintException, UnsatisfiableConstraintException {
        Variable x = new Variable("x");
        Solver solver = new Solver();
        solver.addConstraint(Symbolics.greaterThanOrEqualTo(x, 100));
        solver.updateVariables();
        assertTrue(x.getValue() >= 100);
        solver.addConstraint(Symbolics.equals(x, 110));
        solver.updateVariables();
        assertEquals(x.getValue(), 110, EPSILON);
    }

    @Test(expected = UnsatisfiableConstraintException.class)
    public void greaterThanEqualToUnsatisfiable() throws DuplicateConstraintException, UnsatisfiableConstraintException {
        Variable x = new Variable("x");
        Solver solver = new Solver();
        solver.addConstraint(Symbolics.greaterThanOrEqualTo(x, 100));
        solver.updateVariables();
        assertTrue(x.getValue() >= 100);
        solver.addConstraint(Symbolics.equals(x, 90));
        solver.updateVariables();
    }
}
