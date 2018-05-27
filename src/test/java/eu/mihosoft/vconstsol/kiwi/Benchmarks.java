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
package eu.mihosoft.vconstsol.kiwi;

import java.util.HashMap;

/**
 * Created by alex on 27/11/2014.
 */
public class Benchmarks {

    public static void testAddingLotsOfConstraints() throws DuplicateConstraintException, UnsatisfiableConstraintException, NonlinearExpressionException {
        Solver solver = new Solver();

        final HashMap<String, Variable> variables = new HashMap<String, Variable>();

        ConstraintParser.CassowaryVariableResolver variableResolver = new ConstraintParser.CassowaryVariableResolver() {

            @Override
            public Variable resolveVariable(String variableName) {
                Variable variable = null;
                if (variables.containsKey(variableName)) {
                    variable =  variables.get(variableName);
                } else {
                    variable = new Variable(variableName);
                    variables.put(variableName, variable);
                }
                return variable;
            }

            @Override
            public Expression resolveConstant(String name) {
                try {
                    return new Expression(Double.parseDouble(name));
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        };

        solver.addConstraint(ConstraintParser.parseConstraint("variable0 == 100", variableResolver));

        for (int i = 1; i < 3000; i++) {
            String constraintString  = getVariableName(i) + " == 100 + " + getVariableName(i - 1);

            Constraint constraint = ConstraintParser.parseConstraint(constraintString, variableResolver);

            System.gc();
            long timeBefore = System.nanoTime();

            solver.addConstraint(constraint);

            System.out.println(i + "," + ((System.nanoTime() - timeBefore) / 1000) );
        }


    }

    private static String getVariableName(int number) {
        return "getVariable" + number;
    }

    public static void main(String [ ] args) {
        try {
            testAddingLotsOfConstraints();
        } catch (DuplicateConstraintException e) {
            e.printStackTrace();
        } catch (UnsatisfiableConstraintException e) {
            e.printStackTrace();
        } catch (NonlinearExpressionException e) {
            e.printStackTrace();
        }
    }

}
