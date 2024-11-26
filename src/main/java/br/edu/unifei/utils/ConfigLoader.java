package br.edu.unifei.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Responsável por carregar e fornecer as configurações da aplicação a partir de um arquivo de propriedades.
 */
public class ConfigLoader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                LogUtils.logError("Não foi possível encontrar o arquivo config.properties");
                System.exit(1);
            }
            properties.load(inputStream);
        } catch (Exception e) {
            LogUtils.logError("Erro ao carregar arquivo de configuração: %s", e.getMessage());
        }
    }

    /**
     * Obtém o valor da configuração para a chave especificada.
     *
     * @param key A chave da configuração.
     * @return O valor correspondente à chave, ou {@code null} se a chave não for encontrada.
     */
    public static String getConfigValue(String key) {
        return properties.getProperty(key);
    }
}
