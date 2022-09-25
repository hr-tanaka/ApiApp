package jp.techacademy.hiroaki.tanaka.apiapp

    import io.realm.Realm
    import io.realm.RealmConfiguration
    import io.realm.RealmObject
    import io.realm.annotations.PrimaryKey

    open class FavoriteShop: RealmObject() {
        @PrimaryKey
        var id: String = ""
        var imageUrl: String = ""
        var name: String = ""
        var url: String = ""



        companion object {

            // ★ Realmsデータベースに関わる不具合対応　田中　ここから
            var realmConfiguration =
                RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .allowWritesOnUiThread(true)
                    .allowQueriesOnUiThread(true)
                    .build()

            // ★ ここまで

            fun findAll(): List<FavoriteShop> = // お気に入りのShopを全件取得
                // ★ Realmsデータベースに関わる不具合対応　田中
                // Realm.getDefaultInstance().use { realm ->
                Realm.getInstance(realmConfiguration).use { realm ->
                    realm.where(FavoriteShop::class.java)
                        .findAll().let {
                            realm.copyFromRealm(it)
                        }
                }

            fun findBy(id: String): FavoriteShop? = // お気に入りされているShopをidで検索して返す。お気に入りに登録されていなければnullで返す
                // ★ Realmsデータベースに関わる不具合対応　田中
                // Realm.getDefaultInstance().use { realm ->
                Realm.getInstance(realmConfiguration).use { realm ->
                    realm.where(FavoriteShop::class.java)
                        .equalTo(FavoriteShop::id.name, id)
                        .findFirst()?.let {
                            realm.copyFromRealm(it)
                        }
                }

            fun insert(favoriteShop: FavoriteShop) = // お気に入り追加
                // ★ Realmsデータベースに関わる不具合対応　田中
                // Realm.getDefaultInstance().executeTransaction {
                Realm.getInstance(realmConfiguration).executeTransaction {
                    it.insertOrUpdate(favoriteShop)
                }

            fun delete(id: String) = // idでお気に入りから削除する
                // ★ Realmsデータベースに関わる不具合対応　田中
                // Realm.getDefaultInstance().use { realm ->
                Realm.getInstance(realmConfiguration).use { realm ->
                    realm.where(FavoriteShop::class.java)
                        .equalTo(FavoriteShop::id.name, id)
                        .findFirst()?.also { deleteShop ->
                            realm.executeTransaction {
                                deleteShop.deleteFromRealm()
                            }
                        }
                }
        }
    }