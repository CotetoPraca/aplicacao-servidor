package br.edu.unifei.tratamento;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.FileUtils;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;

/**
 * Responsável por processar mensagens relacionadas ao resultado de buscas, lidando com erros e salvando arquivos quando
 * necessário.
 */
public class TratadorResultadoBusca implements TratadorMensagem {

    /**
     * Processa uma mensagem contendo o resultado de uma busca e lida com possíveis erros ou salva arquivos conforme
     * necessário.
     *
     * @param mensagem A {@link Mensagem} contendo o resultado da busca.
     * @return Uma mensagem indicando o sucesso ou erro do processamento.
     */
    @Override
    public String processar(Mensagem mensagem) {
        JsonObject conteudo = mensagem.getConteudo();

        if (conteudo.has("erro")) {
            return conteudo.get("erro").getAsString();
        } else {
            String nomeServico = conteudo.get("nome").getAsString();
            String bytecode = conteudo.get("bytecode").getAsString();

            try {
                FileUtils.decodeAndSaveFile("/servicos/", nomeServico + ".java", bytecode);
            } catch (Exception e) {
                LogUtils.logError("Erro na decodificação e extração do arquivo do serviço %s: %s", nomeServico, e.getMessage());
                throw new RuntimeException(e);
            }
            return String.format("Arquivo %s recebido e salvo como /servicos/%s.java", nomeServico, nomeServico);
        }
    }
}
