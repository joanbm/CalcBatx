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
 * Funció elevar - Potència.
 */
public class FuncioElevar extends Funcio {
    public FuncioElevar() {
        super("elevar", 2);
    }
    
    public Complex calcular(Complex[] params) throws ExpressioException {
        if (params[1].esComplex())
            throw new ExpressioException("Encara no es pot operar amb nombres complexos amb elevar.");

        if (params[1].esReal() && params[1].re() == 0.5 && params[0].re() < 0) {
            return new Complex(0, Math.sqrt(-params[0].re()));
        }

        double modul = params[0].getModul(), angle = params[0].getAngle(), exp = params[1].re();
        return new Complex(
            Math.pow(modul, exp) * Math.cos(exp * angle),
            Math.pow(modul, exp) * Math.sin(exp * angle)
        );
    }

    @Override public Expressio derivada(Expressio[] params) throws ExpressioException {
        // Derivada fàcil, per fer els càlculs més precisos i poder calcular la tangent quan ln < 0
        if (params[1].esConstant() && params[1].evaluar(null).esReal()) {
            double n = params[1].evaluar(null).re();
            return new ExpressioFuncio("multiplicar", new Expressio[] {
                new ExpressioFuncio("multiplicar", new Expressio[] {
                    new ExpressioNombre(new Complex(n)),
                    new ExpressioFuncio("elevar", new Expressio[] {
                        params[0],
                        new ExpressioNombre(new Complex(n - 1))
                    })
                }),
                params[0].derivada()
            });
        }

        return new ExpressioFuncio("multiplicar", new Expressio[] {
            new ExpressioFuncio("elevar", new Expressio[] {
                params[0], params[1]
            }),
            new ExpressioFuncio("sumar", new Expressio[] {
                new ExpressioFuncio("multiplicar", new Expressio[] {
                    params[1].derivada(),
                    new ExpressioFuncio("ln", new Expressio[] {
                        params[0]
                    })
                }),
                new ExpressioFuncio("multiplicar", new Expressio[] {
                    new ExpressioFuncio("invers", new Expressio[] {
                        params[0]
                    }),
                    new ExpressioFuncio("multiplicar", new Expressio[] {
                        params[0].derivada(), params[1]
                    })
                })
            })
        });
    }
}
