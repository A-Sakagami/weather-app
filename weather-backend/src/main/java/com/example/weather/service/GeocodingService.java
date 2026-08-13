package com.example.weather.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.weather.client.GeocodingClient;
import com.example.weather.exception.CityNotFoundException;
import com.example.weather.model.GeocodingResponse;

/**
 * 都市名から位置情報を検索し、利用する都市候補を決定するService。
 *
 * <p>
 * {@link GeocodingClient}を使って外部のGeocoding APIを呼び出し、
 * 都市名、緯度、経度、都道府県、国、タイムゾーンなどを取得する。
 *
 * <p>
 * 外部APIへの通信自体はGeocodingClientへ任せ、
 * このクラスでは次のようなアプリ固有の判断を担当する。
 *
 * <p>
 * ・都市名が入力されているか確認する<br>
 * ・「北見」を「北見市」のように補完する<br>
 * ・国名が指定された場合は、その国の候補を選択する<br>
 * ・国名が指定されていない場合は、日本の候補を優先する<br>
 * ・条件に合う都市がない場合は例外を発生させる
 *
 * <p>
 * 外部APIとの通信と候補を選ぶ処理を分離することで、
 * 検索条件や候補の選択方法を変更しやすくしている。
 */
@Service
public class GeocodingService {

        /**
         * 都市名の再検索に使用する行政区分。
         *
         * <p>
         * 最初の検索で候補が見つからなかった場合に、
         * 「市」「区」「町」「村」の順で都市名の末尾へ追加する。
         *
         * <p>
         * 例えば「北見」で結果が得られなければ、
         * 「北見市」「北見区」「北見町」「北見村」の順で検索する。
         */
        private static final List<String> ADMINISTRATIVE_SUFFIXES = List.of("市", "区", "町", "村");

        /**
         * Geocoding APIの検索結果を使用せず、
         * 固定の位置情報を使用する市町村。
         *
         * <p>
         * 市町村名によってGeocoding APIが意図しない地点を返す場合に、
         * 正しい市町村名、緯度、経度などをあらかじめ登録して使用する。
         */
        private static final List<ExceptionalMunicipality> EXCEPTIONAL_MUNICIPALITIES =
                List.of(
                        new ExceptionalMunicipality("美幌",43.823928, 144.107242,"日本","北海道","Asia/Tokyo"),
                        new ExceptionalMunicipality("津別",43.706267, 144.025253,"日本","北海道","Asia/Tokyo")
                );

        /**
         * Geocoding APIの通常検索を使用しない市町村の位置情報。
         *
         * @param name      画面へ表示する正式な市町村名
         * @param latitude  緯度
         * @param longitude 経度
         * @param admin1    都道府県名
         * @param country   国名
         * @param timezone  タイムゾーン
         */
        private record ExceptionalMunicipality(
                String name,
                double latitude,
                double longitude,
                String country,
                String admin1,
                String timezone
        ) {
        }

        /**
        * Geocoding APIとの通信を担当するClient。
        */
        private final GeocodingClient geocodingClient;

        /**
         * GeocodingServiceを生成する。
         *
         * <p>Springが管理しているGeocodingClientのインスタンスが、
         * コンストラクタの引数へ自動的に渡される。
         *
         * <p>GeocodingService自身がClientを生成しないため、
         * 外部APIとの通信部分を交換したり、
         * テスト用のClientへ置き換えたりしやすくなる。
         *
         * @param geocodingClient Geocoding APIとの通信を担当するClient
         */
        public GeocodingService(GeocodingClient geocodingClient) {
                this.geocodingClient = geocodingClient;
        }

        /**
         * 都市名を検索し、条件に合う都市を1件返す。
         *
         * <p>都市名を正規化した後、必要に応じて行政区分を補完して検索する。
         * 複数の候補が返された場合は、国名の指定に従って候補を選択する。
         *
         * <p>国名が指定されている場合は、その国に一致する最初の候補を返す。
         * 国名が指定されていない場合は日本の候補を優先し、
         * 日本の候補がなければ検索結果の先頭を返す。
         *
         * @param cityName 検索する都市名
         * @param country  検索対象の国名。指定されていない場合はnull
         * @return 条件に合う都市の位置情報
         * @throws IllegalArgumentException 都市名がnullまたは空白の場合
         * @throws CityNotFoundException     都市または指定した国の候補が見つからない場合
         */
        public GeocodingResponse.Result searchCity(String cityName, String country) {
                /*
                * 都市名が入力されているか確認する。
                *
                * isBlank()は空文字だけでなく、
                * 半角・全角スペースなど空白だけの文字列も検出する。
                */
                if (cityName == null || cityName.isBlank()) {
                        throw new IllegalArgumentException(
                                        "都市名を入力してください");
                }
                /*
                * 都市名の前後に入力された不要な空白を取り除く。
                *
                * 以降の処理で同じ正規化済みの都市名を使えるよう、
                * normalizedCityNameとして保持する。
                */
                String normalizedCityName = cityName.trim();

                /*
                * Geocoding APIでは正しい地点を取得できない
                * 例外市町村に該当するか確認する。
                *
                * 該当する場合はAPI検索を行わず、
                * あらかじめ登録した位置情報を使用する。
                */
                ExceptionalMunicipality exceptionalMunicipality =
                        findExceptionalMunicipality(normalizedCityName);

                if (exceptionalMunicipality != null) {
                        return new GeocodingResponse.Result(
                                exceptionalMunicipality.name(),
                                exceptionalMunicipality.latitude(),
                                exceptionalMunicipality.longitude(),
                                exceptionalMunicipality.country(),
                                exceptionalMunicipality.admin1(),
                                exceptionalMunicipality.timezone()
                        );
                }

                /*
                * 都市名をGeocoding APIで検索する。
                *
                * 最初の検索で見つからなかった場合の
                * 「市」「区」「町」「村」の補完は、
                * searchWithAdministrativeSuffix内で行われる。
                */
                GeocodingResponse response = searchWithAdministrativeSuffix(normalizedCityName);

                /*
                * APIからレスポンスを取得できなかった場合や、
                * 検索結果が1件もなかった場合は処理を続けられない。
                *
                * NullPointerExceptionを防ぎ、都市が見つからなかったことを
                * アプリ固有の例外として明示する。
                */     
                if (response == null
                                || response.results() == null
                                || response.results().isEmpty()) {
                        throw new CityNotFoundException(cityName);
                }

                List<GeocodingResponse.Result> results = response.results();

                /*
                * 国名が指定されている場合は、
                * その国に一致する候補だけを対象にする。
                *
                * equalsIgnoreCase()を使うことで、
                * JapanとJAPANのような英字の大文字・小文字の違いを無視する。
                */
                if (country != null && !country.isBlank()) {
                        return results.stream()
                                        .filter(result -> country.equalsIgnoreCase(result.country()))
                                        .findFirst()
                                        .orElseThrow(() -> new CityNotFoundException(
                                                        cityName + "（" + country + "）"));
                }

                /*
                * 国名が指定されていない場合は、
                * 同名の海外都市より日本の都市を優先する。
                *
                * 日本の候補が含まれていなければ、
                * Geocoding APIが返した先頭の候補を使用する。
                *
                * resultsが空でないことは事前に確認済みのため、
                * getFirst()を安全に呼び出せる。
                */
                return results.stream()
                                .filter(result -> "日本".equals(result.country()))
                                .findFirst()
                                .orElse(results.getFirst() /* =get(0) */);
        }

        /**
         * 都市名を検索し、必要な場合は行政区分を補って再検索する。
         *
         * <p>最初の検索結果を優先することで、
         * 「網走」のように補完しなくても検索できる都市の結果を変えない。
         *
         * @param cityName 前後の空白を除去した都市名
         * @return 最初に候補が見つかった検索レスポンス
         */
        private GeocodingResponse searchWithAdministrativeSuffix(
                        String cityName) {
                GeocodingResponse response = geocodingClient.search(cityName);

                // 最初の検索で候補があれば、その結果をそのまま使用する
                if (hasResults(response)) {
                        return response;
                }

                // すでに行政区分まで入力されている場合は補完しない
                if (hasAdministrativeSuffix(cityName)) {
                        return response;
                }

                for (String suffix : ADMINISTRATIVE_SUFFIXES) {
                        GeocodingResponse retryResponse = geocodingClient.search(cityName + suffix);

                        if (hasResults(retryResponse)) {
                                return retryResponse;
                        }
                }

                return response;
        }

        /**
         * Geocoding APIのレスポンスに検索結果が含まれているか確認する。
         *
         * <p>レスポンス、results、リストの内容を順番に確認することで、
         * APIから想定外のレスポンスが返された場合の
         * NullPointerExceptionを防ぐ。
         *
         * @param response 確認するGeocoding APIのレスポンス
         * @return 1件以上の検索結果があればtrue
         */
        private boolean hasResults(GeocodingResponse response) {
                return response != null
                                && response.results() != null
                                && !response.results().isEmpty();
        }

        /**
         * 都市名の末尾に行政区分が付いているか確認する。
         *
         * <p>ADMINISTRATIVE_SUFFIXESを順番に調べ、
         * 「市」「区」「町」「村」のいずれかで終わればtrueを返す。
         *
         * @param cityName 確認する都市名
         * @return 行政区分がすでに付いていればtrue
         */
        private boolean hasAdministrativeSuffix(String cityName) {
                return ADMINISTRATIVE_SUFFIXES.stream()
                                .anyMatch(cityName::endsWith);
        }

        /**
         * 入力された都市名が例外市町村として登録されているか検索する。
         *
         * <p>
         * 「○○」と「○○市」の両方を同じ市町村として扱えるよう、
         * 登録された正式名称から行政区分を除いた名称とも比較する。
         *
         * @param cityName 前後の空白を除去した都市名
         * @return 該当する例外市町村。存在しない場合はnull
         */
        private ExceptionalMunicipality findExceptionalMunicipality(
                String cityName
        ) {
                return EXCEPTIONAL_MUNICIPALITIES.stream()
                        .filter(municipality ->
                                municipality.name().equals(cityName)
                                        || removeAdministrativeSuffix(
                                                municipality.name()
                                        ).equals(cityName)
                        )
                        .findFirst()
                        .orElse(null);
        }

        /**
         * 市町村名の末尾から行政区分を取り除く。
         *
         * <p>
         * 例えば「北見市」は「北見」、
         * 「○○町」は「○○」として返す。
         *
         * @param cityName 市町村名
         * @return 行政区分を取り除いた市町村名
         */
        private String removeAdministrativeSuffix(String cityName) {
                for (String suffix : ADMINISTRATIVE_SUFFIXES) {
                        if (cityName.endsWith(suffix)) {
                                return cityName.substring(
                                        0,
                                        cityName.length() - suffix.length()
                                );
                        }
                }

                return cityName;
        }
}
