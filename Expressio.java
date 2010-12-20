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
 * Classe abstracta que forma la base de totes les expressions matemàtiques.
 */
public abstract class Expressio {
    /**
     * Evalua l'expressió i calcula el valor numèric resultant.
     * @param ctx Context en el que s'evalua l'expressió.
     * @return El resultat obtingut d'evaluar l'expressió.
     * @throws ExpressioException
     */
    public abstract Complex evaluar(Complex[] params) throws ExpressioException;

    public abstract Expressio derivada() throws ExpressioException;

    /**
     * Determina si l'expressió té un resultat constant.
     * @return true si és constant, false en cas contrari.
     * @throws ExpressioException
     */
    public abstract boolean esConstant() throws ExpressioException;

    public abstract Expressio substituirParametres(Expressio[] params) throws ExpressioException;
}
