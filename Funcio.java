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

import java.util.Map;
import java.util.LinkedHashMap;
import calcbatx.funcions.*;

/**
 * Definició d'una funció matemàtica.
 */
public abstract class Funcio {
    /**
     * Llista de funcions definides.
     */
    public static final Map<String, Funcio> llista = new LinkedHashMap<String, Funcio>();

    /**
     * Afegir una funció a la llista de funcions.
     * @param f La funció a afegir.
     */
    public static void afegir(Funcio f) {
        llista.put(f.nom, f);
    }

    /**
     * Afegir una funció a la llista de funcions.
     * @param s El codi de la funció a afegir.
     */
    public static void afegir(String s) {
        try {
            afegir(new ProcessadorExpressio(s).processar());
        } catch (ProcessadorExpressioException exception) {
            System.out.println("Error carregant " + s);
        }
    }

    /**
     * Constructor estàtic. Defineix les funcions del sistema.
     */
    static {
        // Bàsiques
        afegir(new FuncioSumar());
        afegir(new FuncioNegar());

        afegir(new FuncioMultiplicar());
        afegir(new FuncioInvers());

        afegir(new FuncioElevar());
        afegir("arrel(x,n)=x^invers(n)");
        afegir("arrelq(x)=x^0.5");

        afegir(new FuncioLn());
        afegir("log(x,n)=ln(x)/ln(n)");
        afegir("log10(x)=ln(x)/ln(10)");

        afegir(new FuncioDerivada());

        afegir("abs(x)=arrelq(x^2)");
        afegir("sgn(x)=x/abs(x)");

        // Trigonometria
        afegir(new FuncioSin());
        afegir(new FuncioCos());
        afegir("tan(x)=sin(x)/cos(x)");
        afegir("tg(x)=tan(x)"); // Alias

        afegir(new FuncioArcSin());
        afegir(new FuncioArcCos());
        afegir(new FuncioArcTan());
        afegir("arctg(x)=arctan(x)"); // Alias

        // Constants comunes
        afegir("i=-1^0.5");
        afegir("pi=" + Double.toString(Math.PI));
        afegir("e=" + Double.toString(Math.E));
    }

    /**
     * El nom de la funció.
     */
    public String nom;

    /**
     * El nombre de paràmetres que rep la funció.
     */
    public int numParams;

    /**
     * Definir una nova funció matemàtica.
     * @param nom El nom de la funció.
     * @param numParams El nombre de paràmetres que rep la funció.
     * @param cos El cos (expressió) de la funció.
     */
    public Funcio(String nom, int numParams) {
        this.nom = nom;
        this.numParams = numParams;
    }

    /**
     * Comprova si el nombre de paràmetres per a la funció és correcte.
     * @param n El nombre de paràmetres amb el que es vol evaluar.
     * @return true si és correcte, false si no.
     */
    public boolean numParamsCorrecte(int n) {
        return n == numParams;
    }

    public Expressio evaluar(Expressio[] parametres, Complex[] params) throws ExpressioException {
        Complex[] p = new Complex[parametres.length];
        for (int i = 0; i < parametres.length; i++)
            p[i] = parametres[i].evaluar(params);

        return new ExpressioNombre(calcular(p));
    }

    /**
     * Evalua la funció i calcula el valor numèric resultant.
     * @param params Els paràmetres sobre els quals s'executa la funció.
     * @return El resultat obtingut d'evaluar l'expressió.
     * @throws ExpressioException
     */
    public abstract Complex calcular(Complex[] params) throws ExpressioException;

    /**
     * Troba la funció derivada d'aquesta funció.
     * @return La funció derivada d'aquesta funció.
     */
    public abstract Expressio derivada(Expressio[] params) throws ExpressioException;

    @Override public String toString() {
        return nom;
    }
}
