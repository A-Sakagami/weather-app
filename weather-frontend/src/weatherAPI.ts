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
    let errorMessage = '天気情報の取得に失敗しました。'

    try {
      const errorData: unknown = await response.json()

      if (
        typeof errorData === 'object' &&
        errorData !== null &&
        'message' in errorData &&
        typeof errorData.message === 'string'
      ) {
        errorMessage = errorData.message
      }
    } catch {
      // JSON形式ではないエラーレスポンスでは既定のメッセージを使用する。
    }

    throw new Error(errorMessage)
  }

  return response.json()
}