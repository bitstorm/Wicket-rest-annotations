package org.wicketstuff.rest.domain;

public class WeatherForecast {
	private float umidity;
	private float temperature;
	private int status;
	private int partOfTheDay;
	
	public WeatherForecast(float umidity, float temperature, 
							int status, int partOfTheDay) {
		this.umidity = umidity;
		this.temperature = temperature;
		this.status = status;
		this.partOfTheDay = partOfTheDay;
	}
}
