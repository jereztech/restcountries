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
package com.jereztech.restcountries;

import static com.jereztech.restcountries.support.Constants.FIRST_PAGE_INT;
import static com.jereztech.restcountries.support.Constants.PAGE_NUMBER_2;
import static com.jereztech.restcountries.support.Constants.PAGE_SIZE_3;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jereztech.restcountries.v1.data.Country;
import com.jereztech.restcountries.v1.data.CountryTranslation;
import com.jereztech.restcountries.v1.ws.CountryController;

/**
 * @author Joel Jerez
 */
@SpringBootTest
class CountryControllerTests {

	@Autowired
	private CountryController countryController;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void testPagination() {
		List<Country> countries = countryController.findAll(FIRST_PAGE_INT, PAGE_SIZE_3, null);
		assertEquals(countries.size(), PAGE_SIZE_3);
		assertEquals(countries.get(0).getName(), "Afghanistan");
		countries = countryController.findAll(PAGE_NUMBER_2, PAGE_SIZE_3, null);
		assertEquals(countries.get(0).getName(), "Angola");
	}

	@Test
	void testFilter() {
		List<Country> countries = countryController.findAll(FIRST_PAGE_INT, PAGE_SIZE_3, "alpha3Code eq BRA");
		assertEquals(countries.get(0).getName(), "Brazil");
	}

	@Test
	void testTranslations() {
		List<CountryTranslation> countries = countryController.findAllWithLocale(FIRST_PAGE_INT, PAGE_SIZE_3, "alpha2Code eq US",
				"en");
		assertEquals(countries.get(0).getCountry(), "United States");
		countries = countryController.findAllWithLocale(FIRST_PAGE_INT, PAGE_SIZE_3, "alpha2Code eq US", "es");
		assertEquals(countries.get(0).getCountry(), "Estados Unidos");
	}

	@Test
	@SuppressWarnings("unchecked")
	void testIncludeProperties() throws IOException {
		MappingJacksonValue jacksonValue = countryController.findAllMapping(FIRST_PAGE_INT, PAGE_SIZE_3, null, null,
				"currencies,numericCode", null);
		String _response = objectMapper.writer(jacksonValue.getFilters())
				.writeValueAsString(((List<Country>) jacksonValue.getValue()).get(0));
		assertTrue(_response.contains("\"currencies\""));
		assertTrue(_response.contains("\"numericCode\""));
		assertFalse(_response.contains("\"alpha3Code\""));
	}

	@Test
	@SuppressWarnings("unchecked")
	void testIgnoreProperties() throws IOException {
		MappingJacksonValue jacksonValue = countryController.findAllMapping(FIRST_PAGE_INT, PAGE_SIZE_3, null, null, null,
				"currencies,numericCode");
		String _response = objectMapper.writer(jacksonValue.getFilters())
				.writeValueAsString(((List<Country>) jacksonValue.getValue()).get(0));
		assertFalse(_response.contains("\"currencies\""));
		assertFalse(_response.contains("\"numericCode\""));
		assertTrue(_response.contains("\"alpha3Code\""));
	}

}
