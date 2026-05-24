package com.openttd.roadmvp.core

enum class RoadVehicleType {
    BUS,
    TRUCK,
    CAR
}

class RoadVehicle(
    var x: Float,
    var y: Float,
    val type: RoadVehicleType,
    var speed: Float = 100f, // pixels por segundo
    var direction: Float = 0f // em graus
) {
    private var distanceTraveled = 0f
    
    fun update(delta: Float) {
        // Movimento simples para demonstração
        // No MVP completo, isso seguiria as estradas definidas
        
        val movement = speed * delta
        distanceTraveled += movement
        
        // Mover na direção atual
        x += kotlin.math.cos(Math.toRadians(direction.toDouble())).toFloat() * movement
        y += kotlin.math.sin(Math.toRadians(direction.toDouble())).toFloat() * movement
        
        // Limites da tela - simples wrap-around para demonstração
        if (x > 1100f) x = -50f
        if (x < -50f) x = 1100f
        if (y > 850f) y = -50f
        if (y < -50f) y = 850f
    }
}

class RoadSegment(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float
) {
    val length: Float
        get() {
            val dx = endX - startX
            val dy = endY - startY
            return kotlin.math.sqrt(dx * dx + dy * dy)
        }
}
