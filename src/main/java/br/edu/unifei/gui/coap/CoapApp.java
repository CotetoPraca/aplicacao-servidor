package br.edu.unifei.gui.coap;

import br.edu.unifei.gui.GUIComponents;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.ConfigLoader;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonPrimitive;

import java.io.IOException;

/**
 * Classe principal da aplicação CoAP, responsável pela interface gráfica.
 */
public class CoapApp extends GUIComponents {

    private final CoapHandler coapHandler;

    /**
     * Construtor da classe {@code CoapApp}. Inicializa a interface gráfica e o {@link CoapHandler}.
     */
    public CoapApp() {
        super();
        frame.setTitle("Aplicação Servidor - CoAP");
        origemField.setText(
                ConfigLoader.getConfigValue("coap.servidor.host") + ":"
                + ConfigLoader.getConfigValue("coap.servidor.porta")
                + ConfigLoader.getConfigValue("coap.servidor.endpoint")
        );

        coapHandler = new CoapHandler();
        try {
            coapHandler.start(logTextArea);
        } catch (IOException e) {
            LogUtils.logError("Erro ao iniciar o protocolo CoAP: %s", e.getMessage());
        }
    }

    @Override
    protected void customEnviarButtonAction(Mensagem mensagem) {
        mensagem.adicionarAoMetadata(
                "timestamp_servidor_msg_enviada",
                new JsonPrimitive(System.currentTimeMillis()));
        coapHandler.sendMessage(mensagem.toJson());

        if (mensagem.getAcao().equals("REGISTRAR_SERVICO")) {
            String logServicoEnviado = String.format("Serviço '%s' enviado para registro " +
                    "no barramento.", mensagem.getConteudo().get("servico").getAsString());
            LogUtils.logInfo(logServicoEnviado);
            getLogTextArea().adicionarLog(logServicoEnviado);
        }
    }

    @Override
    protected void customVoltarButtonAction() {
        try {
            coapHandler.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void customWindowClosingBehavior() {
        try {
            coapHandler.stop();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
