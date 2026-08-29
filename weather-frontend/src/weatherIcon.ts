import { getWeatherCategory } from './weatherCategory'
/**
 * 天気アイコンの出し分けを定義する関数
 * @param weatherCode 
 * @returns 
 */
export function getWeatherIcon(weatherCode: number): string {
  const category = getWeatherCategory(weatherCode)

  switch (category) {
    case 'sunny':
      return weatherCode === 0 ? '☀️' : '🌤️'

    case 'cloudy':
      return '☁️'

    case 'fog':
      return '🌫️'

    case 'rain':
      return weatherCode === 65 ? '⛈️' : '🌧️'

    case 'snow':
      return '🌨️'

    case 'storm':
      return '⛈️'

    default:
      return '🌡️'
  }
}