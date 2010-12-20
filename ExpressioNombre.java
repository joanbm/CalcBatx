/*
 * Copyright (C) Joan Bruguera 2010
 *
 * This file is part of CalcBatx.
 *
 * CalcBatx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CalcBatx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CalcBatx.  If not, see <http://www.gnu.org/licenses/>.
 */

package calcbatx;

public class ExpressioNombre extends Expressio {
    /**
     * Valor que es retornarà.
     */
    private Complex valor;
    
    public ExpressioNombre(Complex valor) {
        this.valor = valor;
    }
    
    public Complex evaluar(Complex[] params) {
        return valor;
    }

    public boolean esConstant() {
        return true;
    }
    
    @Override public String toString() {
        return valor.toString();
    }

    @Override public Expressio derivada() {
        return new ExpressioNombre(Complex.ZERO);
    }

    public Expressio substituirParametres(Expressio[] params) {
        return new ExpressioNombre(valor);
    }
}
