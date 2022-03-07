package com.example.fastcampus_6

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinuteTextView: TextView by lazy {
        findViewById(R.id.remainMinuteTextView)
    }

    private val remainSecondTextView: TextView by lazy {
        findViewById(R.id.remainSecondTextView)
    }

    private val soundPool = SoundPool.Builder().build()

    private var tickingSoundID : Int? = null

    private var bellSoundID : Int? = null

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    //메모리에 올라간 사운드 파일들은 비용이 크기 때문에(메모리를 많이 차지하기 때문에) 꼭 해제를 해줘야 한다
    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private var currentCountDownTimer : CountDownTimer? = null

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                    //updateSeekBar가 동작을 하면서 onProgressChanged를 호출하게 됨
                    //코드상에서 변경하는 것인지 유저가 변경 하는 것인지 확실히 해줘야 함
                    if(fromUser){
                        updateRemainTime(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    seekBar ?: return //seekBar가 null일 때는 실행을 아예 x

                    if(seekBar.progress == 0){
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }
            }
        )
    }

    private fun initSounds() {
        tickingSoundID = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundID = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(p0: Long) {
                updateRemainTime(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()

        tickingSoundID?.let {
            soundPool.play(it, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundID?.let {
            soundPool.play(it, 1F, 1F, 0, 0, 1F)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMills : Long) {
        val remainSecond = remainMills / 1000

        remainMinuteTextView.text = "%02d'".format(remainSecond / 60)
        remainSecondTextView.text = "%02d".format(remainSecond % 60)
    }

    private fun updateSeekBar(remainMills : Long) {
        seekBar.progress = (remainMills / 1000 / 60).toInt()
    }

}