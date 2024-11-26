package br.edu.unifei.gui.coap;

import br.edu.unifei.utils.ConfigLoader;
import br.edu.unifei.utils.LogTextAreaUtils;
import br.edu.unifei.utils.LogUtils;
import com.mbed.coap.client.CoapClient;
import com.mbed.coap.packet.BlockSize;
import com.mbed.coap.packet.CoapRequest;
import com.mbed.coap.packet.CoapResponse;
import com.mbed.coap.packet.MediaTypes;
import com.mbed.coap.server.CoapServer;
import com.mbed.coap.server.RouterService;
import com.mbed.coap.transport.udp.DatagramSocketTransport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Classe responsável por configurar e gerenciar as interações entre o cliente e o servidor CoAP da aplicação.
 */
public class CoapHandler {
    private static final String SERVICE_BUS_ENDPOINT = ConfigLoader.getConfigValue("coap.barramento.endpoint");
    private static final String SERVICE_BUS_HOST = ConfigLoader.getConfigValue("coap.barramento.host");
    private static final int SERVICE_BUS_PORT = Integer.parseInt(ConfigLoader.getConfigValue("coap.barramento.porta"));
    private static final String SERVER_ENDPOINT = ConfigLoader.getConfigValue("coap.servidor.endpoint");
    private static final int SERVER_PORT = Integer.parseInt(ConfigLoader.getConfigValue("coap.servidor.porta"));

    private CoapClient coapClient;
    private CoapServer coapServer;

    /**
     * Inicia o servidor e o cliente CoAP.
     *
     * @param logTextArea Utilitário para registrar logs na interface.
     * @throws IOException Se houver um erro ao iniciar o servidor ou o cliente.
     */
    public void start(LogTextAreaUtils logTextArea) throws IOException {
        // Configura o servidor CoAP
        coapServer = CoapServer.builder()
                .transport(new DatagramSocketTransport(SERVER_PORT))
                .route(RouterService.builder()
                        .post(SERVER_ENDPOINT, new CoapCallbackHandler(logTextArea, this)::handleRequest)
                )
                .blockSize(BlockSize.S_1024) // Define o tamanho do bloco para 1024 bytes
                .maxIncomingBlockTransferSize(16384) // Transferência de até 16 KB
                .build();
        coapServer.start();
        LogUtils.logInfo("Servidor CoAP iniciado na porta %d", SERVER_PORT);

        // Configura o cliente CoAP
        coapClient = CoapServer.builder()
                .transport(DatagramSocketTransport.udp())
                .blockSize(BlockSize.S_1024) // Define o tamanho do bloco para 1024 bytes
                .maxIncomingBlockTransferSize(16384) // Transferência de até 16 KB
                .buildClient(new InetSocketAddress(SERVICE_BUS_HOST, SERVICE_BUS_PORT));
        LogUtils.logInfo("Cliente CoAP configurado para %s:%d", SERVICE_BUS_HOST, SERVICE_BUS_PORT);
    }

    /**
     * Envia uma mensagem CoAP para o barramento de serviço.
     *
     * @param mensagemJson A mensagem a ser enviada em formato JSON.
     */
    public void sendMessage(String mensagemJson) {
        try {
            CompletableFuture<CoapResponse> responseFuture = coapClient.send(
                    CoapRequest.post(SERVICE_BUS_ENDPOINT).payload(mensagemJson, MediaTypes.CT_APPLICATION_JSON)
            );

            responseFuture.thenAccept(response -> {
                String payload = response.getPayloadString();
                LogUtils.logInfo("Resposta recebida do servidor CoAP: %s", payload);
            }).exceptionally(e -> {
                LogUtils.logError("Erro ao enviar mensagem CoAP: %s", e.getMessage());
                return null;
            });
        } catch (Exception e) {
            LogUtils.logError("Erro ao enviar mensagem CoAP: %s", e.getMessage());
        }
    }

    /**
     * Interrompe o servidor e o cliente CoAP, liberando recursos.
     *
     * @throws IOException Se houver um erro ao interromper o servidor ou o cliente.
     */
    public void stop() throws IOException {
        if (coapClient != null) {
            coapClient.close();
            LogUtils.logInfo("Cliente CoAP encerrado.");
        }
        if (coapServer != null) {
            coapServer.stop();
            LogUtils.logInfo("Servidor CoAP parado.");
        }
    }
}
