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

import java.util.*;

/**
 * Created by alex on 30/01/15.
 */
public class Constraint {

    private Expression expression;
    private double strength;
    private RelationalOperator op;

    public Constraint(){
    }

    public Constraint(Expression expr, RelationalOperator op) {
        this(expr, op, Strength.REQUIRED);
    }

    public Constraint(Expression expr, RelationalOperator op, double strength) {
        this.expression = reduce(expr);
        this.op = op;
        this.strength = Strength.clip(strength);
    }

    public Constraint(Constraint other, double strength) {
        this(other.expression, other.op, strength);
    }

    private static Expression reduce(Expression expr){

        Map<Variable, Double> vars = new LinkedHashMap<>();
        for(Term term: expr.getTerms()){
            Double value = vars.get(term.getVariable());
            if(value == null){
                value = 0.0;
            }
            value += term.coefficient;
            vars.put(term.getVariable(), value);
        }

        List<Term> reducedTerms = new ArrayList<>();
        for(Variable variable: vars.keySet()){
            reducedTerms.add(new Term(variable, vars.get(variable)));
        }

        return new Expression(reducedTerms, expr.getConstant());
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public double getStrength() {
        return strength;
    }

    public Constraint setStrength(double strength) {
        this.strength = strength;
        return this;
    }

    public RelationalOperator getOp() {
        return op;
    }

    public void setOp(RelationalOperator op) {
        this.op = op;
    }

    @Override
    public String toString() {
        return "expression: (" + expression + ") strength: " + strength + " operator: " + op;
    }

}
