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
            startcb()
        }
        fun execute(cb:(Float)->Unit) {
            cb(scale)

        }
        fun update(stopcb: () -> Unit) {
            scale += dir*0.1f
            if(scale > 1) {
                scale = 1f
                dir = 0f
                stopcb()
            }
        }
    }
    data class HangingList(var w:Float,var h:Float,var itemStrings:LinkedList<String>) {
        val items:ConcurrentLinkedQueue<HangingItem> = ConcurrentLinkedQueue()
        val state = HangingItemState()
        var curr:HangingItem?=null
        var prev:HangingItem?=null
        init {
            val gap = w/(2*itemStrings.size+1)
            var x = 3*gap/2
            var i = 0
            itemStrings.forEach {
                items.add(HangingItem(i,it,x,gap/2))
                i++
                x += 2*gap
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            items.forEach {
                if(it == curr) {
                    state.execute {
                        curr?.draw(canvas,paint,h/2,it)
                    }
                }
                else if(it == prev) {
                    state.execute {
                        prev?.draw(canvas,paint,h/2,1-it)
                    }
                }
                else {
                    it.draw(canvas, paint, h / 2, state.scale)
                }
            }
        }
        fun update(stopcb:(Int)->Unit) {
            state.update{
                stopcb(curr?.i?:-1)
                prev = curr
            }
        }
        fun handleTap(x:Float,y:Float,startcb:()->Unit) {
            items.forEach {
                if(it != prev && it.handleTap(x,y)) {
                    curr = it
                    state.startUpdating(startcb)
                    return
                }
            }
        }
    }
}