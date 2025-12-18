package br.edu.unifei.tratamento;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.servicos.ServicoAlphaVantageAPI;
import com.google.gson.JsonPrimitive;

/**
 * Responsável por processar mensagens que solicitam a consulta de taxas de câmbio entre moedas através da API
 * AlphaVantage.
 */
public class TratadorConsultarMoedaAPI implements TratadorMensagem {

    private final ServicoAlphaVantageAPI servicoAlphaVantageAPI;

    /**
     * Construtor da classe. Inicializa o serviço {@link ServicoAlphaVantageAPI} para consulta de taxa de câmbio.
     */
    public TratadorConsultarMoedaAPI() {
        this.servicoAlphaVantageAPI = new ServicoAlphaVantageAPI();
    }

    /**
     * Processa uma mensagem de consulta de taxa de câmbio e retorna os dados correspondentes obtidos da API
     * AlphaVantage.
     *
     * @param mensagem A {@link Mensagem} com os dados das moedas a serem consultadas.
     * @return Uma representação JSON da resposta da consulta.
     */
    @Override
    public String processar(Mensagem mensagem) {
        Mensagem resposta = servicoAlphaVantageAPI.executar(mensagem);
        resposta.adicionarAoMetadata(
                "timestamp_servidor_msg_enviada",
                new JsonPrimitive(System.currentTimeMillis()));
        return resposta.toJson();
    }
}
