package br.edu.unifei.gui;

import br.edu.unifei.App;
import br.edu.unifei.modelos.mensagem.Mensagem;
import br.edu.unifei.utils.JsonTextAreaUtils;
import br.edu.unifei.utils.LogTextAreaUtils;
import br.edu.unifei.utils.LogUtils;
import br.edu.unifei.utils.ServicoUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Objects;

/**
 * Classe que define a interface comum.
 */
public abstract class GUIComponents {
    protected LogTextAreaUtils logTextArea;

    // Grupo: Janela principal
    protected JFrame frame;
    protected JPanel panel;
    protected JPanel bottonPanel;
    protected JButton cleanButton;
    protected JButton enviarButton;
    protected JButton voltarButton;
    protected JTextArea respostaArea;

    // Grupo: Ação
    protected JLabel acaoLabel;
    protected JComboBox<String> acaoComboBox;

    // Grupo: Conteúdo
    protected JPanel chaveValorPanel;
    protected JLabel conteudoLabel;
    protected JLabel chaveLabel;
    protected JLabel valorLabel;
    protected JTextField chaveField;
    protected JTextField valorField;
    protected JButton adicionarParButton;
    protected JButton removerParButton;
    protected JTextArea jsonArea;

    // Grupo: Destino
    protected JLabel destinoLabel;
    protected JTextField destinoField;

    // Grupo: Origem
    protected JLabel origemLabel;
    protected JTextField origemField;

    // Grupo: Serviço
    protected JLabel servicoLabel;
    protected JTextField servicoField;

    /**
     * Construtor da classe {@code UIComponents}. Inicializa a interface gráfica.
     */
    public GUIComponents() {
        Dimension dimensionPadrao = new Dimension(150, 24);

        // Configuração da janela principal
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 700);

        // Configuração do painel principal
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        enviarButton = new JButton("Enviar Mensagem");
        enviarButton.addActionListener(e -> enviarButtonActionListener());

        voltarButton = new JButton();
        voltarButton.setIcon(
                new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/revert.png")))
        );
        voltarButton.setToolTipText("Voltar");
        voltarButton.setFocusPainted(false);
        voltarButton.setBorderPainted(false);
        voltarButton.setContentAreaFilled(false);
        voltarButton.addActionListener(e -> voltarButtonActionListener());

        cleanButton = new JButton();
        cleanButton.setIcon(
                new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("img/delete.png")))
        );
        cleanButton.setToolTipText("Limpar");
        cleanButton.setFocusPainted(false);
        cleanButton.setBorderPainted(false);
        cleanButton.setContentAreaFilled(false);
        cleanButton.addActionListener(e -> cleanButtonActionListener());

        respostaArea = new JTextArea();
        respostaArea.setEditable(false);
        respostaArea.setLineWrap(true);
        respostaArea.setWrapStyleWord(true);
        respostaArea.setRows(10);
        JScrollPane scrollPaneRespostaArea = new JScrollPane(respostaArea);
        scrollPaneRespostaArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        logTextArea = new LogTextAreaUtils(respostaArea);

        bottonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottonPanel.add(voltarButton);
        bottonPanel.add(cleanButton);

        // Configuração do grupo de componentes "Ação"
        acaoLabel = new JLabel("Ação:");
        acaoComboBox = new JComboBox<>(new String[]{
                "BUSCAR_SERVICO", "CADASTRAR_ENDPOINT", "ENVIAR_ARQUIVOS_BASE", "ENVIAR_MENSAGEM",
                "LISTAR_SERVICOS", "REGISTRAR_SERVICO", "REMOVER_SERVICO"
        });
        acaoComboBox.setPreferredSize(dimensionPadrao);
        acaoComboBox.addActionListener(e -> acaoComboBoxActionListener());

        // Configuração do grupo de componentes "Origem"
        origemLabel = new JLabel("Origem:");
        origemField = new JTextField("");
        origemField.setPreferredSize(dimensionPadrao);

        // Configuração do grupo de componentes "Destino"
        destinoLabel = new JLabel("Destino:");
        destinoField = new JTextField("");
        destinoField.setPreferredSize(dimensionPadrao);

        // Configuração do grupo de componentes "Serviço"
        servicoLabel = new JLabel("Serviço:");
        servicoField = new JTextField("");
        servicoField.setPreferredSize(dimensionPadrao);

        // Configuração do grupo de componentes "Conteúdo"
        conteudoLabel = new JLabel("Conteúdo:");
        chaveValorPanel = new JPanel();
        chaveValorPanel.setLayout(new GridBagLayout());
        GridBagConstraints cvpGbc = new GridBagConstraints();
        cvpGbc.fill = GridBagConstraints.HORIZONTAL;
        cvpGbc.insets = new Insets(5, 5, 5, 5);

        chaveLabel = new JLabel("Chave");
        chaveField = new JTextField("");
        chaveField.setPreferredSize(new Dimension(100, 24));

        valorLabel = new JLabel("Valor");
        valorField = new JTextField("");
        valorField.setPreferredSize(new Dimension(100, 24));

        adicionarParButton = new JButton("Adicionar");
        adicionarParButton.addActionListener(e -> adicionarParButtonActionListener());

        removerParButton = new JButton("Remover");
        removerParButton.addActionListener(e -> removerParButtonActionListener());

        jsonArea = new JTextArea("{ }");
        jsonArea.setEditable(false);
        jsonArea.setLineWrap(true);
        jsonArea.setWrapStyleWord(true);
        JScrollPane jsonScrollPane = new JScrollPane(jsonArea);
        jsonScrollPane.setPreferredSize(new Dimension(400, 150));

        // Configuração do posicionamento dos componentes do painel de chave-valor
        adicionarComponente(0, 0, chaveLabel, chaveValorPanel, cvpGbc);
        adicionarComponente(1, 0, new JLabel(":"), chaveValorPanel, cvpGbc);
        adicionarComponente(2, 0, valorLabel, chaveValorPanel, cvpGbc);

        adicionarComponente(0, 1, chaveField, chaveValorPanel, cvpGbc);
        adicionarComponente(1, 1, new JLabel(":"), chaveValorPanel, cvpGbc);
        adicionarComponente(2, 1, valorField, chaveValorPanel, cvpGbc);

        cvpGbc.gridwidth = 1;
        adicionarComponente(0, 2, adicionarParButton, chaveValorPanel, cvpGbc);
        adicionarComponente(2, 2, removerParButton, chaveValorPanel, cvpGbc);

        // Configuração do posicionamento dos componentes no painel principal
        gbc.gridwidth = 1;
        adicionarComponente(0, 0, acaoLabel, panel, gbc);
        adicionarComponente(1, 0, acaoComboBox, panel, gbc);

        adicionarComponente(0, 1, origemLabel, panel, gbc);
        adicionarComponente(1, 1, origemField, panel, gbc);

        adicionarComponente(0, 2, destinoLabel, panel, gbc);
        adicionarComponente(1, 2, destinoField, panel, gbc);

        adicionarComponente(0, 2, servicoLabel, panel, gbc);
        adicionarComponente(1, 2, servicoField, panel, gbc);

        gbc.gridwidth = 2;
        adicionarComponente(0, 3, conteudoLabel, panel, gbc);
        adicionarComponente(0, 4, chaveValorPanel, panel, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        adicionarComponente(0, 6, jsonScrollPane, panel, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        adicionarComponente(0, 7, enviarButton, panel, gbc);

        // Aciona o listener do acaoComboBox
        acaoComboBoxActionListener();

        // Posiciona os componentes no frame
        frame.getContentPane().add(BorderLayout.NORTH, panel);
        frame.getContentPane().add(BorderLayout.CENTER, scrollPaneRespostaArea);
        frame.getContentPane().add(BorderLayout.SOUTH, bottonPanel);

        // Configura a lógica de encerramento e torna o frame visível
        frame.addWindowListener(customWindowClosing());
        frame.setVisible(true);
    }

    /**
     * @return O {@link LogTextAreaUtils} configurado.
     */
    public LogTextAreaUtils getLogTextArea() {
        return logTextArea;
    }

    /**
     * Adiciona um componente a um painel com configuração de {@link GridBagLayout} na linha e coluna indicadas.
     *
     * @param panel O {@link JPanel} onde o componente será adicionado.
     * @param component O {@link JComponent} a ser adicionado.
     * @param gridx A posição da coluna onde o componente será posicionado.
     * @param gridy A posição da linha onde o componente será posicionado.
     * @param gbc As restrições de layout do {@link GridBagLayout} que definem a posição e o tamanho do componente.
     */
    private void adicionarComponente(int gridx, int gridy, JComponent component, JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        if (component == null) {
            System.out.println("Componente é nulo: gridx = " + gridx + ", gridy = " + gridy);
        } else {
            panel.add(component, gbc);
        }
    }

    /**
     * Configura a lógica de encerramento quando a janela for fechada.
     *
     * @return A instância de {@link WindowAdapter} configurada com a lógica de encerramento.
     */
    private WindowAdapter customWindowClosing() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                customWindowClosingBehavior();
                System.exit(0);
            }
        };
    }

    /**
     * Coleta os dados preenchidos na interface e gera uma {@link Mensagem} com esses dados.
     *
     * @return A {@link Mensagem} em formato de string JSON.
     */
    private Mensagem gerarMensagemDaInterface() {
        String acao = (String) acaoComboBox.getSelectedItem();
        String origem = origemField.getText().trim();
        String destino = destinoField.getText().trim();
        JsonObject conteudo = new JsonObject();

        String jsonText = jsonArea.getText().trim();

        switch (Objects.requireNonNull(acao)) {
            case "REGISTRAR_SERVICO":
                try {
                    String nomeClasse = servicoField.getText().trim();
                    if (!nomeClasse.isEmpty()) {
                        conteudo = ServicoUtils.gerarMensagemDeRegistroDeServico(nomeClasse).getConteudo();
                    }
                } catch (IOException ex) {
                    String logErroRegistro = String.format(
                            "Erro: o nome do serviço não pode estar vazio. %s", ex.getMessage());
                    LogUtils.logError(logErroRegistro);
                    logTextArea.adicionarLog(logErroRegistro);
                }
                break;

            case "ENVIAR_MENSAGEM":
                if (!jsonText.equals("{ }")) {
                    try {
                        Gson gson = new Gson();
                        conteudo = gson.fromJson(jsonText, JsonObject.class);
                    } catch (JsonSyntaxException ex) {
                        LogUtils.logError("Erro ao parsear o JSON: %s", ex.getMessage());
                        JOptionPane.showMessageDialog(frame, "Erro ao parser o JSON: " + ex.getMessage());
                        return null;
                    }
                }
                break;

            case "BUSCAR_SERVICO":
            case "REMOVER_SERVICO":
                conteudo.addProperty("servico", servicoField.getText().trim());
                break;

            default:
                break;
        }

        return new Mensagem(acao, origem, destino, conteudo);
    }

    /**
     * Configura o {@link java.awt.event.ActionListener} para o componente {@code acaoComboBox}.
     */
    private void acaoComboBoxActionListener() {
        destinoLabel.setVisible(false);
        destinoField.setVisible(false);
        servicoLabel.setVisible(false);
        servicoField.setVisible(false);
        conteudoLabel.setVisible(false);
        chaveValorPanel.setVisible(false);
        jsonArea.setVisible(false);

        String acaoSelecionada = (String) acaoComboBox.getSelectedItem();
        switch (Objects.requireNonNull(acaoSelecionada)) {
            case "ENVIAR_MENSAGEM":
                destinoField.setVisible(true);
                destinoLabel.setVisible(true);
                conteudoLabel.setVisible(true);
                chaveValorPanel.setVisible(true);
                jsonArea.setVisible(true);
                break;

            case "REGISTRAR_SERVICO":
            case "BUSCAR_SERVICO":
            case "REMOVER_SERVICO":
                servicoLabel.setVisible(true);
                servicoField.setVisible(true);
                break;
        }

        frame.revalidate();
        frame.repaint();
    }

    /**
     * Configura o {@link java.awt.event.ActionListener} para o componente {@code adicionarParButton}.
     */
    private void adicionarParButtonActionListener() {
        String chave = chaveField.getText().trim();
        String valor = valorField.getText().trim();
        if (!chave.isEmpty()) {
            jsonArea.setText(JsonTextAreaUtils.adicionarParAoJson(jsonArea.getText().trim(), chave, valor));
            chaveField.setText("");
            valorField.setText("");
        }
    }

    /**
     * Configura o {@link java.awt.event.ActionListener} para o componente {@code removerParButton}.
     */
    private void removerParButtonActionListener() {
        String chave = chaveField.getText().trim();
        if (!chave.isEmpty()) {
            jsonArea.setText(JsonTextAreaUtils.removerParDoJson(jsonArea.getText().trim(), chave));
            chaveField.setText("");
            valorField.setText("");
        }
    }

    /**
     * Configura o {@link java.awt.event.ActionListener} para o componente {@code voltarButton}.
     */
    private void voltarButtonActionListener() {
        customVoltarButtonAction();
        JFrame frameAtual = (JFrame) SwingUtilities.getWindowAncestor(voltarButton);
        if (frameAtual != null) {
            frameAtual.dispose();
        }
        App.criarInterfaceDeSelecao();
    }

    /**
     * Configura o {@link java.awt.event.ActionListener} para o componente {@code cleanButton}.
     */
    private void cleanButtonActionListener() {
        respostaArea.setText("");
    }

    /**
     * Configura o {@link java.awt.event.ActionListener} para o componente {@code enviarButton}.
     */
    private void enviarButtonActionListener() {
        Mensagem mensagem = gerarMensagemDaInterface();
        assert mensagem != null;
        logTextArea.adicionarLog("Enviando mensagem: " + mensagem.toJson());
        customEnviarButtonAction(mensagem);
        logTextArea.adicionarLog("Aguardando resposta...");
    }

    protected abstract void customEnviarButtonAction(Mensagem mensagem);
    protected abstract void customVoltarButtonAction();
    protected abstract void customWindowClosingBehavior();
}
