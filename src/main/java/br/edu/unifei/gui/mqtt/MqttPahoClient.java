package br.edu.unifei.gui.mqtt;

import br.edu.unifei.utils.LogTextAreaUtils;
import br.edu.unifei.utils.LogUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Implementação do {@link MqttClientStrategy} usando a biblioteca Paho.
 */
public class MqttPahoClient implements MqttClientStrategy {
    private MqttClient client;

    /**
     * @param broker      O URL do broker MQTT.
     * @param clientId    O ID do cliente.
     * @param logTextArea Utilitário para registrar logs na interface.
     */
    public MqttPahoClient(String broker, String clientId, LogTextAreaUtils logTextArea) {
        try {
            this.client = new MqttClient(broker, clientId);
            this.client.setCallback(new MqttCallbackHandler(logTextArea, this));
            this.client.connect();
            LogUtils.logInfo("Cliente MQTT conectado com o clientid %s", clientId);
        } catch (MqttException e) {
            LogUtils.logError("Erro de conexão: %s", e.getMessage());
        }
    }

    /**
     * Publica uma mensagem em um tópico especificado.
     *
     * @param topico   O tópico no qual a mensagem será publicada.
     * @param mensagem A mensagem a ser publicada.
     */
    @Override
    public void publish(String topico, String mensagem) {
        try {
            MqttMessage mqttMessage = new MqttMessage(mensagem.getBytes());
            mqttMessage.setQos(2); // Qualidade de serviço 2 (garante entrega)
            client.publish(topico, mqttMessage);
            LogUtils.logInfo("Mensagem publicada no topico %s: %s", topico, mensagem);
        } catch (MqttException e) {
            LogUtils.logError("Falha ao publica mensagem: ", e.getMessage());
        }
    }

    /**
     * Inscreve o cliente em um tópico especificado.
     *
     * @param topico O tópico ao qual o cliente deseja se inscrever.
     */
    public void subscribe(String topico) {
        try {
            client.subscribe(topico);
            LogUtils.logInfo("Inscrito no tópico: " + topico);
        } catch (MqttException e) {
            LogUtils.logError("Falha ao inscrever no tópico %s: ", topico, e.getMessage());
        }
    }

    /**
     * Desconecta o cliente do broker MQTT.
     */
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                LogUtils.logInfo("Cliente MQTT desconectado.");
            }
        } catch (MqttException e) {
            LogUtils.logError("Falha ao desconectar do broker MQTT: ", e.getMessage());
        }
    }
}
