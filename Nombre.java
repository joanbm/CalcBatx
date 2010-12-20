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

import java.text.DecimalFormat;

/**
 * Utilitats generals per manipular nombres.
 */
public class Nombre {
    /**
     * Comprova si un nombre es pot considerar igual a un altre dins del marge d'error.
     * @param x Primer nombre a comparar.
     * @param y Segon nombre a comparar.
     * @return true si es poden considerar iguals.
     */
    public static boolean practicamentIgual(double x, double y) {
        return Math.abs(x - y) < 0.00000001;
    }

    /**
     * Comprova si el nombre és finit.
     * @param x El nombre.
     * @return true si és finit.
     */
    public static boolean esFinit(double x) {
        return !Double.isInfinite(x) && !Double.isNaN(x);
    }

    /**
     * Comprova si un nombre té un valor enter.
     * @param x El nombre.
     * @return true si és enter.
     */
    public static boolean esEnter(double x) {
        if (x < Long.MIN_VALUE || x > Long.MAX_VALUE)
            return false;

        return practicamentIgual(x, Math.round(x));
    }

    /**
     * Genera una representació textual d'un nombre.
     * @param x El nombre.
     * @return Un text que representa el valor.
     */
    public static String toString(double x) {
        if (Double.isNaN(x))
            return "\u2204"; // Caràcter "No existeix"
        else if (x == Double.POSITIVE_INFINITY)
            return "+\u221E"; // Caràcter "Infinit"
        else if (x == Double.NEGATIVE_INFINITY)
            return "-\u221E"; // Caràcter "Infinit"
        else if ((esEnter(x) && (x < -0.1 || x > 0.1)) || x == 0) {
            return Long.toString(Math.round(x)).replace("E", "*10^");
        } else {
            return Double.toString(x).replace("E", "*10^");
        }
    }
}
