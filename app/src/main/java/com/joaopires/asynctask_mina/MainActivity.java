package com.joaopires.asynctask_mina;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    protected TextView campo;
    protected TextView minas;
    protected Button ok;
    colocarMinas backgroundTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        minas = (TextView)findViewById(R.id.minas);
        campo = (TextView)findViewById(R.id.campo);
        campo.setText("A colocar 99999 minas em 100000 posições.");
        ok = (Button)findViewById(R.id.ok);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundTask = new colocarMinas(ok, minas);
                backgroundTask.execute(99999); //Chama o asyncTask
            }
        });
    }


    //AsyncTask//
    private final class colocarMinas extends AsyncTask<Integer, Integer, Integer> { //Criar classe
        protected Button b; //Boão que foi carregado
        protected TextView t;
        protected int[] campo;
        protected String textoBotao;
        protected int minasColocadas;
        protected int minasPostas = 0;

        public colocarMinas(Button botao, TextView campoRecebido) { //Guardar botão e buscar os números
            b = botao;
            t = campoRecebido;
            campo = new int[10000];
            minasColocadas = 0;
        }
        @Override // runs on the GUI thread
        protected void onPreExecute() { //Só podemos ir buscar coisas ao interface gráfico no onPreExecute pois nas outras já estamos num thread diferente.

            for(int i = 0; i < campo.length; i++){
                campo[i] = 0;
            }
            textoBotao = b.getText().toString(); //Guardar label do botão
            b.setText("0"); //Pôr no botão que estamos a começar
            b.setEnabled(false); //Deixar de poder clicar no botão
        }

        @Override // runs on its own thread
        protected Integer doInBackground(Integer... args) {

            int i,indiceGeradoAleatoriamente,minasColocadas=0,iteracoes=0;
            int numDeMinas = args[0];
            int repetidos = 0;
            int[] campo = new int[100000];
            Random rand = new Random();

            for(i=0;i<campo.length;i++)
            {
                campo[i]=0;
            }

            do
            {
                indiceGeradoAleatoriamente = rand.nextInt(numDeMinas);
                if(campo[indiceGeradoAleatoriamente]==0)
                {
                    campo[indiceGeradoAleatoriamente]=1;
                    minasColocadas++;
                    Log.w("minas", Integer.toString(minasColocadas));
                    publishProgress(minasColocadas, args[0], repetidos);
                }
                else
                {
                    repetidos++;
                    Log.w("minas", "Posição já contem mina");
                    publishProgress(minasColocadas, args[0], repetidos);
                }
                iteracoes++;

            }while(minasColocadas<numDeMinas);

            return 0;
        }

        @Override // runs on the GUI thread
        protected void onProgressUpdate(Integer... percentComplete) {

            minasPostas = percentComplete[0];
            int percentage = percentComplete[0]*100/99999;
            String campoTexto = ("Minas colocadas: " + Integer.toString(minasPostas) + "/" + Integer.toString(percentComplete[1]) + "\nRepetidos: " + Integer.toString(percentComplete[2]));
            t.setText(campoTexto);
            String botaoTexto = ("A colocar minas... " + Integer.toString(percentage) + "%");
            b.setText(botaoTexto);
        }

        @Override // runs on the GUI thread
        protected void onPostExecute(Integer d) {

            b.setEnabled(true);
        }
    }
}