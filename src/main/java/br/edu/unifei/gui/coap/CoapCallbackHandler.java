package br.edu.unifei.gui.coap;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.tratamento.TratadorMensagem;
import br.edu.unifei.tratamento.TratadorMensagemFactory;
import br.edu.unifei.utils.LogTextAreaUtils;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonPrimitive;
import com.mbed.coap.packet.CoapRequest;
import com.mbed.coap.packet.CoapResponse;
import com.mbed.coap.packet.Code;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static com.mbed.coap.packet.CoapResponse.coapResponse;

/**
 * Classe responsável por gerenciar as chamadas de retorno (callbacks) para solicitações CoAP.
 */
public class CoapCallbackHandler {

    private final LogTextAreaUtils logTextArea;
    private final CoapHandler coapHandler;

    /**
     * @param logTextArea Utilitário para registrar logs na interface.
     * @param coapHandler Manipulador CoAP que gerencia e configura o protocolo CoAP.
     */
    public CoapCallbackHandler(LogTextAreaUtils logTextArea, CoapHandler coapHandler) {
        this.logTextArea = logTextArea;
        this.coapHandler = coapHandler;
    }

    /**
     * Método para lidar com requisições CoAP recebidas.
     *
     * @param req Requisição CoAP que contém a mensagem a ser processada.
     * @return Um {@link CompletableFuture} com a resposta CoAP.
     */
    public CompletableFuture<CoapResponse> handleRequest(CoapRequest req) {
        try {
            String payload = new String(req.getPayload().getBytes(), StandardCharsets.UTF_8);
            LogUtils.logInfo("Mensagem recebida via CoAP: %s", payload);

            Mensagem mensagemRecebida = Mensagem.fromJson(payload);
            mensagemRecebida.adicionarAoMetadata(
                    "timestamp_servidor_msg_recebida",
                    new JsonPrimitive(System.currentTimeMillis()));

            String acao = mensagemRecebida.getAcao();

            TratadorMensagem tratador = TratadorMensagemFactory.getTratador(acao);

            if (acao.contains("CONSULTAR")) {
                String resposta = tratador.processar(mensagemRecebida);
                coapHandler.sendMessage(resposta);

                String logResposta = String.format("Resposta gerada para %s:%s", mensagemRecebida.getOrigem(), resposta);
                LogUtils.logInfo(logResposta);
                logTextArea.adicionarLog(logResposta + "\n");
            } else {
                String textoRecebimento = tratador.processar(mensagemRecebida);
                LogUtils.logInfo(textoRecebimento);
                logTextArea.adicionarLog(textoRecebimento + "\n");
            }

            return coapResponse(Code.C204_CHANGED).toFuture();
        } catch (Exception e) {
            String logErroProcessarMensagem = String.format("Erro ao processar mensagem recebida: %s", e.getMessage());
            LogUtils.logError(logErroProcessarMensagem);
            logTextArea.adicionarLog(logErroProcessarMensagem + "\n");
            return coapResponse(Code.C500_INTERNAL_SERVER_ERROR).toFuture();
        }
    }
}
