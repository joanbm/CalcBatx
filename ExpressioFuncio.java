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

public class ExpressioFuncio extends Expressio {
    /**
     * Nom de la funció a executar.
     */
    public String nom;

    /**
     * Paràmetres per a la funció a executar.
     */
    public Expressio[] parametres;
    
    public ExpressioFuncio(String nom, Expressio[] parametres)
    {
        this.nom = nom;
        this.parametres = parametres;
    }

    private Funcio buscarFuncio(String nom, int numParams) throws ExpressioException {
        if (!Funcio.llista.containsKey(nom))
            throw new ExpressioException("No es troba la funció \"" + nom + "\".");

        Funcio f = Funcio.llista.get(nom);
        if (!f.numParamsCorrecte(numParams))
            throw new ExpressioException("Nombre invàlid de paràmetres per a la funció " + nom + ".");

        return f;
    }
    
    public Complex evaluar(Complex[] params) throws ExpressioException {
        // Executar la funció amb els paràmetres
        return buscarFuncio(nom, parametres.length).evaluar(parametres, params).evaluar(params);
    }

    public boolean esConstant() throws ExpressioException {
        boolean constant = true;

        // Si tots els paràmetres són constants, l'expressió completa també
        for (Expressio p : parametres)
            if (!p.esConstant())
                constant = false;

        return constant;
    }
    
    @Override public String toString() {
        if (parametres.length > 0) {
            StringBuilder sb = new StringBuilder(nom).append('(');
            for (int i = 0; i < parametres.length; i++) {
                if (i != 0)
                    sb.append(',');
                sb.append(parametres[i]);
            }
            return sb.append(')').toString();
        } else {
            return nom;
        }
    }

    @Override public Expressio derivada() throws ExpressioException {
        return buscarFuncio(nom, parametres.length).derivada(parametres);
    }

    public Expressio substituirParametres(Expressio[] params) {
        Expressio[] parametresNous = new Expressio[parametres.length];
        for (int i = 0; i < parametres.length; i++) {
            try {
                parametresNous[i] = parametres[i].substituirParametres(params);
            } catch (Exception e) {}
        }

        return new ExpressioFuncio(nom, parametresNous);
    }
}
