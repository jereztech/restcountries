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

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.jereztech.restcountries.v1.data.Currency;

/**
 * Defines the logic for currencies search.
 * 
 * @author Joel Jerez
 */
@Service
public class CurrencyService extends AbstractService<Currency, Void> {

	@Value("classpath:/v1/currencies/currencies.json")
	private Resource currenciesResource;

	@Override
	protected Class<Currency> getEntityClass() {
		return Currency.class;
	}

	@Override
	protected InputStream getEntitiesResource() throws IOException {
		return currenciesResource.getInputStream();
	}

	@Override
	protected Class<Void> getTranslationClass() {
		return null;
	}

	@Override
	protected String getTranslationsPath() {
		return null;
	}

}
