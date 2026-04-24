package com.example.moviles_g37.data

/**
 * Initial catalog of campus places for SénecaMaps.
 *
 * These are seeded into the Firestore "places" collection the first time the
 * app runs. Once seeded, the collection is editable from the Firebase console
 * (add/remove/edit places) and the app reads from Firestore on every launch.
 *
 * Coordinates are centred around the Mario Laserna building at Universidad
 * de los Andes (approx. 4.6018, -74.0657).
 */
object SenecaPlaces {

    val all: List<PlaceInfo> = listOf(
        // --- Edificio Mario Laserna (ML) -------------------------------------
        PlaceInfo(
            id = "ml-building",
            name = "Edificio Mario Laserna",
            category = "building",
            latitude = 4.601800, longitude = -74.065700,
            building = "ML",
            description = "Facultad de Ingeniería",
            nearbyPois = listOf(
                "Cafetería Central - 1er piso ML",
                "Biblioteca ML - 2do piso",
                "Auditorio ML - 1er piso"
            )
        ),
        PlaceInfo(
            id = "ml-603",
            name = "ML-603",
            category = "classroom",
            latitude = 4.603087, longitude = -74.065111,
            building = "ML", floor = "6",
            description = "Salón Mario Laserna piso 6",
            nearbyPois = listOf(
                "Cajero Davivienda - 5to piso ML",
                "Cosechas - Terraza del ML",
                "La Galletería - 5to piso ML"
            )
        ),
        PlaceInfo(
            id = "ml-503",
            name = "ML-503",
            category = "classroom",
            latitude = 4.601900, longitude = -74.065700,
            building = "ML", floor = "5",
            description = "Salón Mario Laserna piso 5",
            nearbyPois = listOf(
                "Cajero Davivienda - 5to piso ML",
                "La Galletería - 5to piso ML"
            )
        ),
        PlaceInfo(
            id = "ml-403",
            name = "ML-403",
            category = "classroom",
            latitude = 4.601800, longitude = -74.065750,
            building = "ML", floor = "4",
            description = "Salón Mario Laserna piso 4"
        ),
        PlaceInfo(
            id = "ml-306",
            name = "ML-306",
            category = "classroom",
            latitude = 4.601700, longitude = -74.065700,
            building = "ML", floor = "3",
            description = "Salón Mario Laserna piso 3"
        ),
        PlaceInfo(
            id = "ml-library",
            name = "Biblioteca ML",
            category = "study",
            latitude = 4.602924, longitude = -74.064751,
            building = "ML", floor = "2",
            description = "Espacio de estudio e investigación",
            nearbyPois = listOf("Área de cubículos silenciosos", "Puestos grupales")
        ),
        PlaceInfo(
            id = "ml-auditorium",
            name = "Auditorio ML",
            category = "service",
            latitude = 4.601750, longitude = -74.065680,
            building = "ML", floor = "1",
            description = "Conferencias y eventos"
        ),
        PlaceInfo(
            id = "ml-restroom-3",
            name = "Baños ML - Piso 3",
            category = "restroom",
            latitude = 4.601780, longitude = -74.065720,
            building = "ML", floor = "3"
        ),
        PlaceInfo(
            id = "ml-restroom-5",
            name = "Baños ML - Piso 5",
            category = "restroom",
            latitude = 4.601880, longitude = -74.065720,
            building = "ML", floor = "5"
        ),
        PlaceInfo(
            id = "ml-cafeteria",
            name = "Cafetería ML",
            category = "food",
            latitude = 4.601820, longitude = -74.065650,
            building = "ML", floor = "1",
            description = "Comidas rápidas y bebidas"
        ),

        // --- Edificios cercanos ---------------------------------------------
        PlaceInfo(
            id = "sd-building",
            name = "Edificio Santo Domingo",
            category = "building",
            latitude = 4.601250, longitude = -74.066600,
            building = "SD",
            description = "Facultad de Administración y Economía"
        ),
        PlaceInfo(
            id = "sd-805",
            name = "SD-805",
            category = "classroom",
            latitude = 4.601250, longitude = -74.066600,
            building = "SD", floor = "8",
            description = "Salón Santo Domingo piso 8"
        ),
        PlaceInfo(
            id = "w-building",
            name = "Edificio W",
            category = "building",
            latitude = 4.602350, longitude = -74.066200,
            building = "W",
            description = "Facultad de Derecho"
        ),
        PlaceInfo(
            id = "o-cafeteria",
            name = "Cafetería O",
            category = "food",
            latitude = 4.600875, longitude = -74.065210,
            building = "O",
            description = "Comedor estudiantil principal"
        ),
        PlaceInfo(
            id = "food-chick",
            name = "Chick n' Chips",
            category = "food",
            latitude = 4.601050, longitude = -74.066050,
            building = "Plazoleta",
            description = "Comida rápida"
        ),
        PlaceInfo(
            id = "food-cosechas",
            name = "Cosechas - Terraza ML",
            category = "food",
            latitude = 4.601950, longitude = -74.065600,
            building = "ML", floor = "Terraza",
            description = "Jugos y bowls saludables"
        ),

        // --- Deportes y servicios -------------------------------------------
        PlaceInfo(
            id = "centro-deportivo",
            name = "Centro Deportivo",
            category = "sports",
            latitude = 4.600761, longitude = -74.063661,
            building = "CD",
            description = "Gimnasio, canchas y piscina"
        ),
        PlaceInfo(
            id = "central-library",
            name = "Biblioteca Ramón de Zubiría",
            category = "study",
            latitude = 4.602900, longitude = -74.065550,
            building = "RGD",
            description = "Biblioteca central de la universidad"
        ),
        PlaceInfo(
            id = "cajero-ml",
            name = "Cajero Davivienda ML",
            category = "service",
            latitude = 4.601900, longitude = -74.065680,
            building = "ML", floor = "5",
            description = "Cajero automático"
        ),
        PlaceInfo(
            id = "main-entrance",
            name = "Entrada Principal Uniandes",
            category = "service",
            latitude = 4.601400, longitude = -74.066900,
            building = "Carrera 1",
            description = "Entrada por Carrera 1"
        )
    )

    fun byCategory(category: String): List<PlaceInfo> =
        all.filter { it.category.equals(category, ignoreCase = true) }
}
