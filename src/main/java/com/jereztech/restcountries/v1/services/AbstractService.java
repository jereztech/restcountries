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

import static com.jereztech.restcountries.support.Constants.AND_SEPARATOR;
import static com.jereztech.restcountries.support.Constants.EQ_SEPARATOR;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.strip;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.jereztech.restcountries.support.JsonUtil;
import com.jereztech.restcountries.support.ProxyAccessor;

/**
 * Defines the logic to find all entities.
 * 
 * @author Joel Jerez
 *
 * @param <E> the Entity Class
 * @param <T> the Translation Class
 */
public abstract class AbstractService<E, T> implements FindAllDelegate<E, T> {

	@Autowired
	private JsonUtil jsonUtil;

	@Autowired
	private ApplicationContext appContext;

	private List<E> data;

	protected abstract Class<E> getEntityClass();

	protected abstract InputStream getEntitiesResource() throws IOException;

	protected abstract Class<T> getTranslationClass();

	protected abstract String getTranslationsPath();

	/**
	 * Defines the logic for reading source files.
	 */
	@PostConstruct
	private void init() {
		try {
			if (getEntitiesResource() != null) {
				data = jsonUtil.fromJson(getEntitiesResource(), getEntityClass());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Defines the logic to find all entities that match the filter.
	 */
	@Override
	public List<E> findAll(Integer pageNumber, Integer pageSize, String filter) {
		return (List<E>) filter(pageNumber, pageSize, filter, data);
	}

	/**
	 * Defines the logic to find all entity translations by locale that match the
	 * filter.
	 */
	@Override
	public List<T> findAllWithLocale(Integer pageNumber, Integer pageSize, String filter, String locale) {
		if (isAnyBlank(locale, getTranslationsPath())) {
			throw new UnsupportedOperationException();
		}
		try {
			String localizedPath = String.format("%s/%s.json", getTranslationsPath(), locale);
			List<T> translations = jsonUtil.fromJson(appContext.getResource(localizedPath).getInputStream(),
					getTranslationClass());
			return (List<T>) filter(pageNumber, pageSize, filter, translations);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid locale.");
		}
	}

	/**
	 * Apply filter to the ResultSet.
	 */
	private List<?> filter(Integer pageNumber, Integer pageSize, String filter, List<?> source) {
		return source.stream().filter(item -> {
			if (isBlank(filter)) {
				return true;
			}
			Map<String, String> expressions = Arrays.stream(strip(filter).split(AND_SEPARATOR))
					.map(exp -> exp.split(EQ_SEPARATOR)).collect(toMap(key -> key[0], value -> value[1]));
			return expressions.entrySet().stream()
					.allMatch(entry -> entry.getValue().equals(ProxyAccessor.proxify(item, entry.getKey())));
		}).skip(pageSize * pageNumber).limit(pageSize).collect(toList());
	}

}
