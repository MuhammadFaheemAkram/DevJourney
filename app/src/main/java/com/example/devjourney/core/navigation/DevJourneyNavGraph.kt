package com.example.devjourney.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.devjourney.feature.analytics.AnalyticsRoute
import com.example.devjourney.feature.challenges.ChallengesRoute
import com.example.devjourney.feature.dashboard.DashboardRoute
import com.example.devjourney.feature.goals.GoalsRoute
import com.example.devjourney.feature.notes.NotesRoute
import com.example.devjourney.feature.resources.ResourcesRoute
import com.example.devjourney.feature.roadmap.RoadmapDetailRoute
import com.example.devjourney.feature.roadmap.RoadmapsRoute
import com.example.devjourney.feature.search.SearchRoute
import com.example.devjourney.feature.settings.AboutRoute
import com.example.devjourney.feature.settings.SettingsRoute
import com.example.devjourney.feature.topicdetail.TopicDetailRoute

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
