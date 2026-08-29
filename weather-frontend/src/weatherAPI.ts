import type { WeatherData } from './weatherTypes'

/**
 * API通信処理
 */
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? ''

export async function fetchWeather(city: string): Promise<WeatherData> {
  const response = await fetch(
    `${apiBaseUrl}/api/weather?city=${encodeURIComponent(city.trim())}`,
  )

  if (!response.ok) {
    throw new Error('Failed to fetch weather')
  }

  return response.json()
}