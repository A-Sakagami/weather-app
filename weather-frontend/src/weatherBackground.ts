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

/** 
 * 天気コードと時刻から、UIに適用するテーマ名を返す関数
 * 夜間でも晴れ・おおむね晴れ以外は天気を優先する
 */
export function getWeatherTheme(
  weatherCode?: number,
  time?: string,
): WeatherTheme {
  if (weatherCode === undefined) {
    return 'default'
  }

  // 天気コードをカテゴリに変換
  const category = getWeatherCategory(weatherCode)

  if (category === 'unknown') {
    return 'default'
  }

  // 晴れ・おおむね晴れの場合のみ夜間テーマを優先
  if (category === 'sunny' && !isDay(time)) {
    return 'night'
  }

  return category
}
