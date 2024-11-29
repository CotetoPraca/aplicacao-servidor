# Aplicação Servidor (S3B)

Aplicação servidor usada para testar as funcionalidades do Small Scale Service Bus (S3B).

As dependências são gerenciadas pelo Maven e estão listadas no arquivo `pom.xml`.

Ao iniciar, o servidor, se conecta ao broker MQTT do S3B, portanto ele deve ser executado
após a inicialização do barramento para funcionar corretamente. Sua inicialização é
feita a partir da classe `App`.

Se a aplicação de destino usar um protocolo diferente do configurado no servidor,
é preciso que a aplicação de destino envie uma mensagem com ação `CADASTRAR_ENDPOINT`
ao S3B usando o protocolo que deseja cadastrar como padrão para comunicações futuras.

O mesmo para atualizar o protocolo de algum endereço. Quando o S3B recebe uma
mensagem de um novo endereço, ele automaticamente armazena o protocolo usado como
a preferência para comunicações futuras. Para alterar essa configuração, basta
enviar pelo novo protocolo o `CADASTRAR_ENDPOINT` com o novo endereço.

Para o serviço de acesso a Alpha Vantage funcionar, é preciso adicionar a chave
de acesso no arquivo `config.properties` no diretório de recursos. A chave
pode ser obtida gratuitamente no site da [Alpha Vantage](https://www.alphavantage.co/).
O acesso gratuito é limitado, mas é o suficiente para consultar dados simples
de ação e contação de moedas.