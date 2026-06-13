package com.example.cinema

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cinema.ui.theme.CinemaTheme

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        enableEdgeToEdge()
        setContent {
            CinemaTheme {
                MainScreen(
                    dbHelper = dbHelper,
                    onAddMovie = {
                        startActivity(Intent(this, AddMovieActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(dbHelper: DatabaseHelper, onAddMovie: () -> Unit) {
    var titleFilter by remember { mutableStateOf("") }
    var directorFilter by remember { mutableStateOf("") }
    
    val allMovies = remember { mutableStateOf<List<Movie>>(emptyList()) }

    fun refresh() {
        Thread {
            val movies = dbHelper.getAllMovies()
            // In compose we can just assign the state and compose will handle it, 
            // but just to be safe on the thread issue
            allMovies.value = movies
        }.start()
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    val filteredMovies = allMovies.value.filter {
        it.title.contains(titleFilter, ignoreCase = true) &&
        it.director.contains(directorFilter, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Cinema App") },
                actions = {
                    Button(onClick = { refresh() }) {
                        Text("Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {
                Icon(Icons.Default.Add, contentDescription = "Add Movie")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = titleFilter,
                onValueChange = { titleFilter = it },
                label = { Text("Filter by Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            OutlinedTextField(
                value = directorFilter,
                onValueChange = { directorFilter = it },
                label = { Text("Filter by Director") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMovies) { movie ->
                    MovieCard(movie = movie)
                }
            }
        }
    }
}

@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Title: ${movie.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Director: ${movie.director}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Year: ${movie.year} | Genre: ${movie.genre}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Cost: ${movie.cost}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
