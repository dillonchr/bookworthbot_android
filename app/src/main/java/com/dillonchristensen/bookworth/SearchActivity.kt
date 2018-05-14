package com.dillonchristensen.bookworth

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*
import java.net.URL
import java.net.URLEncoder

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        this.resetButton.setOnClickListener { _ ->
            this.clearInputs()
        }
        this.searchButton.setOnClickListener { view ->
            if (this.titleInput.text.isNullOrEmpty() || this.authorInput.text.isNullOrEmpty() || this.publisherInput.text.isNullOrEmpty()) {
                Snackbar.make(view, "Fill in each field", Snackbar.LENGTH_LONG).show()
            } else {
                this.search()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun clearInputs() {
        this.titleInput.setText("")
        this.authorInput.setText("")
        this.publisherInput.setText("")
        this.avgPrice.text = ""
        this.conf.text = ""
        this.results.text = ""
        this.titleInput.requestFocus()
    }

    fun search() {
        //[shouldBuy, abe, ebaySold, ebayLive, etsy, avg, conf]
        val title = URLEncoder.encode(this.titleInput.text.toString(), "utf-8")
        val author = URLEncoder.encode(this.authorInput.text.toString(), "utf-8")
        val publisher = URLEncoder.encode(this.publisherInput.text.toString(), "utf-8")
        this.progressBar.visibility = View.VISIBLE
        Thread({
            val response = URL("http://bot.bookworth.net/?title=$title&author=$author&publisher=$publisher").readText();
            val results = response.substring(1, response.length - 1).split(",")
            val shouldBuy = results[0].toInt()
            val themeColor:Int
            when (shouldBuy) {
                1 ->
                    themeColor = 0x16A085
                2 ->
                    themeColor = 0xFFE53B
                else ->
                    themeColor = 0xFF0000
            }
            val abe = results[1]
            val ebaySold = results[2]
            val ebayLive = results[3]
            val etsy = results[4]
            val avgPrice = results[5]
            val conf = results[6]

            runOnUiThread({
                this.avgPrice.text = "\$$avgPrice"
                this.conf.text = "~ $conf %"
                this.results.text = """Abe: $$abe
ebay (Sold): $$ebaySold
ebay (Live): $$ebayLive
Etsy: $$etsy
"""
                this.toolbar.setBackgroundColor(themeColor)
                this.progressBar.visibility = View.INVISIBLE
            })
        }).start()
    }
}
