import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileViewModel : ViewModel() {
    var imageBitmap by mutableStateOf<Bitmap?>(null)
        private set

    // Nombre del archivo donde guardaremos la foto
    private val fileName = "profile_picture.jpg"

    // Función para CARGAR la imagen al iniciar
    fun loadImage(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                withContext(Dispatchers.Main) {
                    imageBitmap = bitmap
                }
            }
        }
    }

    // Función para GUARDAR la imagen cuando sacas la foto
    fun saveImage(context: Context, bitmap: Bitmap) {
        // 1. Actualizamos la UI inmediatamente
        imageBitmap = bitmap

        // 2. Guardamos en disco en segundo plano
        viewModelScope.launch(Dispatchers.IO) {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        }
    }
}