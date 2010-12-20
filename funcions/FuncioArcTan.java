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

package calcbatx.funcions;
import calcbatx.*;

/**
 * Funció arctan - Arc tangent.
 */
public class FuncioArcTan extends Funcio {
    public FuncioArcTan() {
        super("arctan", 1);
    }
    
    public Complex calcular(Complex[] params) throws ExpressioException {
        if (params[0].esComplex())
            throw new ExpressioException("Encara no es pot operar amb nombres complexos amb arctan.");

        return new Complex(
            Math.atan(params[0].re()),
            0.0
        );
    }

    @Override public Expressio derivada(Expressio[] params) throws ExpressioException {
        return new ExpressioFuncio("multiplicar", new Expressio[] {
            new ExpressioFuncio("invers", new Expressio[] {
                new ExpressioFuncio("sumar", new Expressio[] {
                    new ExpressioFuncio("elevar", new Expressio[] {
                        params[0], new ExpressioNombre(new Complex(2))
                    }),
                    new ExpressioNombre(Complex.U)
                }),
            }),
            params[0].derivada()
        });
    }
}
