package com.example.shoppinglistcye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------------
// RUTAS
// ---------------------
const val PANTALLA_LISTA = "lista"
const val PANTALLA_ANADIR = "anadir"

// ---------------------
// MODELO
// ---------------------
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
// NAVEGACIÓN (ESTADO COMPARTIDO)
// ---------------------
@Composable
fun Navegacion() {
    val navController = rememberNavController()

    val productos = remember {
        mutableStateListOf(
            Producto("Leche"),
            Producto("Pan"),
            Producto("Huevos")
        )
    }

    NavHost(
        navController = navController,
        startDestination = PANTALLA_LISTA
    ) {
        composable(PANTALLA_LISTA) {
            PantallaListaProductos(navController, productos)
        }
        composable(PANTALLA_ANADIR) {
            PantallaAnadirProducto(navController, productos)
        }
    }
}

// ---------------------
// PANTALLA LISTA
// ---------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaProductos(
    navController: NavController,
    productos: MutableList<Producto>
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }

    // 0 = Todos | 1 = Pendientes | 2 = Comprados
    var opcionSeleccionada by remember { mutableIntStateOf(0) }

    val productosFiltrados = when (opcionSeleccionada) {
        0 -> productos
        1 -> productos.filter { !it.comprado }
        2 -> productos.filter { it.comprado }
        else -> productos
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // IMAGEN DE FONDO
        Image(
            painter = painterResource(id = R.drawable.imagen_fondo),
            contentDescription = "Fondo de pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Estira la imagen
        )

        // CONTENIDO (SCAFFOLD TRANSPARENTE)
        Scaffold(
            containerColor = Color.Transparent, // ¡ESTO ES CLAVE!
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    title = { Text("ShoppingList") },
                    navigationIcon = {
                        // Imagen redonda pequeña en el TopAppBar
                        Box(
                            modifier = Modifier
                                .size(40.dp) // Tamaño pequeño
                                .clip(CircleShape) // Hace la imagen redonda
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pimiento), // La imagen que se quiere mostrar
                                contentDescription = "Imagen redonda",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop // Recorta la imagen para que se ajuste
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(PANTALLA_ANADIR) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        ) { padding ->

            Column(modifier = Modifier.padding(padding)) {

                // CONTADOR
                Text(
                    text = "Tienes ${productos.count { !it.comprado }} productos pendientes",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                // FILTROS
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(0, 3),
                        selected = opcionSeleccionada == 0,
                        onClick = { opcionSeleccionada = 0 },
                        label = { Text("Todos") }
                    )
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(1, 3),
                        selected = opcionSeleccionada == 1,
                        onClick = { opcionSeleccionada = 1 },
                        label = { Text("Pendientes") }
                    )
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(2, 3),
                        selected = opcionSeleccionada == 2,
                        onClick = { opcionSeleccionada = 2 },
                        label = { Text("Comprados") }
                    )
                }

                // LISTA
                LazyColumn {
                    items(productosFiltrados) { producto ->
                        ItemProducto(
                            producto = producto,
                            onCheckedChange = { checked ->
                                val index = productos.indexOf(producto)
                                if (index != -1) {
                                    productos[index] =
                                        producto.copy(comprado = checked)
                                }
                            },
                            onDelete = {
                                productoAEliminar = producto
                                mostrarDialogo = true
                            }
                        )
                    }
                }
            }

            // DIÁLOGO CONFIRMACIÓN
            if (mostrarDialogo && productoAEliminar != null) {
                CuadroDialogo(
                    icon = Icons.Default.Delete,
                    dialogTitle = "Eliminar producto",
                    dialogText = "¿Seguro que quieres eliminar ${productoAEliminar!!.nombre}?",
                    onDismissRequest = { mostrarDialogo = false },
                    onConfirmation = {
                        productos.remove(productoAEliminar)
                        mostrarDialogo = false
                    }
                )
            }
        }
    }
}


// ---------------------
// PANTALLA AÑADIR PRODUCTO
// ---------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAnadirProducto(
    navController: NavController,    productos: MutableList<Producto>
) {
    var texto by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Imagen de fondo (con ContentScale para que cubra todo)
        Image(
            painter = painterResource(id = R.drawable.imagen_fondo),
            contentDescription = "Fondo de pantalla",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    title = { Text("Añadir producto") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                Text(
                    text = "Nombre del producto",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground // Asegura legibilidad
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    modifier = Modifier.fillMaxWidth(),
                    // Opcional: Fondo sólido para el campo de texto para que sea fácil de leer
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (texto.isNotBlank()) {
                                productos.add(Producto(texto))
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text("Guardar")
                    }

                    TextButton(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

// ---------------------
// ITEM PRODUCTO
// ---------------------
@Composable
fun ItemProducto(
    producto: Producto,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Espacio entre los elementos
        elevation = CardDefaults.cardElevation(4.dp), // Sombra para la card
        shape = MaterialTheme.shapes.medium, // Bordes redondeados
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Espaciado interno
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox para marcar como comprado
            Checkbox(
                checked = producto.comprado,
                onCheckedChange = onCheckedChange
            )

            // Nombre del producto
            Text(
                text = producto.nombre,
                modifier = Modifier.weight(1f), // Rellenar el espacio disponible
                style = MaterialTheme.typography.bodyLarge
            )

            // Botón para eliminar el producto
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar producto"
                )
            }
        }
    }
}

// ---------------------
// DIÁLOGO CONFIRMACIÓN
// ---------------------
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
        icon = { Icon(icon, contentDescription = null) },
        title = { Text(dialogTitle) },
        text = {
            if (cargando) {
                CircularProgressIndicator()
            } else {
                Text(dialogText)
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    cargando = true
                    scope.launch {
                        delay(1000)
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
