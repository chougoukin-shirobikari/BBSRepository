# WEBアプリケーションBBS(多機能掲示板)について

## 機能一覧
### ユーザー登録・ログイン機能
### 投稿機能
* 画像投稿機能
* コメント機能
### 投稿検索機能
* 複数のキーワードで検索可
* 条件に一致した部分をハイライト表示
### ページネーション機能
### お問い合わせ機能
### メール機能
* お問い合わせがあった場合・投稿件数が制限に達した場合は管理人に通知
### 管理機能
* 投稿・コメントを検索
* NGワードの登録
* ユーザーの一覧表示
    * ユーザー名で検索可
    * 長期間投稿のないユーザーの表示
* お問い合わせの一覧表示

## 使用技術
* java11
* JUnit5
* springboot2.5
* javascript
    * JQuery
* MySQL8.0
* Nginx
* Tomcat9.0
* AWS
    * ELB
    * Route53
    * VPC
    * EC2
    * RDS
    * S3

## テスト
* 単体テスト
* 結合テスト

## インフラ構成図
![aws](https://user-images.githubusercontent.com/91199128/144392129-621d577c-9e0d-4d3c-8239-c16d0ba41050.png)

## ER図
![er4](https://user-images.githubusercontent.com/91199128/144403319-91ed2b26-39d4-4f11-bc8c-60922f12ffa7.png)

