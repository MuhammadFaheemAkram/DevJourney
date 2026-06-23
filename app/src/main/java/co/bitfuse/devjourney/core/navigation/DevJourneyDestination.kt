package co.bitfuse.devjourney.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

object DevJourneyRoutes {
    const val DASHBOARD = "dashboard"
    const val ROADMAPS = "roadmaps"
    const val ROADMAP_ID_ARG = "roadmapId"
    const val ROADMAP_DETAIL = "roadmaps/{$ROADMAP_ID_ARG}"
    const val NOTES = "notes"
    const val GOALS = "goals"
    const val CHALLENGES = "challenges"
    const val RESOURCES = "resources"
    const val ANALYTICS = "analytics"
    const val SEARCH = "search"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val TOPIC_ID_ARG = "topicId"
    const val TOPIC_DETAIL = "topics/{$TOPIC_ID_ARG}"

    fun roadmapDetailRoute(roadmapId: String): String = "roadmaps/$roadmapId"

    fun topicDetailRoute(topicId: String): String = "topics/$topicId"
}

data class DevJourneyDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
)

val BottomBarDestinations = listOf(
    DevJourneyDestination(DevJourneyRoutes.DASHBOARD, "Dashboard", Icons.Filled.Home),
    DevJourneyDestination(DevJourneyRoutes.ROADMAPS, "Roadmaps", Icons.AutoMirrored.Filled.List),
    DevJourneyDestination(DevJourneyRoutes.CHALLENGES, "Challenges", Icons.Filled.Build),
    DevJourneyDestination(DevJourneyRoutes.ANALYTICS, "Analytics", Icons.Filled.Star),
    DevJourneyDestination(DevJourneyRoutes.SETTINGS, "Settings", Icons.Filled.Settings),
)

val DrawerDestinations = listOf(
    DevJourneyDestination(DevJourneyRoutes.DASHBOARD, "Dashboard", Icons.Filled.Home),
    DevJourneyDestination(DevJourneyRoutes.ROADMAPS, "Roadmaps", Icons.AutoMirrored.Filled.List),
    DevJourneyDestination(DevJourneyRoutes.NOTES, "Notes", Icons.Filled.Create),
    DevJourneyDestination(DevJourneyRoutes.GOALS, "Goals", Icons.Filled.CheckCircle),
    DevJourneyDestination(DevJourneyRoutes.CHALLENGES, "Challenges", Icons.Filled.Build),
    DevJourneyDestination(DevJourneyRoutes.RESOURCES, "Resources", Icons.Filled.FavoriteBorder),
    DevJourneyDestination(DevJourneyRoutes.SEARCH, "Search", Icons.Filled.Search),
    DevJourneyDestination(DevJourneyRoutes.ANALYTICS, "Analytics", Icons.Filled.Star),
    DevJourneyDestination(DevJourneyRoutes.SETTINGS, "Settings", Icons.Filled.Settings),
    DevJourneyDestination(DevJourneyRoutes.ABOUT, "About", Icons.Filled.Info),
)

fun destinationForRoute(route: String?): DevJourneyDestination {
    if (route == DevJourneyRoutes.ROADMAP_DETAIL) {
        return DevJourneyDestination(DevJourneyRoutes.ROADMAP_DETAIL, "Roadmap detail", Icons.AutoMirrored.Filled.List)
    }
    if (route == DevJourneyRoutes.TOPIC_DETAIL) {
        return DevJourneyDestination(DevJourneyRoutes.TOPIC_DETAIL, "Topic detail", Icons.Filled.CheckCircle)
    }
    return DrawerDestinations.firstOrNull { it.route == route }
        ?: DrawerDestinations.first()
}

fun isTopLevelRoute(route: String?): Boolean {
    return DrawerDestinations.any { it.route == route }
}

fun selectedTopLevelRoute(route: String?): String? {
    return when (route) {
        DevJourneyRoutes.ROADMAP_DETAIL,
        DevJourneyRoutes.TOPIC_DETAIL,
        -> DevJourneyRoutes.ROADMAPS
        else -> route
    }
}
