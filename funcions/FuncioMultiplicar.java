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
 * Funció multiplicar - Multiplicació.
 */
public class FuncioMultiplicar extends Funcio {
    public FuncioMultiplicar() {
        super("multiplicar", 2);
    }

    public Complex calcular(Complex[] params) {
        if (params[0].esReal())
            return new Complex(params[0].re() * params[1].re(), params[0].re() * params[1].im());
        
        return new Complex(
            params[0].re() * params[1].re() - params[0].im() * params[1].im(),
            params[0].re() * params[1].im() + params[0].im() * params[1].re()
        );
    }

    @Override public Expressio derivada(Expressio[] params) throws ExpressioException {
        return new ExpressioFuncio("sumar", new Expressio[] {
            new ExpressioFuncio("multiplicar", new Expressio[] {
                params[0].derivada(), params[1]
            }),
            new ExpressioFuncio("multiplicar", new Expressio[] {
                params[0], params[1].derivada()
            })
        });
    }
}
