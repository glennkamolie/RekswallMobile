package com.example.rekswallmobile;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class mainmenu extends AppCompatActivity {

    private Button btnbeli;
    private Button btnjual;
    private Button historyjual;
    private Button historybeli;
    private Button jumlahdana;

    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        btnbeli = findViewById(R.id.btnBuy);
        btnjual = findViewById(R.id.btnSell);
        historybeli = findViewById(R.id.btnbuyhistory);
        historyjual = findViewById(R.id.btnSellhistory);
        username = findViewById(R.id.username);
        jumlahdana = findViewById(R.id.btnjumlahdana);

        Intent intent = getIntent();
        String receivedData = intent.getStringExtra("username");
        username.setText(receivedData);

        btnbeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mainmenu.this, belirekasadana.class);
                intent.putExtra("username",username.getText());
                startActivity(intent);
                finish();
            }
        });

        btnjual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mainmenu.this, jualreksadana.class);
                intent.putExtra("username",receivedData);
                startActivity(intent);
                finish();
            }
        });

        historybeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mainmenu.this, historybeli.class);
                intent.putExtra("username",receivedData);
                startActivity(intent);
                finish();
            }
        });

        historyjual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(mainmenu.this, historyjual.class);
                intent.putExtra("username",receivedData);
                startActivity(intent);
                finish();
            }
        });

        jumlahdana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainmenu.this, jumlahdana.class);
                intent.putExtra("username",receivedData);
                startActivity(intent);
                finish();
            }
        });

    }

}