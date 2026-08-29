// バックエンドから受け取る天気情報の構造を定義する。
export type WeatherData = {
  status: string
  city: string
  prefecture: string
  country: string
  temperature: number
  weatherCode: number
  weatherDescription: string
  windSpeed: number
  time: string
}