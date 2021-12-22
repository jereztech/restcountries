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

import com.jereztech.restcountries.v1.data.Language;
import com.jereztech.restcountries.v1.data.LanguageTranslation;

/**
 * Defines the logic for languages search.
 * 
 * @author Joel Jerez
 */
@Service
public class LanguageService extends AbstractService<Language, LanguageTranslation> {

	@Value("classpath:/v1/languages/languages.json")
	private Resource languagesResource;

	@Override
	protected Class<Language> getEntityClass() {
		return Language.class;
	}

	@Override
	protected InputStream getEntitiesResource() throws IOException {
		return languagesResource.getInputStream();
	}

	@Override
	protected Class<LanguageTranslation> getTranslationClass() {
		return LanguageTranslation.class;
	}

	@Override
	protected String getTranslationsPath() {
		return "classpath:/v1/languages/translations";
	}

}
