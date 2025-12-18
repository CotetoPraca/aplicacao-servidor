package br.edu.unifei.gui.mqtt;

import br.edu.unifei.gui.GUIComponents;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.ConfigLoader;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonPrimitive;

/**
 * Classe principal da aplicação MQTT, responsável pela interface gráfica.
 */
public class MqttClientApp extends GUIComponents {

    private static final String SERVICE_BUS_TOPIC = ConfigLoader.getConfigValue("mqtt.barramento.topico");
    private static final String MQTT_BROKER_URL = ConfigLoader.getConfigValue("mqtt.broker.url");
    private static final String CLIENT_ID = ConfigLoader.getConfigValue("mqtt.cliente.id");
    private static final String CLIENT_TOPIC = ConfigLoader.getConfigValue("mqtt.cliente.topico");

    private final MqttClientStrategy mqttClient;

    /**
     * Construtor da classe {@code MqttClientApp}. Inicializa a interface gráfica e configura o cliente MQTT.
     */
    public MqttClientApp() {
        super();
        frame.setTitle("Aplicação Servidor - MQTT");
        origemField.setText("topico/servidor");

        mqttClient = getMqttClient();
        mqttClient.subscribe(CLIENT_TOPIC);
    }

    /**
     * Cria e retorna uma instância pré-configurada do {@link MqttPahoClient}.
     *
     * @return Uma instância de {@link MqttClientStrategy}.
     */
    private MqttClientStrategy getMqttClient() {
        return new MqttPahoClient(MQTT_BROKER_URL, CLIENT_ID, logTextArea);
    }

    @Override
    protected void customEnviarButtonAction(Mensagem mensagem) {
        mensagem.adicionarAoMetadata(
                "timestamp_servidor_msg_enviada",
                new JsonPrimitive(System.currentTimeMillis()));
        mqttClient.publish(SERVICE_BUS_TOPIC, mensagem.toJson());

        if (mensagem.getAcao().equals("REGISTRAR_SERVICO")) {
            String logServicoEnviado = String.format("Serviço '%s' enviado para registro " +
                    "no barramento.", mensagem.getConteudo().get("servico").getAsString());
            LogUtils.logInfo(logServicoEnviado);
            getLogTextArea().adicionarLog(logServicoEnviado);
        }
    }

    @Override
    protected void customVoltarButtonAction() {
        mqttClient.disconnect();
    }

    @Override
    protected void customWindowClosingBehavior() {
        mqttClient.disconnect();
    }
}
