package br.edu.unifei.utils;

import java.io.*;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    /**
     * Descodifica o bytecode Base64, verifica se é um arquivo ZIP e descompacta no caminho base.
     *
     * @param base64Bytecode O bytecode em Base64 representando o arquivo ZIP.
     * @param outputDir      O diretório de saída onde os arquivos descompactados serão salvos.
     * @throws IOException Se ocorrer um erro na leitura ou escrita dos arquivos.
     */
    public static void decodeAndExtractZip(String outputDir, String base64Bytecode) throws IOException {
        String baseDir = getResourcePath();

        // Decodificar o bytecode Base64 para um array de bytes
        byte[] zipBytes = Base64.getDecoder().decode(base64Bytecode);

        // Criar um InputStream para ler os bytes do ZIP
        ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);

        // Verificar se o bytecode representa um arquivo ZIP
        try (ZipInputStream zis = new ZipInputStream(bais)) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File outputFile = new File(baseDir + outputDir, zipEntry.getName());

                // Criar diretórios se necessário
                if (zipEntry.isDirectory()) {
                    if (!outputFile.mkdirs() && !outputFile.isDirectory()) {
                        throw new IOException("Não foi possível criar o diretório: " + outputFile);
                    }
                } else {
                    // Criar diretórios para o arquivo (caso esteja em uma subpasta)
                    File parentDir = outputFile.getParentFile();
                    if (!parentDir.exists() && !parentDir.mkdirs()) {
                        throw new IOException("Não foi possível criar o diretório: " + parentDir);
                    }

                    // Escrever o arquivo descompactado
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            LogUtils.logError("Erro ao descompactar o arquivo ZIP: %s", e.getMessage());
            throw e;
        }
    }

    public static void decodeAndSaveFile(String outputDir, String fileName, String base64Bytecode) throws IOException {
        String baseDir = getResourcePath();

        // Decodificar o bytecode Base64 para um array de bytes
        byte[] fileBytes = Base64.getDecoder().decode(base64Bytecode);

        // Criar o diretório de saída se ele não existir
        File outputFile = new File(baseDir + outputDir);
        if (!outputFile.exists() && !outputFile.mkdirs()) {
            throw new IOException("Falha ao criar o diretório: " + baseDir + outputDir);
        }

        // Especificar o caminho completo do arquivo
        File outputFilePath = new File(outputFile, fileName);

        // Gravar os bytes no arquivo
        try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            fos.write(fileBytes);
            LogUtils.logInfo("Arquivo salvo com sucesso: " + outputFilePath.getAbsolutePath());
        } catch (IOException e) {
            LogUtils.logError("Erro ao salvar o arquivo: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Converte um arquivo em bytecode Base64.
     *
     * @param fileName O nome do arquivo a ser convertido.
     * @return O bytecode do arquivo em formato Base64.
     * @throws IOException Se ocorrer um erro ao ler o arquivo.
     */
    public static String getFileAsBytecode(String fileName) throws IOException {
        String basePath = getResourcePath();
        File file = new File(basePath, fileName);

        if (!file.exists()) {
            LogUtils.logError("Arquivo %s não encontrado.", file.getAbsolutePath());
            return null;
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            int bytesRead = fileInputStream.read(fileBytes);
            if (bytesRead != fileBytes.length) {
                throw new IOException("Não foi possível ler o arquivo completamente: " + file.getAbsolutePath());
            }
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            LogUtils.logError("Erro ao converter o arquivo %s: %s", file.getAbsolutePath(), e.getMessage());
            throw e;
        }
    }

    /**
     * Obtém o caminho do diretório de recursos dentro do projeto.
     *
     * @return O caminho absoluto do diretório de recursos.
     * @throws IllegalStateException Se o diretório de recursos não for encontrado.
     */
    static String getResourcePath() {
        String basePath = System.getProperty("user.dir");
        File projectRoot = new File(basePath);

        File nucleoDir = new File(projectRoot, "/src/main/java/br/edu/unifei");

        if (!nucleoDir.exists()) {
            LogUtils.logError("Diretório %s não encontrado.", nucleoDir.getAbsolutePath());
            throw new IllegalStateException();
        }

        return nucleoDir.getAbsolutePath();
    }
}
