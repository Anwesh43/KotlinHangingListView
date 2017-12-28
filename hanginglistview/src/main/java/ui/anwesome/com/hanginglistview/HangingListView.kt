package ui.anwesome.com.hanginglistview

/**
 * Created by anweshmishra on 28/12/17.
 */
import android.content.*
import android.view.*
import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue
import android.graphics.*
class HangingListView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val texts:LinkedList<String> = LinkedList()
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class HangingItem(var i:Int,var text:String,var x:Float,var r:Float,var y:Float = r/2,var oy:Float = y) {
        fun draw(canvas:Canvas,paint:Paint,maxY:Float,scale:Float) {
            y = oy + maxY*scale
            canvas.save()
            canvas.translate(x,oy)
            canvas.drawCircle(0f,y-oy,r,paint)
            canvas.drawLine(0f,0f,0f,y-oy,paint)
            canvas.restore()
        }
        fun handleTap(x:Float,y:Float):Boolean = x>=this.x-r && x<=this.x+r && y>=this.oy-r && y<=this.oy+r
    }
    data class HangingItemState(var scale:Float = 0f,var dir:Float = 0f) {
        fun startUpdating(startcb:()->Unit) {
            dir = 1f
            scale = 0f
        }
        fun execute(cb:(Float)->Unit) {
            cb(scale)
        }
        fun update() {
            scale += dir*0.1f
            if(scale > 1) {
                scale = 1f
                dir = 0f
            }
        }
    }
}