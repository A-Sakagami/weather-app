# Weather App

Java、Spring Boot、React、AWSを使用して作成したシンプルな天気検索アプリケーションです。

## 構成

- `backend`: Java / Spring Boot / Maven
- `frontend`: React / TypeScript / Vite

## 開発状況

- [x] Gitリポジトリ作成
- [x] Spring Bootプロジェクト作成
- [x] Reactプロジェクト作成
- [x] フロントエンドとバックエンドの接続
- [x] 天気APIとの接続
- [x] Github Actionsを用いたテスト自動化
- [x] Github Releasesを用いてリリース
- [x] AWSへのデプロイ
- [ ] CI/CD自動化
 
## 仕様
- 都市名を入力して天気情報を取得します。
    - 天気・気温（摂氏）・風速（m/s）・現地の時刻を表示します。
- 検索履歴を最大5件保持し、再検索できます。
