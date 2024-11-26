package br.edu.unifei.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * Fornce métodos utilitários para manipular objetos JSON armazenados em {@link javax.swing.JTextArea};
 */
public class JsonTextAreaUtils {

    /**
     * Adiciona um par chave-valor ao JSON fornecido.
     *
     * @param jsonText O texto JSON atual.
     * @param chave A chave a ser adicionada.
     * @param valor O valor correspondente à chave.
     * @return Uma string JSON formatada com o novo par adicionado.
     */
    public static String adicionarParAoJson(String jsonText, String chave, String valor) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(jsonText, JsonObject.class);

            jsonObject.addProperty(chave, valor);

            return gson.toJson(jsonObject);
        }catch (JsonSyntaxException e) {
            LogUtils.logError("Não foi possível adicionar o par chave-valor ao conteúdo: %s", e.getMessage());
            return jsonText;
        }
    }

    /**
     * Remove um par chave-valor do JSON fornecido.
     *
     * @param jsonText O texto JSON atual.
     * @param chave A chave a ser removida.
     * @return Uma string JSON após a remoção do par, ou "{ }" se o JSON ficar vazio.
     */
    public static String removerParDoJson(String jsonText, String chave) {
        if (jsonText.equals("{ }")) {
            return jsonText;
        }

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject jsonObject = gson.fromJson(jsonText, JsonObject.class);

            jsonObject.remove(chave);

            if (jsonObject.isEmpty()) {
                return "{ }";
            }

            return gson.toJson(jsonObject);
        } catch (JsonSyntaxException e) {
            LogUtils.logError("Não foi possível remover chave: %s", e.getMessage());
            return jsonText;
        }
    }
}
