import { getWeatherCategory } from './weatherCategory'
/**
 * 昼夜を考慮してUIテーマを決定する関数
 */
export type WeatherTheme =
  | 'default'
  | 'sunny'
  | 'cloudy'
  | 'fog'
  | 'rain'
  | 'snow'
  | 'storm'
  | 'night'

export function isDay(time?: string): boolean {
  if (!time) {
    return true
  }

  const timeMatch = time.match(/T(\d{2}):/)

  if (!timeMatch) {
    return true
  }

  const hour = Number(timeMatch[1])

  return hour >= 6 && hour < 18
}

export function getWeatherTheme(
  weatherCode?: number,
  time?: string,
): WeatherTheme {
  if (weatherCode === undefined) {
    return 'default'
  }

  if (!isDay(time)) {
    return 'night'
  }

  const category = getWeatherCategory(weatherCode)

  if (category === 'unknown') {
    return 'default'
  }

  return category
}