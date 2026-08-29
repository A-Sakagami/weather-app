/** 
 * 天気表示時の表示背景を指定する
 * [0, 1, 2] sunny/mostly sunny  昼：青空系、夜：紺系
 * [3] cloudy
 * [45, 48] fog
 * [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82] rain
 * [71, 73, 75, 77, 85, 86] snow
 * [95, 96, 99] storm
*/

// time文字列（OffsetDateTime）から現地の時刻が昼（6〜18時）かを判定する
export function isDay(time?: string): boolean {
  if (!time) return true;
  const hour = new Date(time).getHours();
  return hour >= 6 && hour < 18;
}

export function getWeatherBackground(weatherCode?: number, time?: string): string {
  if (weatherCode === undefined) {
    return "linear-gradient(135deg, #e0f2fe, #f8fafc)";
  }

  if (weatherCode === 0) {
    return isDay(time)
      ? "linear-gradient(135deg, #38bdf8, #fef3c7)"
      : "linear-gradient(135deg, #1e3a5f, #0f172a)";
  }

  if ([1, 2].includes(weatherCode)) {
    return isDay(time)
      ? "linear-gradient(135deg, #4dc5f8, #fef3c7)"
      : "linear-gradient(135deg, #1e3a5f, #334155)";
  }

  if (weatherCode === 3) {
    return "linear-gradient(135deg, #94a3b8, #e2e8f0)";
  }

  if ([45, 48].includes(weatherCode)) {
    return "linear-gradient(135deg, #cbd5e1, #f1f5f9)";
  }

  if (
    [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82].includes(weatherCode)
  ) {
    return "linear-gradient(135deg, #475569, #7dd3fc)";
  }

  if ([71, 73, 75, 77, 85, 86].includes(weatherCode)) {
    return "linear-gradient(135deg, #e0f2fe, #ffffff)";
  }

  if ([95, 96, 99].includes(weatherCode)) {
    return "linear-gradient(135deg, #312e81, #64748b)";
  }

  return "linear-gradient(135deg, #e0f2fe, #f8fafc)";
}