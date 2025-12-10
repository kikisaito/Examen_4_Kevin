package com.example.pokemonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokemonapp.ui.screens.DetailScreen
import com.example.pokemonapp.ui.screens.FavoritesScreen
import com.example.pokemonapp.ui.screens.LandingScreen
import com.example.pokemonapp.ui.screens.SearchScreen
import com.example.pokemonapp.ui.viewmodel.DetailViewModel
import com.example.pokemonapp.ui.viewmodel.FavoritesViewModel
import com.example.pokemonapp.ui.viewmodel.LandingViewModel
import com.example.pokemonapp.ui.viewmodel.PokemonViewModelFactory
import com.example.pokemonapp.ui.viewmodel.SearchViewModel
import com.example.pokemonapp.util.NetworkConnectivityObserver

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun main() {
        super.onCreate(null) // Mock onCreate, usually super.onCreate(savedInstanceState) 

        val app = application as PokemonApplication
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Pokemon App") },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "landing",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Landing
                        composable("landing") {
                            val viewModel: LandingViewModel = viewModel(
                                factory = PokemonViewModelFactory(app.repository, connectivityObserver)
                            )
                            LandingScreen(
                                viewModel = viewModel,
                                onNavigateToDetail = { pokemon ->
                                    navController.navigate("detail/${pokemon.id}/${pokemon.name}")
                                },
                                onNavigateToSearch = {
                                    navController.navigate("search")
                                },
                                onNavigateToFavorites = {
                                    navController.navigate("favorites")
                                }
                            )
                        }

                        // Detail
                        composable(
                            "detail/{id}/{name}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.IntType },
                                navArgument("name") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: 0
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            
                            val viewModel: DetailViewModel = viewModel(
                                factory = PokemonViewModelFactory(app.repository)
                            )
                            
                            // Load data
                            remember {
                                viewModel.loadPokemon(id, name, null)
                                true
                            }
                            
                            DetailScreen(viewModel = viewModel)
                        }

                        // Search
                        composable("search") {
                            val viewModel: SearchViewModel = viewModel(
                                factory = PokemonViewModelFactory(app.repository)
                            )
                            SearchScreen(
                                viewModel = viewModel,
                                onNavigateToDetail = { pokemon ->
                                    navController.navigate("detail/${pokemon.id}/${pokemon.name}")
                                }
                            )
                        }

                        // Favorites
                        composable("favorites") {
                            val viewModel: FavoritesViewModel = viewModel(
                                factory = PokemonViewModelFactory(app.repository)
                            )
                            FavoritesScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Proper onCreate override for code file correctness
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        main()
    }
}
