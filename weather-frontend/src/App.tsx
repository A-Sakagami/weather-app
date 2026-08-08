import { useState } from 'react'
import './App.css'

// バックエンドから受け取る天気情報の構造を定義する。
// APIレスポンスの項目名や型が変わった場合は、ここも合わせて変更する。
type WeatherData = {
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

// Open-Meteoの天気コードを、画面表示用のアイコンへ変換する。
// 未知のコードにも対応できるよう、最後に共通アイコンを返す。
const getWeatherIcon = (weatherCode: number) => {
  if (weatherCode === 0) return '☀️'
  if (weatherCode === 1 || weatherCode === 2) return '🌤️'
  if (weatherCode === 3) return '☁️'
  if (weatherCode === 45 || weatherCode === 48) return '🌫️'
  if (weatherCode >= 51 && weatherCode <= 57) return '🌦️'
  if (weatherCode >= 61 && weatherCode <= 67) return '🌧️'
  if (weatherCode >= 71 && weatherCode <= 77) return '🌨️'
  if (weatherCode >= 80 && weatherCode <= 82) return '🌦️'
  if (weatherCode >= 85 && weatherCode <= 86) return '🌨️'
  if (weatherCode >= 95 && weatherCode <= 99) return '⛈️'

  return '🌡️'
}

function App() {
  // 正常に取得できた天気情報を保持する。
  // まだ検索していない場合や取得に失敗した場合はnullになる。
  const [weather, setWeather] = useState<WeatherData | null>(null)

  // 利用者に表示するエラーメッセージを保持する。
  // 空文字の場合はエラーを表示しない。
  const [error, setError] = useState('')

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
      const response = await fetch(
        `/api/weather?city=${encodeURIComponent(city.trim())}`,
      )

      if (!response.ok) {
        // fetchは404や500でも通常のレスポンスとして完了するため、
        // HTTPエラーを明示的に例外として扱う。
        throw new Error()
      }

      const data: WeatherData = await response.json()
      setWeather(data)
    } catch {
      // 以前の正常な結果を残さず、今回の検索が失敗したことを表示する。
      setWeather(null)
      setError('天気情報を取得できませんでした。都市名を確認してください。')
    }
  }
  // 表示部分で設定しているクラスには、次の役割があります。
  // weather-result	検索結果全体の配置を整える
  // weather-summary	アイコンと説明文を横並びにする
  // weather-icon	アイコンの大きさや位置を調整する
  // weather-description	天気説明を読みやすく強調する
  // weather-details	気温・風速を同じ形式で配置する
  return (
    <main>
      <h1>お天気アプリ</h1>

      <form action={handleSubmit}>
        <label htmlFor="city">都市名</label>
        <input
          id="city"
          name="city"
          type="text"
          placeholder="例：Tokyo"
          autoComplete="address-level2"
          required
        />
        <button type="submit">天気を調べる</button>
      </form>

      {error && <p role="alert">{error}</p>}

      {weather && (
        <section className="weather-result" aria-live="polite">
          <h2>
            {weather.country === '日本' || weather.country === 'Japan'
              ? weather.prefecture === weather.city
                ? weather.city
                : `${weather.prefecture}${weather.city}`
              : weather.city}
            の天気
          </h2>
          <div className="weather-summary">
            <span className="weather-icon" aria-hidden="true">
              {getWeatherIcon(weather.weatherCode)}
            </span>
            <p className="weather-description">
              {weather.weatherDescription}
            </p>
          </div>

          <dl className="weather-details">
            <div>
              <dt>現在の気温</dt>
              <dd>{weather.temperature}℃</dd>
            </div>
            <div>
              <dt>風速</dt>
              <dd>{weather.windSpeed}m/s</dd>
            </div>
          </dl>
          <div id="powered-by">
            <p>powered by <a href="https://open-meteo.com/" target="_blank" rel="noopener noreferrer">Open-Meteo</a></p>
          </div>
          <div id="copyright">
            <p>©A-Sakagami 2026-{new Date().getFullYear()}</p>
          </div>
        </section>
      )}
    </main>
  )
}

export default App