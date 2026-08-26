package com.Omar.imc.views;

import com.Omar.imc.server.ServidorTcp;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class VentanaPrincipal extends JFrame {

    private JButton btnIniciar;
    private JButton btnLimpiar;
    private JTextArea cajaLog;
    private JTextField campoIP;
    private JTextField campoPuerto;
    private JLabel txtEstado;

    public VentanaPrincipal (){
        setTitle("Servidor IMC");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents(){
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("SERVIDOR IMC");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPrincipal.add(titulo);
        panelPrincipal.add(Box.createVerticalStrut(20));

        JPanel panelConexion = new JPanel(new GridLayout(4, 2, 10, 10));
        panelConexion.setBorder(BorderFactory.createTitledBorder("Conexion"));

        panelConexion.add(new JLabel("DIRECCION IP:"));
        campoIP = new JTextField();
        campoIP.setEditable(false);
        panelConexion.add(campoIP);

        panelConexion.add(new JLabel("PUERTO DE RED:"));
        campoPuerto = new JTextField("9007");
        panelConexion.add(campoPuerto);

        panelConexion.add(new JLabel("Estado"));
        txtEstado = new JLabel("Detenido");
        txtEstado.setForeground(Color.RED);
        panelConexion.add(txtEstado);

        btnIniciar = new JButton("INICIAR");
        btnIniciar.setForeground(Color.GREEN);
        btnIniciar.addActionListener(e -> btnIniciarActionPerformed());
        panelConexion.add(btnIniciar);

        panelPrincipal.add(panelConexion);
        panelPrincipal.add(Box.createVerticalStrut(20));

        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("LOG DE CONEXIONES"));

        cajaLog = new JTextArea(10, 40);
        cajaLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cajaLog);
        panelLog.add(scrollPane, BorderLayout.CENTER);

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.addActionListener(e -> cajaLog.setText(""));
        panelLog.add(btnLimpiar, BorderLayout.SOUTH);

        panelPrincipal.add(panelLog);

        add(panelPrincipal);

        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            campoIP.setText(ip);
        }catch (UnknownHostException ex){
            JOptionPane.showMessageDialog(this, "Falla en la conexion");
        }
    }

    ServidorTcp s;

    private void btnIniciarActionPerformed(){
        if(btnIniciar.getText().equalsIgnoreCase("INICIAR")){
            int puerto = Integer.parseInt(campoPuerto.getText());
            s = new ServidorTcp(puerto, this);
            s.start();
        }else if(btnIniciar.getText().equalsIgnoreCase("DETENER")){
            s.detenerServicio();
        }
    }


    public JLabel getTxtEstado(){ return txtEstado; }
    public JTextArea getCajaLog() {return cajaLog; }
    public JButton getBtnIniciar() { return  btnIniciar; }

}