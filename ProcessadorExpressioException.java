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
 * Classe d'excepció per quan es produeix un problema al processar una expressió.
 */
public class ProcessadorExpressioException extends Exception {
    /**
     * Create una nova ProcessadorExpressioException.
     * @param error El missatge d'error.
     */
    public ProcessadorExpressioException(String msg) {
        super(msg);
    }
}
