package org.wicketstuff.rest.resource;

import java.util.Date;

import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.domain.WeatherForecast;
import org.wicketstuff.rest.gson.GsonRestResource;

import com.google.gson.Gson;

public class WeatherForecastRestResource extends GsonRestResource {
	
	@MethodMapping("/forecast/{date}/{partday}")
	public WeatherForecast getForecast(Date day, int partOfTheDay){
		return new WeatherForecast(67.8f, 24.2f, 3, 2);
	}

	@MethodMapping("/forecast/{partday}")
	public WeatherForecast getForecast(int partOfTheDay){
		return new WeatherForecast(67.8f, 24.2f, 3, 2);
	}
}
