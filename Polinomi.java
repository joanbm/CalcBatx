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
import java.util.*;
import java.math.*;


public class Polinomi {
    private static BigDecimal DOS = new BigDecimal("2");
    private static BigDecimal ERROR = new BigDecimal("0.000001");

    private List<BigDecimal> coeficients = new ArrayList<BigDecimal>();

    public void afegirMonomi(int grau, BigDecimal coef) {
        if (grau >= coeficients.size())
            coeficients.addAll(Collections.nCopies(grau - coeficients.size() + 1, BigDecimal.ZERO));

        coeficients.set(grau, coef.add(coeficients.get(grau)));
    }

    public Polinomi primitiva() {
        Polinomi pri = new Polinomi();
        for (int grau = 0; grau < coeficients.size(); grau++)
            pri.afegirMonomi(grau + 1, coeficients.get(grau).divide(new BigDecimal(grau + 1), 10,RoundingMode.HALF_UP));
        return pri;
    }

    public Polinomi derivada() {
        Polinomi der = new Polinomi();
        for (int grau = 1; grau < coeficients.size(); grau++)
            der.afegirMonomi(grau - 1, coeficients.get(grau).multiply(new BigDecimal(grau)));
        return der;
    }
    
    private Complex[] convertirDouble(List<BigDecimal> llista) {
        Complex[] array = new Complex[llista.size()];
        for (int i = 0; i < llista.size(); i++)
            array[i] = new Complex(llista.get(i).doubleValue());
        return array;
    }

    public Complex[] getCoeficients() {
        return convertirDouble(coeficients);
    }

    public Complex[] trobarArrels() {
        return convertirDouble(trobarArrelsBig());
    }

    public List<BigDecimal> trobarArrelsBig() {
        ArrayList<BigDecimal> arrels = new ArrayList<BigDecimal>();
        
        if (coeficients.size() < 2) {
            return arrels;
        } else if (coeficients.size() == 2) {
            arrels.add(coeficients.get(0).negate().divide(coeficients.get(1), 20, RoundingMode.HALF_UP));
            return arrels;
        }

        ArrayList<BigDecimal> marges = new ArrayList<BigDecimal>();
        marges.add(null);
        marges.addAll(derivada().trobarArrelsBig());
        marges.add(null);

        for (int i = 0; i < marges.size() - 1; i++) {
            BigDecimal arr = biseccionar(marges.get(i), marges.get(i + 1));
            if (arr != null)
                arrels.add(arr);
        }

        return arrels;
    }

    /**
     * Busca una arrel d'un polinomi dins d'uns límits determinats.
     * @param min Limit inferior, o null si no es coneix.
     * @param max Limit superior, o null si no es coneix.
     * @return El valor de l'arrel, o NaN si no existeix una arrel.
     */
    private BigDecimal biseccionar(BigDecimal min, BigDecimal max) {

        /*
        // Per fer: Arreglar fix

        if (min != null)
            min = min.subtract(ERROR);

        if (max != null)
            max = max.add(ERROR);
        */

        // Si el minim i el maxim son infinits, trobar-ne un de finit
        if (min == null && max == null) {
            if (calcular(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
                if (coeficients.get(coeficients.size() - 1).compareTo(BigDecimal.ZERO) > 0)
                    max = BigDecimal.ZERO; // f(0)>0 i f(+inf)>0
                else
                    min = BigDecimal.ZERO; // f(0)>0 i f(+inf)<0
            } else {
                if (coeficients.get(coeficients.size() - 1).compareTo(BigDecimal.ZERO) > 0)
                    min = BigDecimal.ZERO; // f(0)<0 i f(+inf)>0
                else
                    max = BigDecimal.ZERO; // f(0)<0 i f(+inf)<0
            }
        }

        // Si el mínim és infinit, buscar un límit inferior finit
        if (min == null) {
            BigDecimal rmax = calcular(max), dist = BigDecimal.ONE;
            for (int i = 0; calcular(max.subtract(dist)).signum() == rmax.signum(); i++) {
                if (i >= 100)
                    return null;
                dist = dist.multiply(DOS);
            }
            min = max.subtract(dist);
        }

        // Si el máxim és infinit, buscar un límit superior finit
        if (max == null) {
            BigDecimal rmin = calcular(min), dist = BigDecimal.ONE;
            for (int i = 0; calcular(min.add(dist)).signum() == rmin.signum(); i++)
            {
                if (i >= 100)
                    return null;
                dist = dist.multiply(DOS);
            }
            max = min.add(dist);
        }

        // Aproximar l'arrel entre el minim i màxim mitjançant la bisecció
        for (int i = 0; i < 50; i++) {
            // Buscar el punt mig
            BigDecimal mig = (min.add(max)).divide(DOS);
            
            // Calcular f(x) en f(min), f(mig), f(max)
            BigDecimal rmin = calcular(min);
            BigDecimal rmig = calcular(mig);
            BigDecimal rmax = calcular(max);

            if (rmin.signum() == rmig.signum()) // Si f(min) té el mateix signe que f(mig)
                min = mig; // Agafar (mig, max) com a nou interval
            else
                max = mig; // Agafar (min, mig) com a nou interval
        }

        // Assegurar que el resultat obtingut es realment una arrel, dins uns marges d'error
        BigDecimal res = calcular(min);
        if (res.subtract(ERROR).compareTo(BigDecimal.ZERO) > 0 ||
            res.add(ERROR).compareTo(BigDecimal.ZERO) < 0)
            return null;

        return min;
    }

    private static int count = 0;
    /**
     * Calcular el valor d'un polinomi donat el valor de la variable x.
     * @param x El valor de la variable x.
     * @return El valor del polinomi.
     */
    private BigDecimal calcular(BigDecimal x) {
        count++;
        if (x == null) {
            System.out.println(count);
        }
        BigDecimal r = BigDecimal.ZERO;

        for (int grau = 0; grau < coeficients.size(); grau++)
            // r += coeficients[grau] * x ^ grau
            r = r.add(coeficients.get(grau).multiply(x.pow(grau)));

        return r;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int grau = coeficients.size() - 1; grau >= 0; grau--) {
            double coef = coeficients.get(grau).doubleValue();

            if (coef != 0) {
                boolean impCoef = coef != 1 || grau == 0;
                boolean impGrau = grau != 0;

                if (coef > 0)
                    sb.append('+');

                if (impCoef) {
                    sb.append(Nombre.toString(coef));
                }

                if (impGrau) {
                    sb.append('x');
                    if (grau > 1) {
                        sb.append('^').append(Nombre.toString(grau));
                    }
                }
            }
        }
        
        if (sb.length() > 0 && sb.charAt(0) == '+')
            return sb.substring(1);

        return sb.toString();
    }
}
