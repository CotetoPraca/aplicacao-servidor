package br.edu.unifei.gui.mqtt;

/**
 * Define as operações básicas para um cliente MQTT.
 */
public interface MqttClientStrategy {

    /**
     * Publica uma mensagem em um tópico especificado.
     *
     * @param topico   O tópico no qual a mensagem será publicada.
     * @param mensagem A mensagem a ser publicada.
     */
    void publish(String topico, String mensagem);

    /**
     * Inscreve o cliente em um tópico especificado.
     *
     * @param topico O tópico ao qual o cliente deseja se inscrever para receber mensagens.
     */
    void subscribe(String topico);

    /**
     * Desconecta o cliente do broker MQTT.
     */
    void disconnect();
}
