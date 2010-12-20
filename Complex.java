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

/**
 * Un nombre complex.
 */
public class Complex {
    /**
     * Part real.
     */
    private double re;

    /**
     * Part imaginària.
     */
    private double im;
    
    /**
     * Obtè la part real.
     * @return La part real.
     */
    public double re() {
        return re;
    }

    /**
     * Obtè la part imaginària.
     * @return La part imaginària.
     */
    public double im() {
        return im;
    }

    /**
     * Nombre 0.
     */
    public static final Complex ZERO = new Complex(0.0, 0.0);

    /**
     * Nombre 1 (real).
     */
    public static final Complex U = new Complex(1.0, 0.0);

    /**
     * Constant imaginària.
     */
    public static final Complex I = new Complex(0.0, 1.0);

    /**
     * Valor NaN (No és un nombre).
     */
    public static final Complex NaN = new Complex(Double.NaN, 0);

    /**
     * Crear un nombre real.
     * @param re Part real.
     */
    public Complex(double re) {
        this(re, 0.0);
    }

    /**
     * Crear un nombre complex.
     * @param re Part real.
     * @param im Part imaginària.
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Obtè el mòdul del nombre complex.
     * @return El mòdul del nombre complex.
     */
    public double getModul() {
        return Math.hypot(re, im);
    }

    /**
     * Obtè l'angle del nombre complex.
     * @return L'angle del nombre complex.
     */
    public double getAngle() {
        return Math.atan2(im, re);
    }

    /**
     * Crea un nombre complex a partir de la forma polar (mòdul i angle).
     * @param modul El mòdul del nombre complex.
     * @param angle L'angle del nombre complex.
     * @return El nombre complex.
     */
    public static Complex crearPolar(double modul, double angle) {
        return new Complex(modul * Math.cos(angle), modul * Math.sin(angle));
    }

    /**
     * Comprova si és un nombre real.
     * @return true si és un nombre real.
     */
    public boolean esReal() {
        // Permetre una petita part imaginària que pot ser fruit d'un error
        return Nombre.practicamentIgual(im, 0) && Nombre.esFinit(re);
    }

    /**
     * Comprova si és un nombre real.
     * @return true si és un nombre real.
     */
    public boolean esComplex() {
        // Permetre una petita part imaginària que pot ser fruit d'un error
        return !Nombre.practicamentIgual(im, 0);
    }

    /**
     * Comprova si és un nombre enter.
     * @return true si és un nombre enter.
     */
    public boolean esEnter() {
        return esReal() && Nombre.esEnter(re);
    }

    /**
     * Comprova si és un nombre natural. El nombre 0 es considera un nombre natural.
     * @return true si és un nombre natural.
     */
    public boolean esNatural() {
        return esEnter() && re >= 0.0;
    }

    /**
     * Genera una representació textual del nombre complex.
     * @return Un text que representa el valor.
     */
    @Override public String toString() {
        if (Double.isNaN(re) || Double.isNaN(im))
            return Nombre.toString(Double.NaN);
        
        StringBuilder sb = new StringBuilder();
        boolean representarRe = !Nombre.practicamentIgual(re, 0) || Nombre.practicamentIgual(im, 0);
        boolean representarIm = !Nombre.practicamentIgual(im, 0);

        if (representarRe) {
            sb.append(Nombre.toString(re));

            // És necessari el signe entre 2 termes
            if (representarIm && im > 0)
                sb.append('+');
        }

        if (representarIm) {            
            if (!Nombre.practicamentIgual(Math.abs(im), 1))
                sb.append(Nombre.toString(im));

            sb.append(im > 0 ? "i" : "-i");
        }

        return sb.toString();
    }

    @Override public boolean equals(Object o) {
        if (o instanceof Complex) {
            Complex c = (Complex)o;
            return re == c.re && im == c.im;
        }

        return false;
    }
}
