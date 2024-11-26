package br.edu.unifei;

import br.edu.unifei.utils.LogUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TesteAlphaVantageAPI {

    private static final String API_KEY = "RHYSFAJ1YNTF05RH";
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    public static void main(String[] args) {
        // Teste de cotação de moeda
        String moedaReferencia = "CAD";
        String moedaDestino = "BRL";
        double valorAConverter = 1.0;

        try {
            JsonObject resposta = consultarCotacao(moedaReferencia, moedaDestino);
            System.out.println("Resposta da API: " + resposta);

            // Se necessário, exiba o valor convertido
            if (resposta != null) {
                double taxaConversao = resposta.get("5. Exchange Rate").getAsDouble();
                System.out.println("Valor convertido de " + valorAConverter + " " + moedaReferencia + " para " + moedaDestino + ": " + (valorAConverter * taxaConversao));
            }
        } catch (Exception e) {
            LogUtils.logError("%s", e.getMessage());
        }

        // Testar consulta de ação (IBM)
        String simboloAcao = "IBM";

        try {
            JsonObject respostaAcao = consultarAcao(simboloAcao);
            System.out.println("Resposta da API para Ações: " + respostaAcao);

            System.out.println("Preço Atual da Ação: " + respostaAcao.get("precoAtual").getAsBigDecimal());
            System.out.println("Preço de Fechamento Anterior: " + respostaAcao.get("precoFechamentoAnterior").getAsBigDecimal());
            System.out.println("Máximo do Dia: " + respostaAcao.get("maxDia").getAsBigDecimal());
            System.out.println("Mínimo do Dia: " + respostaAcao.get("minDia").getAsBigDecimal());
            System.out.println("Volume: " + respostaAcao.get("volume").getAsLong());
        } catch (Exception e) {
            LogUtils.logError("%s", e.getMessage());
        }
    }

    private static JsonObject consultarCotacao(String moedaReferencia, String moedaDestino) throws Exception {
        String url = String.format("%s?function=CURRENCY_EXCHANGE_RATE&from_currency=%s&to_currency=%s&apikey=%s",
                BASE_URL, moedaReferencia, moedaDestino, API_KEY);

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            return jsonResponse.getAsJsonObject("Realtime Currency Exchange Rate");  // Retorna o JsonObject com a taxa de câmbio
        }
    }

    private static JsonObject consultarAcao(String simboloAcao) throws Exception {
        String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                BASE_URL, simboloAcao.trim().toUpperCase(), API_KEY);

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
            } else if (jsonResponse.has("Time Series (Daily)")) {
                JsonObject timeSeries = jsonResponse.getAsJsonObject("Time Series (Daily)");
                JsonObject latestData = timeSeries.entrySet().iterator().next().getValue().getAsJsonObject();

                respostaConteudo.addProperty("precoAtual", latestData.get("1. open").getAsBigDecimal());
                respostaConteudo.addProperty("precoFechamentoAnterior", latestData.get("4. close").getAsBigDecimal());
                respostaConteudo.addProperty("maxDia", latestData.get("2. high").getAsBigDecimal());
                respostaConteudo.addProperty("minDia", latestData.get("3. low").getAsBigDecimal());
                respostaConteudo.addProperty("volume", latestData.get("5. volume").getAsLong());
            } else {
                respostaConteudo.addProperty("erro", "Nenhum dado de ação encontrado.");
            }

            return respostaConteudo;
        }
    }
}
