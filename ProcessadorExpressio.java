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

import java.util.Arrays;
import java.util.ArrayList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ProcessadorExpressio {
    private String txt;
    private int pos;

    private String nomFuncio;
    private ArrayList<String> paramsFuncio;

    /**
     * Expressió regular que permet identificar un nombre.
     */
    private Pattern regexpNombre = Pattern.compile("\\d+(\\.\\d+)?([eE][+-]?\\d+)?");

    /**
     * Expressió regular que permet identificar un nom (de funció, paràmetre, etc.)
     */
    private Pattern regexpNom = Pattern.compile("\\w+");

    /**
     * Create un processador de funcions matemàtiques.
     * @param txt La representació textual de la funció a processar.
     */
    public ProcessadorExpressio(String txt) {
        this.txt = txt;
        this.pos = 0;
        this.nomFuncio = "";
        this.paramsFuncio = new ArrayList<String>();
    }

    /**
     * Processar una definició de funció.
     * @return La funció.
     * @throws ProcessadorExpressioException
     */
    public FuncioUsuari processar() throws ProcessadorExpressioException {
        processarDefinicio();
        Expressio funcio = processarFormula(new char[] {});
        return new FuncioUsuari(nomFuncio, paramsFuncio.size(), funcio, txt);
    }

    /**
     * Processar el pròleg d'una funció.
     * El "pròleg" és la part en la que es defineix el nom i paràmetres de la funció.
     * @throws ProcessadorExpressioException
     */
    private void processarDefinicio() throws ProcessadorExpressioException {
        saltarEspais();

        // Llegir el nom de la funció
        Matcher esNomFunc = regexpNom.matcher(txt).region(pos, txt.length());
        if (!esNomFunc.lookingAt())
            throw new ProcessadorExpressioException("El nom de la funcio és invàlid.");

        nomFuncio = esNomFunc.group();
        pos = esNomFunc.end();

        // Llegir la llista de paràmetres a la funció, si en té
        saltarEspais();

        if (txt.startsWith("(", pos)) {
            pos++;
            
            do {
                // Llegir el nom del paràmetre
                saltarEspais();

                Matcher esParam = regexpNom.matcher(txt).region(pos, txt.length());
                if (!esParam.lookingAt())
                    throw new ProcessadorExpressioException("Cada parametre de la funció ha de ser un nom.");
                paramsFuncio.add(esParam.group());
                pos = esParam.end();

                saltarEspais();

                if (pos >= txt.length() || txt.charAt(pos) != ',' && txt.charAt(pos) != ')')
                    throw new ProcessadorExpressioException("Separador invàlid pels paràmetres de la funció.");
            } while (txt.charAt(pos++) == ',');
        }

        // Assegurar que acabi amb un signe d'igual
        saltarEspais();

        if (!txt.startsWith("=", pos))
            throw new ProcessadorExpressioException("S'esperava un igual després de la definició.");
        pos++;
    }

    /**
     * Processar una fòrmula.
     * Una fòrmula és un conjunt de components, units entre si amb operadors.
     * @param fi Caràcters que marquen el fi de la formula.
     * @return La fòrmula, en una expressió matemàtica.
     * @throws ProcessadorExpressioException
     */
    private Expressio processarFormula(char[] fi) throws ProcessadorExpressioException {
        Arrays.sort(fi); // Es necessita per poder utilitzar binarySearch

        // Llegir cada un dels components i operadors de la fòrmula
        ArrayList<Expressio> comps = new ArrayList<Expressio>();
        ArrayList<Character> ops = new ArrayList<Character>();

        comps.add(processarComp());
        saltarEspais();

        while (pos < txt.length() && Arrays.binarySearch(fi, txt.charAt(pos)) < 0) {

            ops.add(processarOp());
            comps.add(processarComp());
            saltarEspais();
        }

        processarOpBinari('^', "elevar", comps, ops);
        substituirOpBinari('/', '*', "invers", comps, ops);
        processarOpBinari('*', "multiplicar", comps, ops);
        substituirOpBinari('-', '+', "negar", comps, ops);
        processarOpBinari('+', "sumar", comps, ops);

        return comps.get(0);
    }

    private void processarOpBinari(char op, String funcio, ArrayList<Expressio> comps, ArrayList<Character> ops) {
        for (int i = ops.size() - 1; i >= 0; i--) {
            if (ops.get(i) == op) {
                comps.set(i, new ExpressioFuncio(
                        funcio,
                        new Expressio[] { comps.get(i), comps.get(i + 1) }
                ));
                ops.remove(i);
                comps.remove(i + 1);
            }
        }
    }

    private void substituirOpBinari(char op, char substitut, String funcio, ArrayList<Expressio> comps, ArrayList<Character> ops) {
        for (int i = 0; i < ops.size(); i++) {
            if (ops.get(i) == op) {
                ops.set(i, substitut);
                comps.set(i + 1, new ExpressioFuncio(
                        funcio,
                        new Expressio[] { comps.get(i + 1) }
                ));
            }
        }
    }

    /**
     * Processar un component d'una fòrmula.
     * Un component és cada una de les parts entre els operadors d'una fòrmula.
     * @return El component, en una expressió matemàtica.
     * @throws ProcessadorExpressioException
     */
    private Expressio processarComp() throws ProcessadorExpressioException {
        saltarEspais();

        // Buida (error)
        if (pos == txt.length())
            throw new ProcessadorExpressioException("És necessari introduir una expressió.");

        // Signes + o -
        if (txt.charAt(pos) == '+' || txt.charAt(pos) == '-')
        {
            boolean negar = txt.charAt(pos++) == '-';
            Expressio expr = processarComp();
            if (negar)
                expr = new ExpressioFuncio("negar", new Expressio[] { expr });
            return expr;
        }
        // Fòrmula entre parentesi
        if (txt.startsWith("(", pos) || txt.startsWith("[", pos))
        {
            pos++;
            Expressio formula = processarFormula(new char[] {')', ']'});
            pos++;
            return formula;
        }

        // Nombre
        Matcher esNombre = regexpNombre.matcher(txt).region(pos, txt.length());
        if (esNombre.lookingAt()) {
            String num = esNombre.group();
            pos = esNombre.end();
            return new ExpressioNombre(new Complex(Double.parseDouble(num)));
        }

        // Funció, constant o paràmetre
        Matcher esNomFunc = regexpNom.matcher(txt).region(pos, txt.length());
        if (esNomFunc.lookingAt()) {
            String nom = esNomFunc.group();
            pos = esNomFunc.end();

            // Si és el nom d'un paràmetre d'aquesta funció, agafar-lo
            if (paramsFuncio.contains(nom))
                return new ExpressioParametre(paramsFuncio.indexOf(nom));

            // Llegir els paràmetres de la funció, si en té
            saltarEspais();

            ArrayList<Expressio> param = new ArrayList<Expressio>();
            if (txt.startsWith("(", pos) || txt.startsWith("[", pos)) {
                pos++;

                do {
                    param.add(processarFormula(new char[] { ',', ')', ']'}));
                } while (pos < txt.length() && txt.charAt(pos++) == ',');
            }

            Expressio[] paramArray = new Expressio[param.size()];
            return new ExpressioFuncio(nom, param.toArray(paramArray));
        }
        
        throw new ProcessadorExpressioException("Tipus d'expressió invàlid.");
    }

    /**
     * Processar un operador binari.
     * @return El caràcter de l'operador.
     * @throws ProcessadorExpressioException
     */
    private char processarOp() throws ProcessadorExpressioException {
        char op = txt.charAt(pos);
        if (op == '+' || op == '-' || op == '*' || op == '/' || op == '^') {
            pos++;
            return op;
        }

        return '*';
        //throw new ProcessadorExpressioException("Operador desconegut.");
    }

    /**
     * Avançar la posició fins a trobar un caràcter que no sigui un espai.
     */
    private void saltarEspais() {
        while (pos < txt.length() && Character.isWhitespace(txt.charAt(pos)))
            pos++;
    }
}
