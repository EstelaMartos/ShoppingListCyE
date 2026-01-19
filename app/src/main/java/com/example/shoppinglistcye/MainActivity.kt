package com.example.shoppinglistcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ---------------------
// RUTAS DE NAVEGACIÓN
// ---------------------
const val PANTALLA_LISTA = "lista"
const val PANTALLA_ANADIR = "anadir"

// ---------------------
// MAIN ACTIVITY
// ---------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Navegacion()
            }
        }
    }
}

// ---------------------
// FUNCIÓN DE NAVEGACIÓN
// ---------------------
@Composable
fun Navegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PANTALLA_LISTA
    ) {
        composable(PANTALLA_LISTA) {
            PantallaListaProductos(navController)
        }
        composable(PANTALLA_ANADIR) {
            PantallaAnadirProducto(navController)
        }
    }
}

// ---------------------
// PANTALLA PRINCIPAL
// ---------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaProductos(navController: NavController) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary),
                title = { Text("ShoppingList") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(PANTALLA_ANADIR)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir producto"
                )
            }
        }
    ) { paddingValues ->
        // AQUÍ VA LA LISTA DE PRODUCTOS
        // LazyColumn
        // Checkbox
        // Eliminar
        // Filtros
        // Contador
        // Diálogo
        val itemsList = (0..5).toList()
        val itemsIndexedList = listOf("A", "B", "C")

        LazyColumn {
            items(itemsList) { Text("Item is $it") }

            item { Text("Single item") }

            itemsIndexed(itemsIndexedList) { index, item -> Text("Item at index $index is $item") }
        }
    }
}

// ---------------------
// PANTALLA AÑADIR PRODUCTO
// ---------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAnadirProducto(navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir producto") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // AQUÍ VA EL FORMULARIO
        // TextField
        // Botón Guardar (popBackStack)
        // Botón Cancelar (popBackStack)
    }
}
