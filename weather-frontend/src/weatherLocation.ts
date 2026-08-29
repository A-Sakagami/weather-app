import type { WeatherData } from './weatherTypes'

export function getLocationName(weather: WeatherData): string {
  const isJapan =
    weather.country === '日本' ||
    weather.country === 'Japan'

  if (!isJapan) {
    return weather.city
  }

  if (weather.prefecture === weather.city) {
    return weather.city
  }

  return `${weather.prefecture}${weather.city}`
}