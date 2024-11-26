package br.edu.unifei.servicos;

import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.modelos.servico.Servico;
import br.edu.unifei.utils.ConfigLoader;
import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Serviço de consulta a API da AlphaVantage para obter dados de ações e moedas. Este serviço é capaz de processar
 * mensagens para consultar informações financeiras, retornando os resultados no formato de mensagens.
 */
public class ServicoAlphaVantageAPI extends Servico {

    private static final String API_KEY = ConfigLoader.getConfigValue("servico.alpha_vantage_api.api_key");
    private static final String BASE_URL = ConfigLoader.getConfigValue("servico.alpha_vantage_api.base_url");

    /**
     * Construtor padrão. Para mais informações, acessar a classe pai: {@link Servico}.
     */
    public ServicoAlphaVantageAPI() {
        super();
    }

    /**
     * Executa o serviço com base na ação especificada na mensagem fornecida.
     * Suporta as ações "CONSULTAR_ACAO" e "CONSULTAR_MOEDA".
     *
     * @param mensagem A {@link Mensagem} de entrada contendo a ação e os parâmetros necessários.
     * @return A {@link Mensagem} de resposta contendo os resultados da operação.
     * @throws IllegalArgumentException Se a ação especificada não for suportada.
     */
    @Override
    public Mensagem executar(Mensagem mensagem) {
        LogUtils.logInfo("Processando mensagem: " + mensagem.toJson());

        String acao = mensagem.getAcao();
        Mensagem resposta;

        switch (acao) {
            case "CONSULTAR_ACAO":
                resposta = consultarAcao(mensagem);
                break;
            case "CONSULTAR_MOEDA":
                resposta = consultarMoeda(mensagem);
                break;
            default:
                throw new IllegalArgumentException("Ação desconhecida: " + acao);
        }

        return resposta;
    }

    /**
     * Consulta os dados de uma ação específica usando a API AlphaVantage.
     *
     * @param mensagem A {@link Mensagem} contendo o símbolo da ação a ser consultada.
     * @return Uma {@link Mensagem} de resposta com os dados da ação ou um erro, se houver.
     */
    private Mensagem consultarAcao(Mensagem mensagem) {
        String respostaDestino = mensagem.getOrigem();
        JsonObject conteudo = mensagem.getConteudo();
        String simboloAcao = conteudo.get("simboloAcao").getAsString();

        JsonObject metadata = conteudo.has("metadata")
                ? conteudo.getAsJsonObject("metadata")
                : new JsonObject();

        try {
            JsonObject respostaConteudo = getDadosAcao(simboloAcao);
            respostaConteudo.add("metadata", metadata);

            return new Mensagem(
                    "RESULTADO_CONSULTAR_ACAO",
                    mensagem.getDestino(),
                    respostaDestino,
                    respostaConteudo
            );
        } catch (IOException e) {
            LogUtils.logError("Erro ao consultar dados de ações: %s", e.getMessage());

            JsonObject respostaErro = new JsonObject();
            respostaErro.add("metadata", metadata);
            respostaErro.addProperty("erro",
                    String.format("Erro ao consultar dados da ação %s: %s", simboloAcao, e.getMessage())
            );

            return new Mensagem(
                    "RESULTADO_CONSULTAR_ACAO",
                    mensagem.getDestino(),
                    respostaDestino,
                    respostaErro
            );
        }
    }

    /**
     * Consulta a taxa de câmbio entre duas moedas usando a API AlphaVantage.
     *
     * @param mensagem A {@link Mensagem} contendo as informações das moedas a serem convertidas.
     * @return Uma {@link Mensagem} de resposta com os dados da cotação ou um erro, se houver.
     */
    private Mensagem consultarMoeda(Mensagem mensagem) {
        String respostaDestino = mensagem.getOrigem();
        JsonObject conteudo = mensagem.getConteudo();
        String moedaReferencia = conteudo.get("moedaReferencia").getAsString();
        String moedaDestino = conteudo.get("moedaDestino").getAsString();
        BigDecimal valorAConverter = conteudo.get("valorAConverter").getAsBigDecimal();

        JsonObject metadata = conteudo.has("metadata")
                ? conteudo.getAsJsonObject("metadata")
                : new JsonObject();

        try {
            JsonObject respostaConteudo = getDadosCotacao(moedaReferencia, moedaDestino, valorAConverter);
            respostaConteudo.add("metadata", metadata);

            return new Mensagem(
                    "RESULTADO_CONSULTAR_MOEDA",
                    mensagem.getDestino(),
                    respostaDestino,
                    respostaConteudo
            );
        } catch (IOException e) {
            LogUtils.logError("Erro ao consultar dados de cotação: %s", e.getMessage());

            JsonObject respostaErro = new JsonObject();
            respostaErro.add("metadata", metadata);
            respostaErro.addProperty("erro",
                    String.format("Erro ao consultar dados de cotação para as moedas %s/%s: %s",
                            moedaReferencia, moedaDestino, e.getMessage())
            );

            return new Mensagem(
                    "RESULTADO_CONSULTAR_MOEDA",
                    mensagem.getDestino(),
                    respostaDestino,
                    respostaErro
            );
        }
    }

    /**
     * Realiza uma consulta à API AlphaVantage para obter os dados diários de uma ação.
     *
     * @param simboloAcao O símbolo da ação a ser consultada.
     * @return Um objeto JSON contendo os dados da ação consultada.
     * @throws IOException Se ocorrer um erro na conexão com a API.
     */
    private static JsonObject getDadosAcao(String simboloAcao) throws IOException {
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                BASE_URL, simboloAcao.trim().toUpperCase(), API_KEY);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject respostaConteudo = new JsonObject();

            if (jsonResponse.has("Error Message")) {
                String errorMessage = jsonResponse.get("Error Message").getAsString();
                respostaConteudo.addProperty("erro", errorMessage);
            } else if (jsonResponse.has("Time Series (Daily)")) {
                JsonObject timeSeries = jsonResponse.getAsJsonObject("Time Series (Daily)");
                JsonObject latestData = timeSeries.entrySet().iterator().next().getValue().getAsJsonObject();

                respostaConteudo.addProperty(
                        "precoAtual",
                        latestData.get("1. open").getAsBigDecimal()
                );
                respostaConteudo.addProperty(
                        "precoFechamentoAnterior",
                        latestData.get("4. close").getAsBigDecimal()
                );
                respostaConteudo.addProperty(
                        "maxDia",
                        latestData.get("2. high").getAsBigDecimal()
                );
                respostaConteudo.addProperty(
                        "minDia",
                        latestData.get("3. low").getAsBigDecimal()
                );
                respostaConteudo.addProperty(
                        "volume",
                        latestData.get("5. volume").getAsLong()
                );
            } else {
                respostaConteudo.addProperty("erro", "Nenhum dado de ação encontrado.");
            }

            return respostaConteudo;
        }
    }

    /**
     * Realiza uma consulta à API AlphaVantage para obter a taxa de câmbio entre duas moedas.
     *
     * @param moedaReferencia O símbolo da moeda de referência.
     * @param moedaDestino    O símbolo da moeda de destino.
     * @param valorAConverter O valor a ser convertido.
     * @return Um objeto JSON contendo os dados da conversão.
     * @throws IOException Se ocorrer um erro na conexão com a API.
     */
    private static JsonObject getDadosCotacao(String moedaReferencia, String moedaDestino, BigDecimal valorAConverter) throws IOException {
        String url = String.format("%s?function=CURRENCY_EXCHANGE_RATE&from_currency=%s&to_currency=%s&apikey=%s",
                BASE_URL, moedaReferencia.trim().toUpperCase(), moedaDestino.trim().toUpperCase(), API_KEY);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject respostaConteudo = new JsonObject();

            if (jsonResponse.has("Error Message")) {
                String errorMessage = jsonResponse.get("Error Message").getAsString();
                respostaConteudo.addProperty("erro", errorMessage);
            } else if (jsonResponse.has("Realtime Currency Exchange Rate")) {
                JsonObject currencyExchangeRate = jsonResponse.getAsJsonObject("Realtime Currency Exchange Rate");

                respostaConteudo.addProperty(
                        "moedaReferenciaCodigo",
                        currencyExchangeRate.get("1. From_Currency Code").getAsString()
                );
                respostaConteudo.addProperty(
                        "moedaReferenciaNome",
                        currencyExchangeRate.get("2. From_Currency Name").getAsString()
                );
                respostaConteudo.addProperty(
                        "moedaDestinoCodigo",
                        currencyExchangeRate.get("3. To_Currency Code").getAsString()
                );
                respostaConteudo.addProperty(
                        "moedaDestinoNome",
                        currencyExchangeRate.get("4. To_Currency Name").getAsString()
                );
                respostaConteudo.addProperty(
                        "taxaDeCambio",
                        currencyExchangeRate.get("5. Exchange Rate").getAsBigDecimal()
                );
                respostaConteudo.addProperty(
                        "valorAConverter",
                        valorAConverter
                );
                respostaConteudo.addProperty(
                        "valorConvertido",
                        valorAConverter.multiply(respostaConteudo.get("taxaDeCambio").getAsBigDecimal())
                );
            } else {
                respostaConteudo.addProperty("erro", "Nenhum dado de cotação encontrado.");
            }

            return respostaConteudo;
        }
    }
}
