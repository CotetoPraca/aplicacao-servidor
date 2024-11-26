package br.edu.unifei.tratamento;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.FileUtils;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Responsável por processar mensagens relacionadas à conversão e extração de arquivos base. Ele decodifica um bytecode
 * recebido e extrai os arquivos de um arquivo compactado.
 */
public class TratadorArquivosBase implements TratadorMensagem {

    /**
     * Processa uma mensagem contendo um bytecode e realiza a decodificação e extração dos arquivos base.
     *
     * @param mensagem A {@link Mensagem} contendo o bytecode a ser processado.
     * @return Uma mensagem de sucesso indicando a conclusão do processamento.
     */
    @Override
    public String processar(Mensagem mensagem) {
        JsonObject conteudo = mensagem.getConteudo();
        String bytecode = conteudo.get("resposta").getAsString();
        LogUtils.logDebug("Bytecode recebido: %s", bytecode);

        try {
            FileUtils.decodeAndExtractZip("/modelos/", bytecode);
        } catch (IOException e) {
            LogUtils.logError("Erro na decodificação e extração dos arquivos base: %s", e.getMessage());
            throw new RuntimeException(e);
        }
        return "Arquivos de servico convertidos e descompactados.";
    }
}
