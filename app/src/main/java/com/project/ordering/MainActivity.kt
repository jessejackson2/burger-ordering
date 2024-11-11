package com.project.ordering

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val basePrice = 5 // Base price for the burger
    private var totalPrice = basePrice
    private val channelId = "order_notifications"

    private lateinit var totalTextView: TextView
    private lateinit var lettuceCheckBox: CheckBox
    private lateinit var tomatoCheckBox: CheckBox
    private lateinit var cheeseCheckBox: CheckBox
    private lateinit var baconCheckBox: CheckBox
    private lateinit var onionCheckBox: CheckBox
    private lateinit var picklesCheckBox: CheckBox
    private lateinit var mushroomsCheckBox: CheckBox
    private lateinit var avocadoCheckBox: CheckBox
    private lateinit var imageContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        totalTextView = findViewById(R.id.totalPrice)
        lettuceCheckBox = findViewById(R.id.checkbox_lettuce)
        tomatoCheckBox = findViewById(R.id.checkbox_tomato)
        cheeseCheckBox = findViewById(R.id.checkbox_cheese)
        baconCheckBox = findViewById(R.id.checkbox_bacon)
        onionCheckBox = findViewById(R.id.checkbox_onion)
        picklesCheckBox = findViewById(R.id.checkbox_pickles)
        mushroomsCheckBox = findViewById(R.id.checkbox_mushrooms)
        avocadoCheckBox = findViewById(R.id.checkbox_avocado)
        imageContainer = findViewById(R.id.imageContainer)

        val orderButton: Button = findViewById(R.id.orderButton)
        val clearButton: Button = findViewById(R.id.clearButton)

        orderButton.setOnClickListener { placeOrder() }
        clearButton.setOnClickListener { clearOrder() }

        // Update the total price when any checkbox is checked or unchecked
        lettuceCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        tomatoCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        cheeseCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        baconCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        onionCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        picklesCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        mushroomsCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }
        avocadoCheckBox.setOnCheckedChangeListener { _, _ -> updateTotalPrice() }

        // Check and request notification permission
        checkNotificationPermission()

        createNotificationChannel()
        updateTotalPrice()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the notification permission is granted
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // ActivityResultLauncher for requesting permissions
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Update the total price based on the selected ingredients
    private fun updateTotalPrice() {
        totalPrice = basePrice
        imageContainer.removeAllViews()

        if (lettuceCheckBox.isChecked) {
            totalPrice += 1
            addImageToLayout(R.drawable.lettuce)
        }
        if (tomatoCheckBox.isChecked) {
            totalPrice += 1
            addImageToLayout(R.drawable.tomato)
        }
        if (cheeseCheckBox.isChecked) {
            totalPrice += 2
            addImageToLayout(R.drawable.cheese)
        }
        if (baconCheckBox.isChecked) {
            totalPrice += 3
            addImageToLayout(R.drawable.bacon)
        }
        if (onionCheckBox.isChecked) {
            totalPrice += 1
            addImageToLayout(R.drawable.onion)
        }
        if (picklesCheckBox.isChecked) {
            totalPrice += 1
            addImageToLayout(R.drawable.pickle)
        }
        if (mushroomsCheckBox.isChecked) {
            totalPrice += 2
            addImageToLayout(R.drawable.mushrooms)
        }
        if (avocadoCheckBox.isChecked) {
            totalPrice += 2
            addImageToLayout(R.drawable.avocado)
        }
        totalTextView.text = "Total: £$totalPrice"
    }

    // Add an image to the layout
    private fun addImageToLayout(imageResId: Int) {
        val imageView = ImageView(this)
        imageView.setImageResource(imageResId)
        imageView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        imageContainer.addView(imageView)
    }

    // Place the order and show a notification
    private fun placeOrder() {
        totalTextView.text = "Your order is placed! Total: £$totalPrice"
        Toast.makeText(this, "Your order is taken! You need to pay £$totalPrice Thank you for your order!", Toast.LENGTH_SHORT).show()
        showNotification("Order Placed", "You need to pay £$totalPrice. Thank you for your order!")
    }

    // Clear the order and reset the total price
    private fun clearOrder() {
        lettuceCheckBox.isChecked = false
        tomatoCheckBox.isChecked = false
        cheeseCheckBox.isChecked = false
        baconCheckBox.isChecked = false
        onionCheckBox.isChecked = false
        picklesCheckBox.isChecked = false
        mushroomsCheckBox.isChecked = false
        avocadoCheckBox.isChecked = false
        updateTotalPrice()
    }

    // Show a notification
    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.burgersmall)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    // Create a notification channel for API 26 and above
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Channel for order notifications"
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
