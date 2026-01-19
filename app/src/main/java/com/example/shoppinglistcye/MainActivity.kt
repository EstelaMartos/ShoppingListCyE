package com.example.shoppinglistcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------------
// RUTAS DE NAVEGACIÓN
// ---------------------
const val PANTALLA_LISTA = "lista"
const val PANTALLA_ANADIR = "anadir"

data class Producto(
    val nombre: String,
    val comprado: Boolean = false
)


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

    var productos = remember {
        mutableStateListOf(
            Producto("Leche"),
            Producto("Pan"),
            Producto("Huevos")
        )
    }

    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("ShoppingList") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(PANTALLA_ANADIR) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir producto")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {

            // CONTADOR
            Text(
                text = "Pendientes: ${productos.count { !it.comprado }}",
                modifier = Modifier.padding(8.dp)
            )

            // LISTA
            LazyColumn {
                items(productos) { producto ->
                    ItemProducto(
                        producto = producto,
                        onCheckedChange = { checked ->
                            val index = productos.indexOf(producto)
                            productos[index] = producto.copy(comprado = checked)
                        },
                        onDelete = {
                            productoAEliminar = producto
                            mostrarDialogo = true
                        }
                    )
                }
            }
        }
        if (mostrarDialogo && productoAEliminar != null) {
            CuadroDialogo(
                icon = Icons.Default.Delete,
                dialogTitle = "Eliminar producto",
                dialogText = "¿Seguro que quieres eliminar ${productoAEliminar!!.nombre}?",
                onDismissRequest = {
                    mostrarDialogo = false
                },
                onConfirmation = {
                    productos.remove(productoAEliminar)
                    mostrarDialogo = false
                }
            )
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
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            // aquí irá el formulario
        }
    }
}


@Composable
fun ItemProducto(
    producto: Producto,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = producto.comprado,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = producto.nombre,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar"
            )
        }
    }
}


@Composable
fun CuadroDialogo(
    icon: ImageVector,
    dialogTitle: String,
    dialogText: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {

    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = null)
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!cargando) {
                    Text(
                        text = dialogText,
                        modifier = Modifier.height(70.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    cargando = true
                    scope.launch {
                        delay(1000) // 1 segundo es suficiente
                        onConfirmation()
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}

