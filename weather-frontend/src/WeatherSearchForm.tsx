type WeatherSearchFormProps = {
  onSubmit: (formData: FormData) => void | Promise<void>
  error: string
  loading: boolean
}

export function WeatherSearchForm({
  onSubmit,
  error,
  loading,
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

        <button
          className="search"
          type="submit"
          disabled={loading}
        >
          {loading ? (
            <>
              <span className="loading-spinner" aria-hidden="true" />
              取得中…
            </>
          ) : (
            '天気を調べる'
          )}
        </button>
      </form>

      {error && <p role="alert">{error}</p>}
    </>
  )
}