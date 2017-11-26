package net.azurewebsites.jhonattansouza.unicariocamobileaps;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements View.OnClickListener {
    @BindView(R.id.txtDescricaoId) EditText textoDescricao;
    @BindView(R.id.txtQuantidadeId) EditText textoQuantidade;
    @BindView(R.id.txtPrecoUnitarioId) EditText textoPrecoUnitario;
    @BindView(R.id.txtDescricaoId2) EditText textoDescricao2;
    @BindView(R.id.txtQuantidadeId2) EditText textoQuantidade2;
    @BindView(R.id.txtPrecoUnitarioId2) EditText textoPrecoUnitario2;

    @BindView(R.id.labelDescricao2) TextView labelDescricao;
    @BindView(R.id.labelQuantidade2) TextView labelQuantidade;
    @BindView(R.id.labelPrecoUnitario2) TextView labelPrecoUnitario;
    @BindView(R.id.labelDisponivelVenda2) TextView labelDisponivelVenda;

    @BindView(R.id.botaoInserirId) Button botaoInserir;
    @BindView(R.id.botaoExcluirId) Button botaoExcluir;
    @BindView(R.id.botaoAlterarId) Button botaoAlterar;
    @BindView(R.id.botaoListarId) Button botaoListar;
    @BindView(R.id.botaoFecharId) Button botaoFechar;
    @BindView(R.id.botaoSalvarId) Button botaoSalvar;
    @BindView(R.id.botaoFecharId1) Button botaoFecharExcluir;
    @BindView(R.id.botaoFecharId2) Button botaoFecharAlterar;

    @BindView(R.id.radioGroupId) RadioGroup radioGroup;
    @BindView(R.id.radioGroupId2) RadioGroup radioGroupAlterar;

    @BindView(R.id.logoId) ImageView logo;

    @BindView(R.id.LayoutId) ConstraintLayout layoutListar;
    @BindView(R.id.LayoutAlterarId) ConstraintLayout layoutAlterar;
    @BindView(R.id.LayoutExcluirId) ConstraintLayout layoutExcluir;

    @BindView(R.id.listViewListarId) ListView listaProdutos;
    @BindView(R.id.listViewExcluirId) ListView listaExcluir;
    @BindView(R.id.listViewAlterarId) ListView listaAlterar;

    private SQLiteDatabase bancoDados;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;
    private ArrayList<String> descricoes;
    private ArrayList<String> quantidades;
    private ArrayList<String> precosUnitarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        try {
            StartDB();
            SetListerners();
            listarProdutos();
        } catch (Exception e) {
            Log.e("Error", MainActivity.class.getName());
        } finally {
            FecharConexao();
        }
    }

    private void StartDB()
    {
        String dbName = "appestoque";
        String createTableEstoque = "CREATE TABLE IF NOT EXISTS estoque(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descricao VARCHAR, quantidade INTEGER, " +
                "precoUnitario DOUBLE, " +
                "disponivelVenda VARCHAR)";

        bancoDados = openOrCreateDatabase(dbName, MODE_PRIVATE, null);

        bancoDados.execSQL(createTableEstoque);
    }

    private void AbrirConexao()
    {
        String dbName = "appestoque";
        bancoDados = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
    }

    private void FecharConexao()
    {
        bancoDados.close();
    }

    private void SetListerners()
    {
        botaoInserir.setOnClickListener(this);
        botaoExcluir.setOnClickListener(this);
        botaoAlterar.setOnClickListener(this);
        botaoListar.setOnClickListener(this);
        botaoFechar.setOnClickListener(this);
        botaoFecharExcluir.setOnClickListener(this);
        botaoFecharAlterar.setOnClickListener(this);
        botaoSalvar.setOnClickListener(this);

        listaExcluir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                excluirProdutos(ids.get(position), itens.get(position));
                listarProdutos();
            }
        });

        listaAlterar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textoDescricao2.setText(descricoes.get(position));
                textoQuantidade2.setText(quantidades.get(position));
                textoPrecoUnitario2.setText(precosUnitarios.get(position));
                listaAlterar.setVisibility(View.GONE);
                labelDescricao.setVisibility(View.VISIBLE);
                labelQuantidade.setVisibility(View.VISIBLE);
                labelPrecoUnitario.setVisibility(View.VISIBLE);
                labelDisponivelVenda.setVisibility(View.VISIBLE);
                botaoSalvar.setVisibility(View.VISIBLE);
                textoDescricao2.setVisibility(View.VISIBLE);
                textoQuantidade2.setVisibility(View.VISIBLE);
                textoPrecoUnitario2.setVisibility(View.VISIBLE);
                radioGroupAlterar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.botaoInserirId:
                String descricao = textoDescricao.getText().toString();
                Integer quantidade = Integer.parseInt(textoQuantidade.getText().toString());
                Double precoUnitario = Double.parseDouble(textoPrecoUnitario.getText().toString());
                String opcao = "";

                int idRadioButtonEscolhido = radioGroup.getCheckedRadioButtonId();
                if (idRadioButtonEscolhido > 0) {
                    RadioButton radioButtonOpcao = (RadioButton) findViewById(idRadioButtonEscolhido);
                    opcao = radioButtonOpcao.getText().toString();
                }
                inserirProduto(descricao, quantidade, precoUnitario, opcao);
                break;

            case R.id.botaoExcluirId:
                logo.setVisibility(View.GONE);
                layoutExcluir.setVisibility(View.VISIBLE);
                layoutAlterar.setVisibility(View.GONE);
                layoutListar.setVisibility(View.GONE);
                botaoInserir.setVisibility(View.GONE);
                botaoExcluir.setVisibility(View.GONE);
                botaoAlterar.setVisibility(View.GONE);
                botaoListar.setVisibility(View.GONE);
                break;

            case R.id.botaoAlterarId:

                logo.setVisibility(View.GONE);
                layoutAlterar.setVisibility(View.VISIBLE);
                layoutListar.setVisibility(View.GONE);
                layoutExcluir.setVisibility(View.GONE);
                botaoInserir.setVisibility(View.GONE);
                botaoExcluir.setVisibility(View.GONE);
                botaoAlterar.setVisibility(View.GONE);
                botaoListar.setVisibility(View.GONE);
                break;

            case R.id.botaoListarId:
                logo.setVisibility(View.GONE);
                layoutListar.setVisibility(View.VISIBLE);
                layoutExcluir.setVisibility(View.GONE);
                layoutAlterar.setVisibility(View.GONE);
                botaoInserir.setVisibility(View.GONE);
                botaoExcluir.setVisibility(View.GONE);
                botaoAlterar.setVisibility(View.GONE);
                botaoListar.setVisibility(View.GONE);
                break;

            case R.id.botaoFecharId:
                logo.setVisibility(ImageView.VISIBLE);
                layoutListar.setVisibility(View.GONE);
                layoutExcluir.setVisibility(View.GONE);
                layoutAlterar.setVisibility(View.GONE);
                botaoInserir.setVisibility(View.VISIBLE);
                botaoExcluir.setVisibility(View.VISIBLE);
                botaoAlterar.setVisibility(View.VISIBLE);
                botaoListar.setVisibility(View.VISIBLE);
                break;

            case R.id.botaoFecharId1:
                logo.setVisibility(View.VISIBLE);
                layoutExcluir.setVisibility(View.GONE);
                layoutListar.setVisibility(View.GONE);
                layoutAlterar.setVisibility(View.GONE);
                botaoInserir.setVisibility(View.VISIBLE);
                botaoExcluir.setVisibility(View.VISIBLE);
                botaoAlterar.setVisibility(View.VISIBLE);
                botaoListar.setVisibility(View.VISIBLE);
                break;

            case R.id.botaoFecharId2:
                logo.setVisibility(View.VISIBLE);
                layoutExcluir.setVisibility(View.GONE);
                layoutListar.setVisibility(View.GONE);
                layoutAlterar.setVisibility(View.GONE);

                botaoInserir.setVisibility(View.VISIBLE);
                botaoExcluir.setVisibility(View.VISIBLE);
                botaoAlterar.setVisibility(View.VISIBLE);
                botaoListar.setVisibility(View.VISIBLE);
                listaAlterar.setVisibility(View.VISIBLE);

                labelDescricao.setVisibility(View.GONE);
                labelQuantidade.setVisibility(View.GONE);
                labelPrecoUnitario.setVisibility(View.GONE);
                labelDisponivelVenda.setVisibility(View.GONE);

                botaoSalvar.setVisibility(View.GONE);
                textoDescricao2.setVisibility(View.GONE);
                textoQuantidade2.setVisibility(View.GONE);
                textoPrecoUnitario2.setVisibility(View.GONE);
                radioGroupAlterar.setVisibility(View.GONE);
                break;

            case R.id.botaoSalvarId:
                String descricao2 = textoDescricao2.getText().toString();
                Integer quantidade2 = Integer.parseInt(textoQuantidade2.getText().toString());
                Double precoUnitario2 = Double.parseDouble(textoPrecoUnitario2.getText().toString());
                String opcao2 = "";

                int idRadioButtonEscolhido2 = radioGroupAlterar.getCheckedRadioButtonId();
                if (idRadioButtonEscolhido2 > 0) {
                    RadioButton radioButtonOpcaoAlterada = (RadioButton) findViewById(idRadioButtonEscolhido2);
                    opcao2 = radioButtonOpcaoAlterada.getText().toString();
                }

                alterarProduto(descricao2, quantidade2, precoUnitario2, opcao2);

                listaAlterar.setVisibility(View.VISIBLE);
                labelDescricao.setVisibility(View.GONE);
                labelQuantidade.setVisibility(View.GONE);
                labelPrecoUnitario.setVisibility(View.GONE);
                labelDisponivelVenda.setVisibility(View.GONE);
                botaoSalvar.setVisibility(View.GONE);
                textoDescricao2.setVisibility(View.GONE);
                textoQuantidade2.setVisibility(View.GONE);
                textoPrecoUnitario2.setVisibility(View.GONE);
                radioGroupAlterar.setVisibility(View.GONE);
                break;
        }
    }

    private void inserirProduto(String descricao, Integer quantidade, Double precoUnitario, String disponivelVenda) {
        if ((descricao.equals("")) || (quantidade.equals(null)) || (precoUnitario.equals(null)) || (disponivelVenda.equals(""))) {
            Toast.makeText(MainActivity.this, "Atenção! Preencha todos os campos.", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (descricao.equals("")) {
                    Toast.makeText(MainActivity.this, "Atenção! Preencha todos os campos.", Toast.LENGTH_SHORT).show();
                } else {
                    AbrirConexao();
                    bancoDados.execSQL("INSERT INTO estoque(descricao,quantidade,precoUnitario,disponivelVenda)VALUES ('" + descricao + "','" + quantidade + "','" + precoUnitario + "','" + disponivelVenda + "')");
                    Toast.makeText(MainActivity.this, descricao + " inserido com Sucesso!", Toast.LENGTH_SHORT).show();
                    listarProdutos();
                    textoDescricao.setText("");
                    textoQuantidade.setText("");
                    textoPrecoUnitario.setText("");
                    radioGroup.clearCheck();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                FecharConexao();
            }
        }

    }

    private void listarProdutos() {
        try {
            AbrirConexao();
            Cursor cursor = bancoDados.rawQuery("SELECT id,descricao, quantidade, precoUnitario, precoUnitario*quantidade, disponivelVenda FROM estoque  ORDER BY id ASC", null);

            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaDescricao = cursor.getColumnIndex("descricao");
            int indiceColunaQuantidade = cursor.getColumnIndex("quantidade");
            int indiceColunaPrecoUnitario = cursor.getColumnIndex("precoUnitario");
            int indiceColunaPrecoEstoque = cursor.getColumnIndex("precoUnitario*quantidade");
            int indiceColunaDisponivelVenda = cursor.getColumnIndex("disponivelVenda");

            itens = new ArrayList<>();
            ids = new ArrayList<>();
            descricoes = new ArrayList<>();
            quantidades = new ArrayList<>();
            precosUnitarios = new ArrayList<>();

            ArrayList<String> disponiveisVenda = new ArrayList<>();

            ArrayAdapter<String> itensAdaptador = new ArrayAdapter<>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    itens
            );

            listaProdutos.setAdapter(itensAdaptador);
            listaExcluir.setAdapter(itensAdaptador);
            listaAlterar.setAdapter(itensAdaptador);

            cursor.moveToFirst();

            while (!cursor.isLast()) {
                cursor.moveToNext();

                String d = ".";

                if (cursor.getString(indiceColunaDisponivelVenda).equals("Não")) {
                    d = "Valor Indisponível";
                } else {
                    d = "  Valor em Estoque: R$" + cursor.getString(+indiceColunaPrecoEstoque);
                }

                itens.add("Nome: " + cursor.getString(indiceColunaDescricao) + "  " + d);
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                descricoes.add(cursor.getString(indiceColunaDescricao));
                quantidades.add(cursor.getString(indiceColunaQuantidade));
                precosUnitarios.add(cursor.getString(indiceColunaPrecoUnitario));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            FecharConexao();
        }
    }

    private void excluirProdutos(Integer id, String descricao) {
        try {
            AbrirConexao();
            bancoDados.execSQL("DELETE FROM estoque WHERE id=" + id);
            Toast.makeText(MainActivity.this, descricao + " removido(a) com sucesso.", Toast.LENGTH_LONG).show();
            listarProdutos();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            FecharConexao();
        }
    }

    private void alterarProduto(String descricao, Integer quantidade, Double precoUnitario, String disponivelVenda) {
        AbrirConexao();
        bancoDados.execSQL("UPDATE estoque SET descricao='" + descricao + "', quantidade='" + quantidade + "', precoUnitario='" + precoUnitario + "', disponivelVenda='" + disponivelVenda + "' WHERE descricao='" + descricao + "'");
        Toast.makeText(MainActivity.this, descricao + " atualizado(a) com Sucesso!", Toast.LENGTH_SHORT).show();
        listarProdutos();
        textoDescricao2.setText("");
        textoQuantidade2.setText("");
        textoPrecoUnitario2.setText("");
        radioGroupAlterar.clearCheck();
        FecharConexao();
    }
}