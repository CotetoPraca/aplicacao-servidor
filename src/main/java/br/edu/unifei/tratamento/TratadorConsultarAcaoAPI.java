package br.edu.unifei.tratamento;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.servicos.ServicoAlphaVantageAPI;
import com.google.gson.JsonPrimitive;

/**
 * Responsável por processar mensagens que solicitam a consulta de dados de ações através da API AlphaVantage.
 */
public class TratadorConsultarAcaoAPI implements TratadorMensagem {

    private final ServicoAlphaVantageAPI servicoAlphaVantageAPI;

    /**
     * Construtor da classe. Inicializa o serviço {@link ServicoAlphaVantageAPI} para consulta de ações.
     */
    public TratadorConsultarAcaoAPI() {
        this.servicoAlphaVantageAPI = new ServicoAlphaVantageAPI();
    }

    /**
     * Processa uma mensagem de consulta de ações e retorna os dados correspondentes.
     *
     * @param mensagem A {@link Mensagem} com os dados da ação a ser consultada.
     * @return Uma representação JSON da resposta da consulta.
     */
    @Override
    public String processar(Mensagem mensagem) {
        mensagem.adicionarAoMetadata(
                "timestamp_servidor_processamento_inicio",
                new JsonPrimitive(System.currentTimeMillis()));
        Mensagem resposta = servicoAlphaVantageAPI.executar(mensagem);
        resposta.adicionarAoMetadata(
                "timestamp_servidor_msg_enviada",
                new JsonPrimitive(System.currentTimeMillis()));
        return resposta.toJson();
    }
}
