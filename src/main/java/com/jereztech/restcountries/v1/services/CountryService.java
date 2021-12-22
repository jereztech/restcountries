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

import static java.lang.String.format;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.jereztech.restcountries.v1.data.Country;
import com.jereztech.restcountries.v1.data.CountryTranslation;

/**
 * Defines the logic for countries search.
 * 
 * @author Joel Jerez
 */
@Service
public class CountryService extends AbstractService<Country, CountryTranslation> {

	@Value("${restcountries.flags-url}")
	private String flagURL;

	@Value("classpath:/v1/countries/countries.json")
	private Resource countriesResource;

	@Override
	protected Class<Country> getEntityClass() {
		return Country.class;
	}

	@Override
	protected InputStream getEntitiesResource() throws IOException {
		return countriesResource.getInputStream();
	}

	@Override
	protected Class<CountryTranslation> getTranslationClass() {
		return CountryTranslation.class;
	}

	@Override
	protected String getTranslationsPath() {
		return "classpath:/v1/countries/translations";
	}

	@Override
	public List<CountryTranslation> findAllWithLocale(Integer pageNumber, Integer pageSize, String filter, String locale) {
		List<CountryTranslation> translations = super.findAllWithLocale(pageNumber, pageSize, filter, locale);
		translations.forEach(item -> item.setFlag(format("%s/%s.png", flagURL, item.getAlpha2Code().toLowerCase())));
		return translations;
	}

}
