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

public class Integral {
    public static Complex calcular(Funcio f, double extrem1, double extrem2, int parts) {
        try {
            double h = (extrem2 - extrem1) / parts;

            // Calcular els valors d'y per cada punt on s'ha de fer un trapezi
            Complex[] valors = new Complex[parts + 1];
            for (int i = 0; i < valors.length; i++)
                valors[i] = f.calcular(new Complex[] { new Complex(extrem1 + h * i) });

            // Sumar les arees dels trapezis
            Complex r = Complex.ZERO;
            for (int i = 0; i < parts; i++)
                r = new Complex(
                    r.re() + areaTrapezi(h, valors[i].re(), valors[i + 1].re()),
                    r.im() + areaTrapezi(h, valors[i].im(), valors[i + 1].im())
                );
            
            return r;
        } catch (ExpressioException exception) {
            // Ha fallat el càlcul en algun dels punts
            return Complex.NaN;
        }
    }

    /**
     * Calcula l'àrea d'un trapezi rectangle de base h, i altures y1 i y2.
     * @param h Base
     * @param y1 Primera altura
     * @param y2 Segona altura
     * @return L'àrea del trapezi.
     */
    private static double areaTrapezi(double h, double y1, double y2) {
        return (h / 2) * (y1 + y2);
    }
}
