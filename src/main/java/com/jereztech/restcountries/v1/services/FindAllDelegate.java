/*******************************************************************************
 * Copyright (C) 2021 Joel Jerez
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.jereztech.restcountries.v1.services;

import java.util.List;

/**
 * Represents the behaviors for findAll operations that must be realized without
 * inheritance.
 * 
 * @author Joel Jerez
 *
 * @param <E> the Entity Class
 * @param <T> the Translation Class
 */
public interface FindAllDelegate<E, T> {

	List<E> findAll(Integer pageNumber, Integer pageSize, String filter);

	List<T> findAllWithLocale(Integer pageNumber, Integer pageSize, String filter, String locale);

}
