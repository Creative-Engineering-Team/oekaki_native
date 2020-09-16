package com.example.chatkotlin.Room

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Button
import com.example.chatkotlin.Board.*
import com.example.chatkotlin.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_another_board.*
import kotlinx.android.synthetic.main.activity_board.*
import kotlinx.android.synthetic.main.activity_room_main.*
import kotlin.system.exitProcess

class RoomMainActivity : AppCompatActivity() {
    var i: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_main)

        val room_id = intent.getStringExtra("room_id")//room_id: room_1


        setContentView(R.layout.activity_board)
        val customSurfaceView = CustomSurfaceView(this, surfaceView_write)

        val btn: Button = findViewById(R.id.btn_to_another_board_activity)

        i = 0
//        val job = surface_write_fun(customSurfaceView,i)
//        job.cancel
        surface_watch_fun(customSurfaceView)

        btn.setOnClickListener{
            if(i%2==1){
                i = i + 1
                surface_write_fun(customSurfaceView)
                Log.d("bbb","write${i.toString()}")

//                Log.d("num",surface_write_fun(customSurfaceView,i).toString())
            }else if(i%2==0){
                i = i + 1
//                surface_write_fun(customSurfaceView,i)
//                surface_watch_fun(customSurfaceView)
                customSurfaceView.setOnTouchListener { v, event ->
                    customSurfaceView.onTouch_watch(event)
                }
                Log.d("num",i.toString())
            }
        }



//        val btn_write: Button = findViewById(R.id.btn_main_to_write)
//        btn_write.setOnClickListener {
//            val intent_write = Intent(this, BoardActivity::class.java)
//            intent_write.putExtra("room_id", room_id)//room_id: room_1
//            startActivity(intent_write)
//        }
//
//        val btn_watch: Button = findViewById(R.id.btn_main_to_watch)
//        btn_watch.setOnClickListener {
//            val intent_watch = Intent(this, AnotherBoardActivity::class.java)
//            intent_watch.putExtra("room_id", room_id)//room_id: room_1
//            startActivity(intent_watch)
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun surface_write_fun(customSurfaceView_write: CustomSurfaceView){

        //リッスンの終了
//        val ref_draw = FirebaseDatabase.getInstance().getReference("draw")
//        ref_draw.child("draw_up/x").onDisconnect()
//        ref_draw.child("draw_up/y").onDisconnect()
//        ref_draw.child("draw_move/x").onDisconnect()
//        ref_draw.child("draw_move/y").onDisconnect()
//        ref_draw.child("draw_down/x").onDisconnect()
//        ref_draw.child("draw_down/y").onDisconnect()


        /// CustomSurfaceViewのインスタンスを生成しonTouchリスナーをセット
        surfaceView_write.setOnTouchListener { v, event ->
            customSurfaceView_write.onTouch(event)
        }
        /// カラーチェンジボタンにリスナーをセット
        /// CustomSurfaceViewのchangeColorメソッドを呼び出す
        blackBtn.setOnClickListener {
            customSurfaceView_write.changeColor("black")
        }
        redBtn.setOnClickListener {
            customSurfaceView_write.changeColor("red")
        }
        greenBtn.setOnClickListener {
            customSurfaceView_write.changeColor("green")
        }

        /// リセットボタン
        btn_board_reset.setOnClickListener {
            customSurfaceView_write.reset()
        }

    }

    fun surface_watch_fun(customSurfaceView_read: CustomSurfaceView){
        Log.d("watch","massage")

//        描画の停止
        surfaceView_write.setOnTouchListener { v, event ->
            customSurfaceView_read.onTouch_watch(event)
        }

        Log.d("bbb" , i.toString())
        val pass_down = FirebaseDatabase.getInstance().getReference("draw/draw_down")
        pass_down.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("bbb" , "read----")
                val draw_down = snapshot.getValue(Draw_data::class.java)
                val x_string: String = draw_down?.x.toString()
                val y_string = draw_down?.y.toString()

                //string からfloatに変換
                val x: Float = x_string.toFloat()
                val y: Float = y_string.toFloat()

                Log.d("firebase", "down")
                if(i%2 == 1){
                    customSurfaceView_read.touchDown_watch(x, y)
                    Log.d("bbb" , i.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_move = FirebaseDatabase.getInstance().getReference("/draw/draw_move")
        pass_move.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down = snapshot.getValue(Draw_data::class.java)
                val x_string: String = draw_down?.x.toString()
                val y_string = draw_down?.y.toString()

                if (x_string != "" && y_string != "") {
                    //string からfloatに変換
                    val x: Float = x_string.toFloat()
                    val y: Float = y_string.toFloat()
                    if(i%2 == 1){
                        customSurfaceView_read.touchMove_watch(x, y)
                    }
                }

                Log.d("firebase", "move")
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        val pass_up = FirebaseDatabase.getInstance().getReference("/draw/draw_up")
        pass_up.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val draw_down = snapshot.getValue(Draw_data::class.java)
                val x_string: String = draw_down?.x.toString()
                val y_string = draw_down?.y.toString()

                //string からfloatに変換
                val x: Float = x_string.toFloat()
                val y: Float = y_string.toFloat()

                Log.d("firebase", "up")
                if(i%2 == 1){
                    customSurfaceView_read.touchUp_watch(x, y)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        // 色を変えた場合の処理
        val color_ref = FirebaseDatabase.getInstance().getReference("/draw/btn")
        color_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val btn_ref = snapshot.getValue(Button_board::class.java)
                val selectedcolor = btn_ref?.color
                customSurfaceView_read.changeColor_watch(selectedcolor!!)
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

        // リセットボタンの処理
        val reset_ref = FirebaseDatabase.getInstance().getReference("/draw/btn")
        reset_ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val btn_ref = snapshot.getValue(Button_board::class.java)
                if (btn_ref?.reset == "reset") {
                    customSurfaceView_read.reset_watch()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //エラー処理
            }
        })

    }



}