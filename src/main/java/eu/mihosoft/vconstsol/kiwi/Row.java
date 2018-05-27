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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by alex on 30/01/15.
 */
public class Row {

    private double constant;

    private Map<Symbol, Double> cells = new LinkedHashMap<>();

    public Row() {
        this(0);
    }

    public Row(double constant) {
        this.constant = constant;
    }

    public Row(Row other) {
        this.cells = new LinkedHashMap<>(other.cells);
        this.constant = other.constant;
    }

    public double getConstant() {
        return constant;
    }

    public void setConstant(double constant) {
        this.constant = constant;
    }

    public Map<Symbol, Double> getCells() {
        return cells;
    }

    public void setCells(Map<Symbol, Double> cells) {
        this.cells = cells;
    }

    /**
     * Add a constant value to the row constant.
     *
     * @return The new value of the constant
     */
    double add(double value) {
        return this.constant += value;
    }

    /**
     * Insert a symbol into the row with a given coefficient.
     * <p/>
     * If the symbol already exists in the row, the coefficient will be
     * added to the existing coefficient. If the resulting coefficient
     * is zero, the symbol will be removed from the row
     */
    void insert(Symbol symbol, double coefficient) {
        Double existingCoefficient = cells.get(symbol);

        if (existingCoefficient != null) {
            coefficient += existingCoefficient;
        }

        if (Util.nearZero(coefficient)) {
            cells.remove(symbol);
        } else {
            cells.put(symbol, Double.valueOf(coefficient));
        }
    }

    /**
     * Insert a symbol into the row with a given coefficient.
     * <p/>
     * If the symbol already exists in the row, the coefficient will be
     * added to the existing coefficient. If the resulting coefficient
     * is zero, the symbol will be removed from the row
     */
    void insert(Symbol symbol) {
        insert(symbol, 1.0);
    }

    /**
     * Insert a row into this row with a given coefficient.
     * The constant and the cells of the other row will be multiplied by
     * the coefficient and added to this row. Any cell with a resulting
     * coefficient of zero will be removed from the row.
     *
     * @param other
     * @param coefficient
     */
    void insert(Row other, double coefficient) {
        this.constant += other.constant * coefficient;

        for(Symbol s: other.cells.keySet()){
            double coeff = other.cells.get(s) * coefficient;

            //insert(s, coeff);  this line looks different than the c++

            //changes start here
            Double value = this.cells.get(s);
            if(value == null){
                this.cells.put(s, 0.0);
            }
            double temp = this.cells.get(s) + coeff;
            this.cells.put(s, temp);
            if(Util.nearZero(temp)){
                this.cells.remove(s);
            }
        }
    }

    /**
     * Insert a row into this row with a given coefficient.
     * The constant and the cells of the other row will be multiplied by
     * the coefficient and added to this row. Any cell with a resulting
     * coefficient of zero will be removed from the row.
     *
     * @param other
     */
    void insert(Row other) {
        insert(other, 1.0);
    }

    /**
     * Remove the given symbol from the row.
     */
    void remove(Symbol symbol) {

        cells.remove(symbol);
        // not sure what this does, can the symbol be added more than once?
        /*CellMap::iterator it = m_cells.find( symbol );
        if( it != m_cells.end() )
            m_cells.erase( it );*/
    }

    /**
     * Reverse the sign of the constant and all cells in the row.
     */
    void reverseSign() {
        this.constant = -this.constant;

        Map<Symbol, Double> newCells = new LinkedHashMap<>();
        for(Symbol s: cells.keySet()){
            double value = - cells.get(s);
            newCells.put(s, value);
        }
        this.cells = newCells;
    }

    /**
     * Solve the row for the given symbol.
     * <p/>
     * This method assumes the row is of the form a * x + b * y + c = 0
     * and (assuming solve for x) will modify the row to represent the
     * right hand side of x = -b/a * y - c / a. The target symbol will
     * be removed from the row, and the constant and other cells will
     * be multiplied by the negative inverse of the target coefficient.
     * The given symbol *must* exist in the row.
     *
     * @param symbol
     */
    void solveFor(Symbol symbol) {
        double coeff = -1.0 / cells.get(symbol);
        cells.remove(symbol);
        this.constant *= coeff;

        HashMap<Symbol, Double> newCells = new LinkedHashMap<>();
        for(Symbol s: cells.keySet()){
            double value = cells.get(s) * coeff;
            newCells.put(s, value);
        }
        this.cells = newCells;
    }

    /**
     * Solve the row for the given symbols.
     * <p/>
     * This method assumes the row is of the form x = b * y + c and will
     * solve the row such that y = x / b - c / b. The rhs symbol will be
     * removed from the row, the lhs added, and the result divided by the
     * negative inverse of the rhs coefficient.
     * The lhs symbol *must not* exist in the row, and the rhs symbol
     * must* exist in the row.
     *
     * @param lhs
     * @param rhs
     */
    void solveFor(Symbol lhs, Symbol rhs) {
        insert(lhs, -1.0);
        solveFor(rhs);
    }

    /**
     * Get the coefficient for the given symbol.
     * <p/>
     * If the symbol does not exist in the row, zero will be returned.
     *
     * @return
     */
    double coefficientFor(Symbol symbol) {
        if (this.cells.containsKey(symbol)) {
            return this.cells.get(symbol);
        } else {
            return 0.0;
        }
    }

    /**
     * Substitute a symbol with the data from another row.
     * <p/>
     * Given a row of the form a * x + b and a substitution of the
     * form x = 3 * y + c the row will be updated to reflect the
     * expression 3 * a * y + a * c + b.
     * If the symbol does not exist in the row, this is a no-op.
     */
    void substitute(Symbol symbol, Row row) {
        if (cells.containsKey(symbol)) {
            double coefficient = cells.get(symbol);
            cells.remove(symbol);
            insert(row, coefficient);
        }
    }

}
