package br.edu.unifei.tratamento;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Essa classe é uma fábrica responsável por gerar instâncias de {@link TratadorMensagem} com base na ação especificada.
 */
public class TratadorMensagemFactory {

    /**
     * Retorna uma instância de {@link TratadorMensagem} com base na ação fornecida.
     *
     * @param acao A ação para a qual um tratador de mensagem deve ser retornado.
     * @return Uma instância de uma das implementações de {@link TratadorMensagem} correspondente à ação.
     */
    public static TratadorMensagem getTratador(String acao) {
        switch (acao) {
            case "ARQUIVOS_BASE":
                return new TratadorArquivosBase();
            case "RESULTADO_BUSCA":
                return new TratadorResultadoBusca();
            case "CONSULTAR_ACAO":
                return new TratadorConsultarAcaoAPI();
            case "CONSULTAR_MOEDA":
                return new TratadorConsultarMoedaAPI();
            case "ENVIAR_MENSAGEM":
                return mensagem -> {
                    String conteudo = mensagem.getConteudo().toString();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject json = gson.fromJson(conteudo, JsonObject.class);
                    return "Mensagem recebida de '" + mensagem.getOrigem() + "'. " +
                            "Conteúdo da mensagem:\n" + gson.toJson(json);
                };
            default:
                return mensagem -> {
                    JsonObject conteudo = mensagem.getConteudo();
                    return conteudo.has("resultado")
                            ? conteudo.get("resultado").getAsString()
                            : String.format("A ação %s não possui tratamento definido.", mensagem.getAcao());
                };
        }
    }
}
