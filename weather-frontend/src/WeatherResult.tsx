import type { WeatherData } from './weatherTypes'
import { getWeatherIcon } from './weatherIcon'
import { getLocationName } from './weatherLocation'

// 表示部分で設定しているクラスには、次の役割があります。
// weather-result	検索結果全体の配置を整える
// weather-summary	アイコンと説明文を横並びにする
// weather-icon	アイコンの大きさや位置を調整する
// weather-description	天気説明を読みやすく強調する
// weather-details	気温・風速を同じ形式で配置する
type WeatherResultProps = {
  weather: WeatherData
  onReset: () => void
}

export function WeatherResult({
  weather,
  onReset,
}: WeatherResultProps) {
  const locationName = getLocationName(weather)

  return (
    <>
      <section className="weather-result" aria-live="polite">
        <h2>{locationName}の天気</h2>

        <div className="weather-summary">
          <span className="weather-icon" aria-hidden="true">
            {getWeatherIcon(weather.weatherCode)}
          </span>

          <p className="weather-description">
            {weather.weatherDescription}
          </p>

          <p className="weather-temperature">
            {weather.temperature}℃
          </p>
        </div>

        <dl className="weather-details">
          <div>
            <dt>風速</dt>
            <dd>{weather.windSpeed}m/s</dd>
          </div>
        </dl>
      </section>

      <button
        className="reset"
        type="button"
        onClick={onReset}
      >
        もう一度調べる
      </button>
    </>
  )
}