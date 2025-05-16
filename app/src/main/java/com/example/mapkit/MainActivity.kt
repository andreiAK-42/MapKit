package com.example.mapkit

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


class MainActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var zoom: Float = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("c0fef72a-a707-44c2-bdbf-e2385fa95a0a")
        setContentView(R.layout.activity_main)
        MapKitFactory.initialize(this)
        mapView = findViewById<MapView>(R.id.mapV_main)


        findViewById<Button>(R.id.btn_post_plus).setOnClickListener {
            zoom += 1f
            mapView!!.getMap().move(
                CameraPosition(
                    Point(55.354993, 86.085805), zoom, 0.0f, 0.0f
                )
            )
        }

        findViewById<Button>(R.id.btn_post_minus).setOnClickListener {
            zoom -= 1f
            mapView!!.getMap().move(
                CameraPosition(
                    Point(55.354993, 86.085805), zoom, 0.0f, 0.0f
                )
            )

        }

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}