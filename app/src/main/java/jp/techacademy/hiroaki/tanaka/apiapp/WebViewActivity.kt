package jp.techacademy.hiroaki.tanaka.apiapp

import java.io.Serializable
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.activity_web_view.favoriteImageView
import kotlinx.android.synthetic.main.recycler_favorite.*



class WebViewActivity: AppCompatActivity(){

    // 課題追加　ここから
    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }
    // 課題追加　ここまで

    // 一覧画面から登録するときのコールバック（FavoriteFragmentへ通知するメソッド)
    var onClickAddFavorite: ((Shop) -> Unit)? = null
    // 一覧画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null
    // Itemを押したときのメソッド
    var onClickItem: ((String) -> Unit)? = null

    // 課題追加　ここから
//    private var fragmentCallback : FragmentCallback? = null // Fragment -> Activity にFavoriteの変更を通知する
//    // お気に入り画面から削除するときのコールバック（ApiFragmentへ通知するメソッド)
//    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null
//    // Itemを押したときのメソッド
//    var onClickItem: ((String) -> Unit)? = null
    // 課題追加　ここまで

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val shop:Shop = intent.getSerializableExtra(KEY_URL) as Shop
        webView.loadUrl(shop.couponUrls.sp)

        // 登録および解除の更新処理を行う。戻ってきたら、更新されるようにする。Fragmentの状態のタイミング
        val isFavorite = FavoriteShop.findBy(shop.id) != null

        // nameTextViewのtextプロパティに代入されたオブジェクトのnameプロパティを代入
        nameTextView.text = shop.name
        // Picassoライブラリを使い、imageViewにdata.logoImageのurlの画像を読み込ませる
        Picasso.get().load(shop.logoImage).into(imageView)
        // 白抜きの星マークの画像を指定
        favoriteImageView.apply {
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border) // Picassoというライブラリを使ってImageVIewに画像をはめ込む
            setOnClickListener {
                if (isFavorite) {
                    onClickDeleteFavorite?.invoke(shop)
                } else {
                    onClickAddFavorite?.invoke(shop)
                }
            }
        }

//        // 課題追加　ここから
//        onClickDeleteFavorite = { // Adapterの処理をそのままActivityに通知する
//                fragmentCallback?.onDeleteFavorite(it.id)
//        }
//        // Itemをクリックしたとき
//        onClickItem = {
//           fragmentCallback?.onClickItem(it)
//        }
//        // 課題追加　ここまで
    }


    // 課題追加　ここから
    fun onAddFavorite(shop: Shop) { // Favoriteに追加するときのメソッド(Fragment -> Activity へ通知する)
        FavoriteShop.insert(FavoriteShop().apply {
            id = shop.id
            name = shop.name
            imageUrl = shop.logoImage
            url = if (shop.couponUrls.sp.isNotEmpty()) shop.couponUrls.sp else shop.couponUrls.pc
        })
    }

    fun onDeleteFavorite(id: String) { // Favoriteから削除するときのメソッド(Fragment -> Activity へ通知する)
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->}
            .create()
            .show()
    }

    private fun deleteFavorite(id: String) {
        FavoriteShop.delete(id)
    }


    // 課題追加　ここまで

    companion object {
        private const val KEY_URL = "key_url"

        fun start(activity: Activity, shop: Shop) {
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, shop))
        }
    }
}