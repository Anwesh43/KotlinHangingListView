package ui.anwesome.com.hanginglistview

import android.graphics.Paint

/**
 * Created by anweshmishra on 28/12/17.
 */
class TextUtil {
    companion object {
        fun getTrimmedText(text:String,w:Float,paint:Paint):String {
            var msg:StringBuilder = StringBuilder()
            text.forEach {
                if(paint.measureText(msg.toString()+it) > 3*w/4) {
                    return msg.toString()
                }
                else {
                    msg.append(it)
                }
            }
            return msg.toString()
        }
    }
}