package substratum.hidex.svandasek.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.scottyab.rootbeer.RootBeer
import kotlinx.android.synthetic.main.activity_main.*
import substratum.hidex.svandasek.R
import java.util.*


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest
    private lateinit var adView: AdView

    val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = adView.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
        supportActionBar!!.hide()

        //  ADS
        MobileAds.initialize(this)

        adRequest = AdRequest.Builder().build()

        loadBanner()
        loadAd()

        subs.setOnClickListener {
            if (isPackageInstalled("projekt.substratum", packageManager) && !isPackageInstalled("projekt.substratum.lite", packageManager)) {
                val launchIntent = packageManager.getLaunchIntentForPackage("projekt.substratum")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
            } else if (!isPackageInstalled("projekt.substratum", packageManager) && isPackageInstalled("projekt.substratum.lite", packageManager)) {
                val launchIntent = packageManager.getLaunchIntentForPackage("projekt.substratum.lite")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
            } else if (isPackageInstalled("projekt.substratum", packageManager) && isPackageInstalled("projekt.substratum.lite", packageManager)) {
                val launchIntent = packageManager.getLaunchIntentForPackage("projekt.substratum")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
            } else {
                val launchIntent = packageManager.getLaunchIntentForPackage("projekt.substratum")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                }
            }
        }

        rightcontainer.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://play.google.com/store/apps/details?id=projekt.substratum&hl=en_US&gl=US")
            startActivity(i)

        }
        contact.setOnClickListener {
            loadAd()
        }


        tgprivate.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://t.me/svandasek")
            startActivity(i)
        }
        mail.setOnClickListener {
            val mIntent = Intent(Intent.ACTION_SEND)
            /*To send an email you need to specify mailto: as URI using setData() method
            and data type will be to text/plain using setType() method*/
            mIntent.data = Uri.parse("mailto:jan@svanda.me")
            mIntent.type = "text/plain"
            // put recipient email in intent
            mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("jan@svanda.me"))
            //put the Subject in the intent
            mIntent.putExtra(Intent.EXTRA_SUBJECT, "Question - Signal & WiFi Icons Lite")
            mIntent.type = "message/rfc822"
            //put the message in the intent
            val api = android.os.Build.VERSION.SDK      // API Level
            val device = android.os.Build.DEVICE           // Device
            val model = android.os.Build.MODEL            // Model
            val all = "API: $api\n$device\n$model\n"

            mIntent.putExtra(Intent.EXTRA_TEXT, all)
            startActivity(mIntent)
        }
        rate.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=substratum.hidex.svandasek")))
        }

        val rootBeer = RootBeer(this)
        if (!rootBeer.isRooted) {
            notRooted()
        }

        if (
                isPackageInstalled("projekt.substratum", packageManager) ||
                isPackageInstalled("projekt.substratum.lite", packageManager) ||
                isPackageInstalled("projekt.substratum.debug", packageManager) ||
                isPackageInstalled("projekt.themer", packageManager)
        ) {
            subsFound()
        }

        val date = Date()
        val cal = Calendar.getInstance()
        cal.time = date
        val hour = cal[Calendar.HOUR_OF_DAY]
        val greeting: String

        greeting = when (hour) {
            in 12..16 -> {
                "Good Afternoon"
            }
            in 17..20 -> {
                "Good Evening"
            }
            in 21..23 -> {
                "Good Night"
            }
            else -> {
                "Good Morning"
            }
        }
        greeting_txt.text = greeting
    }

    private fun notRooted() {
        root_desc.setText("Device not rooted")
        root_desc.setTextColor(Color.RED)
    }

    private fun subsFound() {
        subs_text.setText("Substratum found")
        subs_text.setTextColor(resources.getColor(R.color.green))
        rightcontainer.isClickable = false
    }

    private fun isPackageInstalled(packagename: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageGids(packagename)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun loadAd() {
        applicationContext?.let {
            InterstitialAd.load(it,resources.getString(R.string.interstitial_id), adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("ADss", adError.message)
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d("ADss", "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    if (mInterstitialAd != null) {
                        Thread.sleep(800)
                        this@MainActivity?.let { mInterstitialAd!!.show(it)
                            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {
                                    Log.d("ADss", "Ad was dismissed.")
                                }

                                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                    Log.d("ADss", "Ad failed to show.")
                                }

                                override fun onAdShowedFullScreenContent() {
                                    Log.d("ADss", "Ad showed fullscreen content.")
                                    mInterstitialAd = null;
                                }
                            }}
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }
                }
            })
        }
    }

    private fun loadBanner(){
        val adRequest = AdRequest.Builder().build()
        adView = AdView(this)
        ad_view_home.addView(adView)
        //adView = ad_view_home
        adView.adUnitId = resources.getString(R.string.banner_id)
        adView.adSize = AdSize.SMART_BANNER
        adView.loadAd(adRequest)
        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                ad_Cw.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

    }
}


