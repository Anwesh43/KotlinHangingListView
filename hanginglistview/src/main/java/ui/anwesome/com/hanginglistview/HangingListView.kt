package ui.anwesome.com.hanginglistview

/**
 * Created by anweshmishra on 28/12/17.
 */
import android.app.Activity
import android.content.*
import android.view.*
import java.util.LinkedList
import java.util.concurrent.ConcurrentLinkedQueue
import android.graphics.*
class HangingListView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val texts:LinkedList<String> = LinkedList()
    var hangingSelectionListener:HangingListSelectionListener ?= null
    val renderer = HangingListRenderer(this)
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas,paint)
    }
    private fun addText(text:String) {
        texts.add(text)
    }
    fun addSelectionListener(selectionListener:(String)->Unit) {
        hangingSelectionListener = HangingListSelectionListener(selectionListener)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap(event.x,event.y)
            }
        }
        return true
    }
    data class HangingItem(var i:Int,var text:String,var x:Float,var r:Float,var y:Float = r/2,var oy:Float = y) {
        fun draw(canvas:Canvas,paint:Paint,maxY:Float,scale:Float) {
            y = oy + maxY*scale
            canvas.save()
            canvas.translate(x,oy)
            paint.color = Color.parseColor("#ef5350")
            canvas.drawCircle(0f,y-oy,r,paint)
            canvas.drawLine(0f,0f,0f,y-oy,paint)
            paint.textSize = 2*r/5
            paint.color = Color.WHITE
            val trimmedText = TextUtil.getTrimmedText(text,2*r,paint)
            canvas.drawText(trimmedText,-paint.measureText(trimmedText)/2,y-oy,paint)
            canvas.restore()
        }
        fun handleTap(x:Float,y:Float):Boolean = x>=this.x-r && x<=this.x+r && y>=this.oy-r && y<=this.oy+r
    }
    data class HangingItemState(private var scale:Float = 0f,var dir:Float = 0f) {
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
                items.add(HangingItem(i,it,x,3*gap/4))
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
                    it.draw(canvas, paint, h / 2, 0f)
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
    data class HangingListRenderer(var view:HangingListView,var time:Int = 0) {
        var hangingList:HangingList?=null
        val animator = HangingViewAnimator(view)
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                hangingList = HangingList(w,h,view.texts)
                paint.strokeWidth = Math.min(w,h)/40
                paint.strokeCap = Paint.Cap.ROUND
            }
            canvas.drawColor(Color.parseColor("#212121"))
            hangingList?.draw(canvas,paint)
            animator.update{
                hangingList?.update{index ->
                    view.hangingSelectionListener?.selectionListener?.invoke(view.texts[index])
                    animator.stop()
                }
            }
            time++
        }
        fun handleTap(x:Float,y:Float) {
            hangingList?.handleTap(x,y,{
                animator.startUpdating()
            })
        }
    }
    data class HangingViewAnimator(var view:HangingListView,var animated:Boolean = false) {
        fun update(updatecb:()->Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun startUpdating() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    companion object {

        var isShown = false
        fun create(activity:Activity):HangingListView {
            var view:HangingListView = HangingListView(activity)
            return view
        }
        fun addText(text:String,view:HangingListView) {
            view.addText(text)
        }
        fun show(activity: Activity,view:HangingListView) {
            activity.setContentView(view)
        }
    }
    data class HangingListSelectionListener(var selectionListener:(String)->Unit)
}