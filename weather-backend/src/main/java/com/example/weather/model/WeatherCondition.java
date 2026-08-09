package com.example.weather.model;

/**
 * Open-Meteoの天気コードに対応する天候の種類を表す列挙型。
 *
 * <p>Open-Meteo APIでは、「晴れ」や「雨」などの天候が
 * 数値の天気コードとして返される。
 * この列挙型を使用することで、数値コードをアプリ内で扱いやすい
 * 天候の種類と日本語の説明へ変換できる。
 *
 * <p>{@link #fromCurrentWeather(int, double, double, double)}では、
 * 天気コードと実際の降水量を組み合わせて現在の天候を判定する。
 *
 * <p>{@link #fromCode(int)}では、
 * 天気コードだけを使用して天候を判定する。
 *
 * <p>{@link #getDescription()}を使用すると、
 * 画面表示用の日本語説明を取得できる。
 */
public enum WeatherCondition {

    /**
     * 雲のない晴天。
     *
     * <p>天気コード0に対応する。
     */
    CLEAR("晴れ"),

    /**
     * わずかに雲がある、おおむね晴れの状態。
     *
     * <p>天気コード1に対応する。
     */
    MAINLY_CLEAR("おおむね晴れ"),

    /**
     * 晴れ間と雲が混在する状態。
     *
     * <p>天気コード2に対応する。
     */
    PARTLY_CLOUDY("一部曇り"),

    /**
     * 空の大部分が雲に覆われている状態。
     *
     * <p>天気コード3に対応する。
     */
    CLOUDY("曇り"),

    /**
     * 通常の霧または着氷性の霧。
     *
     * <p>天気コード45、48に対応する。
     */
    FOG("霧"),

    /**
     * 弱い雨粒が降る霧雨。
     *
     * <p>通常の霧雨と着氷性の霧雨をまとめて表し、
     * 天気コード51、53、55、56、57に対応する。
     */
    DRIZZLE("霧雨"),

    /**
     * 継続的に降る雨。
     *
     * <p>通常の雨と着氷性の雨をまとめて表し、
     * 天気コード61、63、65、66、67に対応する。
     *
     * <p>天気コードにかかわらず、現在の雨量または
     * 総降水量が0より大きい場合にも使用される。
     */
    RAIN("雨"),

    /**
     * 継続的に降る雪。
     *
     * <p>降雪と霧雪をまとめて表し、
     * 天気コード71、73、75、77に対応する。
     */
    SNOW("雪"),

    /**
     * 一時的に強く降るにわか雨。
     *
     * <p>天気コード80、81、82に対応する。
     *
     * <p>天気コードにかかわらず、現在のにわか雨量が
     * 0より大きい場合にも使用される。
     */
    RAIN_SHOWER("にわか雨"),

    /**
     * 一時的に降るにわか雪。
     *
     * <p>天気コード85、86に対応する。
     */
    SNOW_SHOWER("にわか雪"),

    /**
     * 雷を伴う雨またはひょう。
     *
     * <p>天気コード95、96、99に対応する。
     */
    THUNDERSTORM("雷雨"),

    /**
     * アプリが対応していない天気コード。
     *
     * <p>想定外のコードが返された場合でも処理を中断せず、
     * 「不明」として扱うために使用する。
     */
    UNKNOWN("不明");

    /**
     * 画面表示に使用する天候の日本語説明。
     *
     * <p>列挙値の生成時に設定され、
     * 生成後に変更されないよう{@code final}として保持する。
     */
    private final String description;

    /**
     * 天候の列挙値を、日本語の説明とともに生成する。
     *
     * <p>このコンストラクタは、
     * {@code CLEAR("晴れ")}のような列挙値の宣言時に使用される。
     *
     * @param description 画面表示に使用する天候の日本語説明
     */
    WeatherCondition(String description) {
        this.description = description;
    }

    /**
     * 天候の日本語説明を返す。
     *
     * <p>WeatherServiceでは、判定した列挙値に対して
     * このメソッドを呼び出し、WeatherResponseへ説明を設定する。
     *
     * @return 画面表示用の天候説明
     */
    public String getDescription() {
        return description;
    }

    /**
     * 天気コードと現在の降水量から、表示する天候を判定する。
     *
     * <p>APIから返された天気コードだけでなく、
     * 現在の総降水量、雨量、にわか雨量も確認することで、
     * 実際に降水が観測されている場合の表示を補正する。
     *
     * <p>判定には次の優先順位が適用される。
     *
     * <ol>
     *     <li>にわか雨量が0より大きければ「にわか雨」</li>
     *     <li>雨量または総降水量が0より大きければ「雨」</li>
     *     <li>降水がなければ天気コードによる判定</li>
     * </ol>
     *
     * <p>にわか雨量を先に判定することで、
     * 総降水量も同時に記録されている場合に
     * 通常の雨ではなく「にわか雨」を返せる。
     *
     * @param weatherCode   Open-Meteoから取得した天気コード
     * @param precipitation 雨、雪、にわか雨などを含む現在の総降水量
     * @param rain          現在の雨による降水量
     * @param showers       現在のにわか雨による降水量
     * @return 降水量と天気コードから判定した天候
     */
    public static WeatherCondition fromCurrentWeather(
            int weatherCode,
            double precipitation,
            double rain,
            double showers
    ) {
        /*
         * にわか雨による降水が記録されている場合は、
         * 天気コードより優先して「にわか雨」と判定する。
         */
        if (showers > 0) {
            return RAIN_SHOWER;
        }

        /*
         * 通常の雨量または総降水量が記録されている場合は、
         * 天気コードより優先して「雨」と判定する。
         */
        if (rain > 0 || precipitation > 0) {
            return RAIN;
        }

        /*
         * 降水が記録されていない場合は、
         * Open-Meteoの天気コードを使用して天候を判定する。
         */
        return fromCode(weatherCode);
    }

    /**
     * Open-Meteoの天気コードを対応する天候へ変換する。
     *
     * <p>{@code switch}式を使用し、同じ種類として表示する
     * 複数の天気コードを1つの列挙値へまとめている。
     *
     * <p>例えば、雨には強さや着氷の有無によって
     * 複数の天気コードがあるが、このアプリでは
     * コード61、63、65、66、67をすべて{@link #RAIN}として扱う。
     *
     * <p>定義されていないコードは{@link #UNKNOWN}へ変換するため、
     * 想定外の値が返された場合でも{@code null}を返さずに処理できる。
     *
     * @param weatherCode Open-Meteoから取得した天気コード
     * @return 天気コードに対応する天候。対応する値がなければUNKNOWN
     */
    public static WeatherCondition fromCode(int weatherCode) {
        return switch (weatherCode) {

            /*
             * 快晴から曇りまでの天候。
             */
            case 0 -> CLEAR;
            case 1 -> MAINLY_CLEAR;
            case 2 -> PARTLY_CLOUDY;
            case 3 -> CLOUDY;

            /*
             * 通常の霧と、霧の水滴が凍結する着氷性の霧。
             */
            case 45, 48 -> FOG;

            /*
             * 弱いものから強いものまでの霧雨と、
             * 着氷性の霧雨。
             */
            case 51, 53, 55, 56, 57 -> DRIZZLE;

            /*
             * 弱いものから強いものまでの雨と、
             * 着氷性の雨。
             */
            case 61, 63, 65, 66, 67 -> RAIN;

            /*
             * 弱いものから強いものまでの雪と霧雪。
             */
            case 71, 73, 75, 77 -> SNOW;

            /*
             * 弱いものから激しいものまでのにわか雨。
             */
            case 80, 81, 82 -> RAIN_SHOWER;

            /*
             * 弱いまたは強いにわか雪。
             */
            case 85, 86 -> SNOW_SHOWER;

            /*
             * 通常の雷雨と、ひょうを伴う雷雨。
             */
            case 95, 96, 99 -> THUNDERSTORM;

            /*
             * 現在のアプリで対応していないコード。
             */
            default -> UNKNOWN;
        };
    }
}