import { useState } from 'react'
import './App.css'
import { getWeatherTheme } from "./weatherBackground"
import type { WeatherData } from './weatherTypes'
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

  // リザルト画面の背景テーマ
  const theme = getWeatherTheme(
    weather?.weatherCode,
    weather?.time,
  )

  // 検索中のローディング表示
  const [loading, setLoading] = useState(false);

  // 検索履歴を保持・表示する
  const [searchHistory, setSearchHistory] = useState<string[]>([])

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
    // ローディング画面を表示する
    setLoading(true)

    try {
      const searchedCity = city.trim()
      const data = await fetchWeather(searchedCity)
      // 検索履歴に追加する。重複は削除し、最大5件まで保持する。
      setSearchHistory((previous) =>
        [
          searchedCity,
          ...previous.filter(
            (item) => item.toLowerCase() !== searchedCity.toLowerCase(),
          ),
        ].slice(0, 5),
      )

      setWeather(data)
      setView('result')
    } catch (caughtError) {
      setWeather(null)

      if (caughtError instanceof TypeError) {
        setError(
          '通信に失敗しました。通信状況を確認して、もう一度お試しください。',
        )
      } else if (caughtError instanceof Error) {
        setError(caughtError.message)
      } else {
        setError(
          '天気情報を取得できませんでした。もう一度お試しください。',
        )
      }
    } finally {
      setLoading(false)
    }
  }

  const handleReset = () => {
    setView('search')
    setWeather(null)
  }

  return (
    <main className={`weather-app weather-app--${theme}`}>
      <h1>お天気アプリ</h1>

      {view === 'search' ? (
        <>
          <WeatherSearchForm
            onSubmit={handleSubmit}
            error={error}
            loading={loading}
          />

          {searchHistory.length > 0 && (
            <section aria-label="検索履歴">
              <h2>最近検索した都市</h2>
              {searchHistory.map((city) => (
                <button
                  key={city}
                  type="button"
                  disabled={loading}
                  onClick={() => {
                    const formData = new FormData()
                    formData.set('city', city)
                    void handleSubmit(formData)
                  }}
                >
                  {city}
                </button>
              ))}
            </section>
          )}
        </>
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