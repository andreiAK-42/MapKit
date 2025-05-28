package com.example.mapkit

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.directions.driving.*
import com.yandex.runtime.Error


import com.yandex.mapkit.map.*

class MainActivity : AppCompatActivity(), InputListener {
    private var mapView: MapView? = null
    private var zoom: Float = 1f

    private var firstPoint: Point? = null
    private var secondPoint: Point? = null

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

        findViewById<Button>(R.id.btn_delete_route).setOnClickListener {
            resetRoute()
        }


        mapView!!.mapWindow.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )

        mapView!!.mapWindow.map.addInputListener(this)
    }

    override fun onMapTap(map: Map, point: Point) {
    }

    override fun onMapLongTap(map: Map, point: Point) {
        if (firstPoint == null) {
            firstPoint = point
            addPlacemark(point, Color.GREEN, "A")
        } else {
            secondPoint = point
            addPlacemark(point, Color.RED, "B")
            buildRoute()
        }
    }

    private fun addPlacemark(point: Point, color: Int, text: String) {
        mapView!!.mapWindow.map.mapObjects.addPlacemark(point).apply {
            setText(text)
            setIconStyle(
                IconStyle().apply {
                    setScale(1f)
                }
            )
            setUserData(color)
        }
    }

    private fun buildRoute() {
        val start = firstPoint ?: return
        val end = secondPoint ?: return

        val points = buildList {
            add(RequestPoint(start, RequestPointType.WAYPOINT, null, null, null))
            add(RequestPoint(end, RequestPointType.WAYPOINT, null, null, null))
        }

        val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(
            DrivingRouterType.ONLINE
        )

        val drivingOptions = DrivingOptions().apply {
            routesCount = 1
        }

        val vehicleOptions = VehicleOptions()

        val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(routes: List<DrivingRoute>) {
                if (routes.isNotEmpty()) {
                    showRoute(routes[0])
                }
            }

            override fun onDrivingRoutesError(error: Error) {
                Toast.makeText(this@MainActivity,
                    "Ошибка построения маршрута: ${error}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        drivingRouter.requestRoutes(
            points,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener
        )



    }


    private fun showRoute(route: DrivingRoute) {
        mapView!!.mapWindow.map.mapObjects.clear()

        firstPoint?.let { addPlacemark(it, Color.GREEN, "A") }
        secondPoint?.let { addPlacemark(it, Color.RED, "B") }

        mapView!!.mapWindow.map.mapObjects.addPolyline(route.geometry).apply {
            strokeWidth = 5f
            setStrokeColor(Color.BLUE)
        }

    }
    private fun resetRoute() {
        mapView!!.mapWindow.map.mapObjects.clear()
        firstPoint = null
        secondPoint = null
        Toast.makeText(this, "Маршрут сброшен", Toast.LENGTH_SHORT).show()
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