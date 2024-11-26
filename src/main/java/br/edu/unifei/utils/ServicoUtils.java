package br.edu.unifei.utils;

import br.edu.unifei.modelos.mensagem.Mensagem;
import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Fornece métodos utilitários para manipulação de serviços, incluindo a geração de mensagens de registro de serviços.
 */
public class ServicoUtils {

    /**
     * Gera uma mensagem de registro de serviço a partir de uma classe específica. O bytecode da classe é convertido
     * para base64 e adicionado à mensagem.
     *
     * @param nomeClasse O nome da classe do serviço a ser registrado.
     * @return A {@link Mensagem} representando a solicitação de registro do serviço.
     * @throws IOException Se ocorrer um erro ao ler o arquivo da classe apontada.
     */
    public static Mensagem gerarMensagemDeRegistroDeServico(String nomeClasse) throws IOException {
        // Obter o caminho da classe e converter para bytecode
        String caminhoClasse = "/servicos/" + nomeClasse + ".java";
        String bytecodeBase64 = FileUtils.getFileAsBytecode(caminhoClasse);

        // Criar uma mensagem para registrar o serviço
        JsonObject conteudo = new JsonObject();
        conteudo.addProperty("servico", nomeClasse);
        conteudo.addProperty("bytecode", bytecodeBase64);

        return new Mensagem("REGISTRAR_SERVICO", "topico/servidor", "topico/barramento", conteudo);
    }
}
