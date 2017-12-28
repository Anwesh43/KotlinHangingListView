package ui.anwesome.com.kotlinhanginglistview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.hanginglistview.HangingListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view:HangingListView = HangingListView.create(this)
        HangingListView.addText("Hello",view)
        HangingListView.addText("World",view)
        HangingListView.addText("say my name",view)
        HangingListView.addText("No more",view)
        HangingListView.show(this,view)
    }
}
