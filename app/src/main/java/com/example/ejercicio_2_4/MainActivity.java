package com.example.ejercicio_2_4;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDescripcion;
    private SignaturePad signaturePad;
    private ImageView imageViewFirma;
    private Button btnHacerFirma, btnLimpiar, btnSave;

    private Bitmap firmaBitmap;
    private byte[] firmaBytes;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        signaturePad = findViewById(R.id.signaturePad);
        imageViewFirma = findViewById(R.id.imageViewFirma);

        btnHacerFirma = findViewById(R.id.btnHacerFirma);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnSave = findViewById(R.id.btnSave);

        btnHacerFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firmaBitmap = signaturePad.getSignatureBitmap();
                imageViewFirma.setImageBitmap(firmaBitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                firmaBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                firmaBytes = stream.toByteArray();
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear(); // Limpia el SignaturePad
                editTextDescripcion.setText("");// Limpia la descripcion
                editTextDescripcion.requestFocus(); // Coloca el puntero en descripcion
                imageViewFirma.setImageBitmap(null); // También puedes limpiar la imagen del ImageView si lo deseas
                Toast.makeText(MainActivity.this, "Firma borrada.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripcion = editTextDescripcion.getText().toString().trim();
                if (!descripcion.isEmpty() && firmaBitmap != null) {
                    try {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        dbHelper.onCreate(db);

                        // Convertir la imagen a Base64
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        firmaBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        String imagenBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        // Guardar en la base de datos
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_DESCRIPCION, descripcion);
                        values.put(DatabaseHelper.COLUMN_FIRMA_DIGITAL, imagenBase64);
                        long result = db.insert(DatabaseHelper.TABLE_NAME, null, values);

                        if (result != -1) {
                            Toast.makeText(MainActivity.this, "Datos guardados correctamente.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al guardar los datos.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("DATABASE_ERROR", "Error de base de datos: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Error de base de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Completa la descripción y la firma.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
