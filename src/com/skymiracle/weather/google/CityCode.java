package com.skymiracle.weather.google;
	public class CityCode {
		private String latitude_e6;

		private String longitude_e6;

		private String name;

		public CityCode() {
			
		}
		
		public CityCode( String name, String latitude_e6, String longitude_e6) {
			super();
			this.latitude_e6 = latitude_e6;
			this.longitude_e6 = longitude_e6;
			this.name = name;
		}

		public String getLatitude_e6() {
			return latitude_e6;
		}

		public void setLatitude_e6(String latitude_e6) {
			this.latitude_e6 = latitude_e6;
		}

		public String getLongitude_e6() {
			return longitude_e6;
		}

		public void setLongitude_e6(String longitude_e6) {
			this.longitude_e6 = longitude_e6;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
