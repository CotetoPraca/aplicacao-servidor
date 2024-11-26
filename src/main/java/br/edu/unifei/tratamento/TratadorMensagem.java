package br.edu.unifei.tratamento;

import br.edu.unifei.modelos.mensagem.Mensagem;

/**
 * Define o contrato para o processamento de mensagens. As classes que implementam esta interface devem fornecer uma
 * implementação do método {@code processar}, que recebe uma mensagem e retorna um resultado como uma {@link String}.
 */
public interface TratadorMensagem {

    /**
     * Processa uma mensagem e retorna o resultado como uma {@link String}.
     *
     * @param mensagem A {@link Mensagem} a ser processada
     * @return O resultado do processamento.
     */
    String processar(Mensagem mensagem);
}
