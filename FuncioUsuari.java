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

public class FuncioUsuari extends Funcio {
    public Expressio cos;

    public String text;

    public FuncioUsuari(String nom, int numParams, Expressio cos, String text) {
        super(nom, numParams);
        this.cos = cos;
        this.text = text;
    }

    public Complex calcular(Complex[] params) throws ExpressioException {
        /* És possible que l'usuari defineixi una funció que causi un bucle infinit.
         * Per exemple: x=x
         * Normalment, es produeix un desbordament de pila i es tanca el programa.
         * Aquest no és el comportament desitjat, la capturem i retornem un valor indefinit.
         */
        try {
            return cos.evaluar(params);
        } catch (StackOverflowError error) {
            return Complex.NaN;
        }
    }

    @Override public Expressio derivada(Expressio[] params) throws ExpressioException {
        return cos.substituirParametres(params).derivada();
    }
}
