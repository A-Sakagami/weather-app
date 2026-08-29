/**
 * weatherCodeを天気カテゴリに分類する関数
 */
export type WeatherCategory =
  | 'sunny'
  | 'cloudy'
  | 'fog'
  | 'rain'
  | 'snow'
  | 'storm'
  | 'unknown'

export function getWeatherCategory(
  weatherCode: number,
): WeatherCategory {
  if ([0, 1, 2].includes(weatherCode)) {
    return 'sunny'
  }

  if (weatherCode === 3) {
    return 'cloudy'
  }

  if ([45, 48].includes(weatherCode)) {
    return 'fog'
  }

  if (
    [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82].includes(
      weatherCode,
    )
  ) {
    return 'rain'
  }

  if ([71, 73, 75, 77, 85, 86].includes(weatherCode)) {
    return 'snow'
  }

  if ([95, 96, 99].includes(weatherCode)) {
    return 'storm'
  }

  return 'unknown'
}