package br.edu.unifei;

import br.edu.unifei.gui.coap.CoapApp;
import br.edu.unifei.gui.mqtt.MqttClientApp;
import br.edu.unifei.utils.LogUtils;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principal da aplicação. Responsável por configurar o tema visual da interface gráfica e inicializar a
 * interface de escolha de protocolo.
 */
public class App {

    /**
     * Método principal da aplicação, responsável por configurar o Look and Feel e iniciar a interface de seleção
     * de protocolo.
     *
     * @param args Argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        try {
            LafManager.install(new DarculaTheme());
        } catch (Exception e) {
            LogUtils.logError("Erro na criação do painel inicial: %s", e.getMessage());
        }

        SwingUtilities.invokeLater(App::criarInterfaceDeSelecao);
    }

    /**
     * Cria e exibe a interface de seleção inicial, onde o usuário pode escolher entre os protocolos CoAP e MQTT ou
     * encerrar a aplicação.
     */
    public static void criarInterfaceDeSelecao() {
        // Frame principal
        JFrame frame = new JFrame("Aplicação Servidor - Seleção de Protocolo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 150);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Bloquear o redimensionamento
        frame.setResizable(false);

        // Botão para selecionar o protocolo CoAP
        JButton coapButton = new JButton("CoAP");
        coapButton.setPreferredSize(new Dimension(120, 40));
        coapButton.setFont(new Font("Arial", Font.BOLD, 13));
        coapButton.addActionListener(e -> {
            iniciarAplicacao("coap");
            frame.dispose();
        });
        frame.add(coapButton);

        // Botão para selecionar o protocolo MQTT
        JButton mqttButton = new JButton("MQTT");
        mqttButton.setPreferredSize(new Dimension(120, 40));
        mqttButton.setFont(new Font("Arial", Font.BOLD, 13));
        mqttButton.addActionListener(e -> {
            iniciarAplicacao("mqtt");
            frame.dispose();
        });
        frame.add(mqttButton);

        // Botão para sair
        JButton sairButton = new JButton("Sair");
        sairButton.setPreferredSize(new Dimension(250,30));
        sairButton.setFont(new Font("Arial", Font.BOLD, 13));
        sairButton.addActionListener(e -> System.exit(0));
        frame.add(sairButton);

        // Configuração do frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Inicia a aplicação com base no protocolo especificado.
     *
     * @param protocolo O protocolo de comunicação a ser utilizado (CoAP ou MQTT)
     */
    private static void iniciarAplicacao(String protocolo) {
        switch (protocolo.toLowerCase()) {
            case "coap":
                new CoapApp();
                LogUtils.logInfo("Aplicação iniciada com o protocolo CoAP.");
                break;
            case "mqtt":
                new MqttClientApp();
                LogUtils.logInfo("Aplicação iniciada com o protocolo MQTT.");
                break;
            default:
                JOptionPane.showMessageDialog(null, "Protocolo não suportado: " + protocolo,
                        "Erro", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }
}
