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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 30/01/15.
 */
public class Expression {

    private List<Term> terms;

    private double constant;

    public Expression() {
        this(0);
    }

    public Expression(double constant) {
        this.constant = constant;
        this.terms = new ArrayList<Term>();
    }

    public Expression(Term term, double constant) {
        this.terms = new ArrayList<Term>();
        terms.add(term);
        this.constant = constant;
    }

    public Expression(Term term) {
        this (term, 0.0);
    }

    public Expression(List<Term> terms, double constant) {
        this.terms = terms;
        this.constant = constant;
    }

    public Expression(List<Term> terms) {
        this(terms, 0);
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public double getValue() {
        double result = this.constant;

        for (Term term : terms) {
            result += term.getValue();
        }
        return result;
    }

    public final boolean isConstant() {
        return terms.size() == 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("isConstant: " + isConstant() + " constant: " + constant);
        if (!isConstant()) {
            sb.append(" terms: [");
            for (Term term: terms) {
                sb.append("(");
                sb.append(term);
                sb.append(")");
            }
            sb.append("] ");
        }
        return sb.toString();
    }

}

