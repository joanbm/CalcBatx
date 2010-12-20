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

import java.math.BigDecimal;

public class ProcessadorPolinomi {
    FuncioUsuari funcio_;
    Polinomi poli;

    public ProcessadorPolinomi(FuncioUsuari funcio) {
        funcio_ = funcio;
    }

    public Polinomi processar() throws ExpressioException {
        poli = new Polinomi();
        processarPolinomi(funcio_.cos);
        return poli;
    }

    private void processarPolinomi(Expressio e) throws ExpressioException {
        if (e instanceof ExpressioFuncio) {
            ExpressioFuncio fe = (ExpressioFuncio)e;
            if (fe.nom.equals("sumar")) {
                processarPolinomi(fe.parametres[0]);
                processarPolinomi(fe.parametres[1]);
                return;
            }
        }

        processarMonomi(e, false);
    }

    private void processarMonomi(Expressio e, boolean negatiu) throws ExpressioException {
        if (e.esConstant()) {
            afegirMonomi(Complex.ZERO, e.evaluar(null), negatiu);
            return;
        } else if (e instanceof ExpressioFuncio) {
            ExpressioFuncio fe = (ExpressioFuncio)e;
            if (fe.nom.equals("negar")) {
                processarMonomi(fe.parametres[0], !negatiu);
                return;
            } else if (fe.nom.equals("multiplicar")) {
                if (!fe.parametres[0].esConstant()) {
                    throw new ExpressioException("No és un polinomi.");
                }

                afegirMonomi(processarGrau(fe.parametres[1]), fe.parametres[0].evaluar(null), negatiu);
                return;
            } else if (fe.nom.equals("elevar")) {
                afegirMonomi(processarGrau(fe), new Complex(1), negatiu);
                return;
            }
        } else if (e instanceof ExpressioParametre) {
            afegirMonomi(Complex.U, Complex.U, negatiu);
            return;
        }

        throw new ExpressioException("No és un polinomi.");
    }

    private Complex processarGrau(Expressio e) throws ExpressioException {
        if (e instanceof ExpressioParametre) {
            return Complex.U;
        }

        ExpressioFuncio fe = (ExpressioFuncio)e;
        if (!fe.nom.equals("elevar")) {
            throw new ExpressioException("No és un polinomi.");
        }

        if (!(fe.parametres[0] instanceof ExpressioParametre)) {
            throw new ExpressioException("No és un polinomi.");
        }

        if (!fe.parametres[1].esConstant())
            throw new ExpressioException("No és un polinomi.");

        return fe.parametres[1].evaluar(null);
    }

    private void afegirMonomi(Complex grau, Complex coef, boolean negatiu) throws ExpressioException {
        if (!grau.esNatural())
            throw new ExpressioException("El grau d'un polinomi ha de ser un nombre natural.");

        if (grau.re() >= 100)
            throw new ExpressioException("El grau del polinomi és extremadament gran, el càlcul tardarà massa.");

        if (!coef.esReal())
            throw new ExpressioException("El coeficient d'un polinomi ha de ser real.");

        if (negatiu)
            coef = new Complex(-coef.re(), -coef.im());

        poli.afegirMonomi((int)grau.re(), new BigDecimal(coef.re()));
    }
}
