package br.edu.unifei.gui.mqtt;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.tratamento.TratadorMensagem;
import br.edu.unifei.tratamento.TratadorMensagemFactory;
import br.edu.unifei.utils.LogTextAreaUtils;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonPrimitive;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Classe responsável por gerenciar as chamadas de retorno (callbacks) do cliente MQTT.
 */
public class MqttCallbackHandler implements MqttCallback {

    private final LogTextAreaUtils logTextArea;
    private final MqttPahoClient mqttClient;

    /**
     * @param logTextArea Utilitário para registrar logs na interface.
     * @param mqttClient  O cliente MQTT associado a este handler.
     */
    public MqttCallbackHandler(LogTextAreaUtils logTextArea, MqttPahoClient mqttClient) {
        this.logTextArea = logTextArea;
        this.mqttClient = mqttClient;
    }

    /**
     * Chamado quando a conexão com o broker é perdida.
     *
     * @param cause A causa da perda da conexão.
     */
    @Override
    public void connectionLost(Throwable cause) {
        LogUtils.logWarn("Conexão perdida! %s", cause);
        logTextArea.adicionarLog("Conexão perdida: " + cause.getMessage() + "\n");
    }

    /**
     * Chamado quando uma mensagem chega ao cliente MQTT.
     *
     * @param topico   O tópico por onde a mensagem foi recebida.
     * @param mensagem A mensagem recebida.
     * @throws InterruptedException Se a thread for interrompida.
     */
    @Override
    public void messageArrived(String topico, MqttMessage mensagem) throws InterruptedException {
        Thread.sleep(50);

        Mensagem mensagemRecebida = Mensagem.fromJson(mensagem.toString());
        mensagemRecebida.adicionarAoMetadata(
                "timestamp_servidor_msg_recebida",
                new JsonPrimitive(System.currentTimeMillis()));

        String acao = mensagemRecebida.getAcao();

        TratadorMensagem tratador = TratadorMensagemFactory.getTratador(acao);

        if (acao.contains("CONSULTAR")) {
            String resposta = tratador.processar(mensagemRecebida);
            mqttClient.publish("topico/barramento", resposta);

            String logResposta = String.format("Resposta gerada para %s: %s", mensagemRecebida.getOrigem(), resposta);
            LogUtils.logInfo(logResposta);
            logTextArea.adicionarLog(logResposta + "\n");
        } else {
            String textoRecebimento = tratador.processar(mensagemRecebida);
            LogUtils.logInfo(textoRecebimento);
            logTextArea.adicionarLog(textoRecebimento + "\n");
        }
    }

    /**
     * Chamado quando a entrega da mensagem é concluída.
     *
     * @param token O token que identifica a entrega da mensagem.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            LogUtils.logInfo("Entrega de mensagem completa! Token: " + token.toString());
        } catch (Exception e) {
            LogUtils.logError("Falha ao registrar entrega completa: ", e.getMessage());
        }
    }
}
