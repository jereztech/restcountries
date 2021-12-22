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
package com.jereztech.restcountries.v1.ws;

import static com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.filterOutAllExcept;
import static com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAll;
import static com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.serializeAllExcept;
import static com.jereztech.restcountries.support.Constants.COMMA_SEPARATOR;
import static com.jereztech.restcountries.support.Constants.FIRST_PAGE;
import static com.jereztech.restcountries.support.Constants.PAGE_SIZE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.strip;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jereztech.restcountries.support.Constants;
import com.jereztech.restcountries.v1.services.AbstractService;
import com.jereztech.restcountries.v1.services.FindAllDelegate;

/**
 * Defines the base endpoint logic.
 * 
 * @author Joel Jerez
 *
 * @param <E> the Entity Class
 * @param <T> the Translation Class
 * @param <S> the concrete Service
 */
public abstract class AbstractController<E, T, S extends AbstractService<E, T>> implements FindAllDelegate<E, T> {

	protected final S service;

	public AbstractController(S service) {
		this.service = service;
	}

	/**
	 * Defines the logic to find all entities that match the filter.
	 */
	@Override
	@Cacheable(value = "entities", key = "{#pageNumber,#pageSize,#filter}")
	public List<E> findAll(Integer pageNumber, Integer pageSize, String filter) {
		return service.findAll(pageNumber, pageSize, filter);
	}

	/**
	 * Defines the logic to find all entity translations by locale that match the
	 * filter.
	 */
	@Override
	@Cacheable(value = "translations", key = "{#pageNumber,#pageSize,#filter,#locale}")
	public List<T> findAllWithLocale(Integer pageNumber, Integer pageSize, String filter, String locale) {
		return service.findAllWithLocale(pageNumber, pageSize, filter, locale);
	}

	/**
	 * Find all entities that match the filter including or excluding properties.
	 *
	 * <pre>
	 * 
	 * * Pagination and Filter
	 * Request Example: GET /v1/countries?pageNumber=0&pageSize=1&filter=alpha3Code eq BRA
	 * Response Example:
		[
		  {
		    "name": "Brazil",
		    "capital": "Brasília",
		    "alpha2Code": "BR",
		    "alpha3Code": "BRA",
		    ...
		  },
		  ...
		]
	 *
	 * * Localization
	 * Request Example: GET /v1/countries?lang=pt
	 * Response Example:
		[
		  {
		    "alpha2Code": "AF",
		    "country": "Afeganistão",
		  },
		  ...
		]
	 *
	 * * Including Properties
	 * Request Example: GET /v1/countries?includeProperties=name
	 * Response Example:
		[
		  {
		    "name": "Brazil"
		  },
		  {
		    "name": "British Indian Ocean Territory"
		  },
		  ...
		]
	 *
	 * * Ignoring Properties
	 * Request Example: GET /v1/countries?ignoreProperties=name
	 * Response Example:
		[
		  {
		    "capital": "Brasília",
		    "alpha2Code": "BR",
		    "alpha3Code": "BRA",
		    ...
		  },
		  {
		    "capital": "Diego Garcia",
		    "alpha2Code": "IO",
		    "alpha3Code": "IOT",
		    ...
		  },
		  ...
		]
	 * </pre>
	 */
	@GetMapping
	public MappingJacksonValue findAllMapping(@RequestParam(defaultValue = FIRST_PAGE) Integer pageNumber,
			@RequestParam(defaultValue = PAGE_SIZE) Integer pageSize, @RequestParam(required = false) String lang,
			@RequestParam(required = false) String filter, @RequestParam(required = false) String includeProperties,
			@RequestParam(required = false) String ignoreProperties) {
		List<?> response = isNotBlank(lang) ? findAllWithLocale(pageNumber, pageSize, filter, lang)
				: findAll(pageNumber, pageSize, filter);
		return responseMapping(includeProperties, ignoreProperties, response);
	}

	/**
	 * Apply the mapping to the ResultSet.
	 */
	private MappingJacksonValue responseMapping(String includeProperties, String ignoreProperties, List<?> response) {
		MappingJacksonValue mappingResponse = new MappingJacksonValue(response);
		SimpleBeanPropertyFilter propertiesFilter = isNotBlank(includeProperties)
				? filterOutAllExcept(strip(includeProperties).split(COMMA_SEPARATOR))
				: isNotBlank(ignoreProperties) ? serializeAllExcept(strip(ignoreProperties).split(COMMA_SEPARATOR))
						: serializeAll();
		mappingResponse.setFilters(new SimpleFilterProvider().addFilter(Constants.FILTER_NAME, propertiesFilter));
		return mappingResponse;
	}

}
