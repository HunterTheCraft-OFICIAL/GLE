package io.hunterthecraft.gle.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.Viewport
import com.badlogic.gdx.utils.viewport.FitViewport

class RoadScreen(private val game: RoadMVPGame) : ScreenAdapter() {

    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: Viewport
    private lateinit var font: BitmapFont

    // MVP - Modal Rodoviário: elementos básicos para transporte rodoviário
    private val vehicles = mutableListOf<RoadVehicle>()
    private val roads = mutableListOf<RoadSegment>()

    override fun show() {
        camera = OrthographicCamera()
        viewport = FitViewport(1024f, 768f, camera)

        font = BitmapFont()
        font.data.scale(1.5f)

        // Inicializar com alguns veículos e estradas para demonstração
        initializeRoadNetwork()
        initializeVehicles()

        camera.position.set(viewport.worldWidth / 2, viewport.worldHeight / 2, 0f)
        camera.update()
    }

    private fun initializeRoadNetwork() {
        // Criar segmentos de estrada simples para o MVP
        roads.add(RoadSegment(100f, 300f, 900f, 300f)) // Estrada horizontal
        roads.add(RoadSegment(500f, 100f, 500f, 600f)) // Estrada vertical
    }

    private fun initializeVehicles() {
        // Adicionar alguns veículos rodoviários
        vehicles.add(RoadVehicle(200f, 280f, RoadVehicleType.BUS))
        vehicles.add(RoadVehicle(400f, 280f, RoadVehicleType.TRUCK))
        vehicles.add(RoadVehicle(600f, 280f, RoadVehicleType.BUS))
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.6f, 0.3f, 1f) // Cor de grama/terreno
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()

        // Renderizar estradas
        for (road in roads) {
            renderRoad(road)
        }

        // Atualizar e renderizar veículos
        for (vehicle in vehicles) {
            vehicle.update(delta)
            renderVehicle(vehicle)
        }

        // Renderizar UI básica
        renderUI()

        game.batch.end()
    }

    private fun renderRoad(road: RoadSegment) {
        val batch = game.batch
        val roadColor = Color.GRAY
        val roadWidth = 40f

        batch.color = roadColor

        // Desenhar estrada como um retângulo
        val length = road.endX - road.startX
        val height = road.endY - road.startY

        if (kotlin.math.abs(length) > kotlin.math.abs(height)) {
            // Estrada horizontal
            batch.draw(createTexture(roadColor),
                kotlin.math.min(road.startX, road.endX),
                road.startY - roadWidth / 2,
                kotlin.math.abs(length),
                roadWidth)
        } else {
            // Estrada vertical
            batch.draw(createTexture(roadColor),
                road.startX - roadWidth / 2,
                kotlin.math.min(road.startY, road.endY),
                roadWidth,
                kotlin.math.abs(height))
        }
    }

    private fun renderVehicle(vehicle: RoadVehicle) {
        val batch = game.batch
        val color = when (vehicle.type) {
            RoadVehicleType.BUS -> Color.YELLOW
            RoadVehicleType.TRUCK -> Color.ORANGE
            RoadVehicleType.CAR -> Color.BLUE
        }

        batch.color = color

        val vehicleWidth = 30f
        val vehicleHeight = 50f

        // Desenhar veículo como retângulo simples
        batch.draw(createTexture(color),
            vehicle.x - vehicleWidth / 2,
            vehicle.y - vehicleHeight / 2,
            vehicleWidth,
            vehicleHeight)
    }

    private fun renderUI() {
        val batch = game.batch
        batch.color = Color.WHITE

        font.draw(batch, "OpenTTD Road MVP - LibGDX Kotlin", 20f, 750f)
        font.draw(batch, "Veículos: ${vehicles.size}", 20f, 720f)
        font.draw(batch, "Estradas: ${roads.size}", 20f, 690f)
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 20f, 660f)

        // Instruções básicas
        font.draw(batch, "Este é um MVP focado no modal rodoviário", 20f, 600f)
        font.draw(batch, "Movimento automático dos veículos", 20f, 570f)
    }

    private fun createTexture(color: Color): com.badlogic.gdx.graphics.Texture {
        // Criar uma textura branca de 1x1 pixel para desenhar formas coloridas
        val pixmap = com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fillRectangle(0, 0, 1, 1)

        val texture = com.badlogic.gdx.graphics.Texture(pixmap)
        pixmap.dispose()

        return texture
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun hide() {}

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        font.dispose()
    }
}
