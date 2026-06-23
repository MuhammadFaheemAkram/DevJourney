package co.bitfuse.devjourney.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.bitfuse.devjourney.feature.analytics.AnalyticsRoute
import co.bitfuse.devjourney.feature.challenges.ChallengesRoute
import co.bitfuse.devjourney.feature.dashboard.DashboardRoute
import co.bitfuse.devjourney.feature.goals.GoalsRoute
import co.bitfuse.devjourney.feature.notes.NotesRoute
import co.bitfuse.devjourney.feature.resources.ResourcesRoute
import co.bitfuse.devjourney.feature.roadmap.RoadmapDetailRoute
import co.bitfuse.devjourney.feature.roadmap.RoadmapsRoute
import co.bitfuse.devjourney.feature.search.SearchRoute
import co.bitfuse.devjourney.feature.settings.AboutRoute
import co.bitfuse.devjourney.feature.settings.SettingsRoute
import co.bitfuse.devjourney.feature.topicdetail.TopicDetailRoute

@Composable
fun DevJourneyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = DevJourneyRoutes.DASHBOARD,
        modifier = modifier,
    ) {
        composable(DevJourneyRoutes.DASHBOARD) {
            DashboardRoute(onOpenRoadmaps = { navController.navigateSingleTopTo(DevJourneyRoutes.ROADMAPS) })
        }
        composable(DevJourneyRoutes.ROADMAPS) {
            RoadmapsRoute(
                onRoadmapClick = navController::navigateToRoadmapDetail,
            )
        }
        composable(
            route = DevJourneyRoutes.ROADMAP_DETAIL,
            arguments = listOf(navArgument(DevJourneyRoutes.ROADMAP_ID_ARG) { type = NavType.StringType }),
        ) {
            RoadmapDetailRoute(
                onTopicClick = navController::navigateToTopicDetail,
            )
        }
        composable(
            route = DevJourneyRoutes.TOPIC_DETAIL,
            arguments = listOf(navArgument(DevJourneyRoutes.TOPIC_ID_ARG) { type = NavType.StringType }),
        ) {
            TopicDetailRoute()
        }
        composable(DevJourneyRoutes.NOTES) {
            NotesRoute()
        }
        composable(DevJourneyRoutes.GOALS) {
            GoalsRoute()
        }
        composable(DevJourneyRoutes.CHALLENGES) {
            ChallengesRoute()
        }
        composable(DevJourneyRoutes.RESOURCES) {
            ResourcesRoute()
        }
        composable(DevJourneyRoutes.SEARCH) {
            SearchRoute(
                onRoadmapClick = navController::navigateToRoadmapDetail,
                onTopicClick = navController::navigateToTopicDetail,
            )
        }
        composable(DevJourneyRoutes.ANALYTICS) {
            AnalyticsRoute()
        }
        composable(DevJourneyRoutes.SETTINGS) {
            SettingsRoute()
        }
        composable(DevJourneyRoutes.ABOUT) {
            AboutRoute()
        }
    }
}
