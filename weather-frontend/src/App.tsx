import { useState } from 'react'
import './App.css'

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
  const [weather, setWeather] = useState<WeatherData | null>(null)
  const [error, setError] = useState('')

  const handleSubmit = async (formData: FormData) => {
    const city = formData.get('city')

    if (typeof city !== 'string' || city.trim() === '') {
      return
    }

    setError('')

    try {
      const response = await fetch(
        `/api/weather?city=${encodeURIComponent(city.trim())}`,
      )

      if (!response.ok) {
        throw new Error()
      }

      const data: WeatherData = await response.json()
      setWeather(data)
    } catch {
      setWeather(null)
      setError('天気情報を取得できませんでした。都市名を確認してください。')
    }
  }

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
          <h2>{locationName}の天気</h2>

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
              <dt>気温</dt>
              <dd>{weather.temperature}℃</dd>
            </div>
            <div>
              <dt>風速</dt>
              <dd>{weather.windSpeed}m/s</dd>
            </div>
          </dl>
        </section>
      )}
    </main>
  )
}

export default App