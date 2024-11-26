package br.edu.unifei.utils;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitária para registrar logs em uma área de texto {@link JTextArea}, adicionando timestamp às mensagens
 * de log.
 */
public class LogTextAreaUtils {

    private final JTextArea textArea;
    private final DateTimeFormatter dateTimeFormatter;

    /**
     * Construtor para associar a classe a um {@link JTextArea}.
     *
     * @param textArea O componente {@link JTextArea} onde os logs serão exibidos.
     */
    public LogTextAreaUtils(JTextArea textArea) {
        this.textArea = textArea;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    /**
     * Adiciona uma mensagem de log precedida por um timestamp no {@link JTextArea}.
     *
     * @param mensagem A mensagem de log a ser adicionada.
     */
    public void adicionarLog(String mensagem) {
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        String logMessage = String.format("[%s] %s", timestamp, mensagem);
        textArea.append(logMessage + "\n");
    }
}
