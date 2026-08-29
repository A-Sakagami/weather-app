type WeatherSearchFormProps = {
  onSubmit: (formData: FormData) => void | Promise<void>
  error: string
}

export function WeatherSearchForm({
  onSubmit,
  error,
}: WeatherSearchFormProps) {
  return (
    <>
      <form action={onSubmit}>
        <label htmlFor="city">都市名</label>

        <input
          id="city"
          name="city"
          type="text"
          placeholder="例：Tokyo"
          autoComplete="address-level2"
          required
        />

        <button className="search" type="submit">
          天気を調べる
        </button>
      </form>

      {error && <p role="alert">{error}</p>}
    </>
  )
}