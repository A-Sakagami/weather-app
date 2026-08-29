import { useState } from 'react'
import './App.css'
import { getWeatherTheme, isDay } from "./weatherBackground"
import { getWeatherIcon } from './weatherIcon'
import type { WeatherData } from './weatherTypes'
import { getLocationName } from './weatherLocation'
import { fetchWeather } from './weatherAPI'
import { WeatherSearchForm } from './WeatherSearchForm'
import { WeatherResult } from './WeatherResult'

function App() {
  // 正常に取得できた天気情報を保持する。
  // まだ検索していない場合や取得に失敗した場合はnullになる。
  const [weather, setWeather] = useState<WeatherData | null>(null)
  const [view, setView] = useState<'search' | 'result'>('search')

  // 利用者に表示するエラーメッセージを保持する。
  // 空文字の場合はエラーを表示しない。
  const [error, setError] = useState('')

  // 
  const theme = getWeatherTheme(
    weather?.weatherCode,
    weather?.time,
  )

  // フォーム送信時に都市名を受け取り、バックエンドから天気情報を取得する。
  // apiの応答を待つため、非同期関数asyncとして定義する。
  const handleSubmit = async (formData: FormData) => {
    const city = formData.get('city')

    // FormDataの値は文字列とは限らないため、型と空欄を確認する。
    if (typeof city !== 'string' || city.trim() === '') {
      return
    }

    // 前回の検索で表示されたエラーを消してから、新しい検索を始める。
    setError('')

   try {
      const data = await fetchWeather(city)

      setWeather(data)
      setView('result')
    } catch {
      setWeather(null)
      setError('天気情報を取得できませんでした。都市名を確認してください。')
    }
  }

  const handleReset = () => {
    setView('search')
    setWeather(null)
  }

  const locationName = weather
    ? getLocationName(weather)
    : ''
  // 表示部分で設定しているクラスには、次の役割があります。
  // weather-result	検索結果全体の配置を整える
  // weather-summary	アイコンと説明文を横並びにする
  // weather-icon	アイコンの大きさや位置を調整する
  // weather-description	天気説明を読みやすく強調する
  // weather-details	気温・風速を同じ形式で配置する
  return (
    <main className={`weather-app weather-app--${theme}`}>
      <h1>お天気アプリ</h1>

      {view === 'search' ? (
        <WeatherSearchForm
          onSubmit={handleSubmit}
          error={error}
        />
      ) : weather && (
        <WeatherResult
          weather={weather}
          onReset={handleReset}
        />
      )}
      <div id="powered-by">
            <p>powered by <a href="https://open-meteo.com/" target="_blank" rel="noopener noreferrer">Open-Meteo</a></p>
          </div>
          <div id="copyright">
            <p>©A-Sakagami 2026-{new Date().getFullYear()}</p>
      </div>
    </main>
  )
}

export default App