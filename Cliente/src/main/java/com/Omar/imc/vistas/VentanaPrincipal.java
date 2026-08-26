package com.Omar.imc.vistas;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class VentanaPrincipal extends JFrame {
    private JButton btnIniciar;
    private JButton btnCalcular;
    private JTextField campoIPServidor;
    private JTextField campoPuertoServidor;
    private JTextField campoPeso;
    private JTextField campoAltura;
    private JLabel txtEstado;
    private JLabel txtResultado;
    private JLabel txtMensaje;

    private Socket servidor;
    private DataOutputStream out;
    private DataInputStream in;

    public VentanaPrincipal(){
        setTitle("CLIENTE IMC");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents(){
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelConexion = new JPanel(new GridLayout(4,2,10,10));
        panelConexion.setBorder(BorderFactory.createTitledBorder("CONEXION"));

        panelConexion.add(new JLabel("DIRECCION IP:"));
        campoIPServidor = new JTextField("localhost");
        panelConexion.add(campoIPServidor);

        panelConexion.add(new JLabel("PUERTO DE RED"));
        campoPuertoServidor = new JTextField("9007");
        panelConexion.add(campoPuertoServidor);

        panelConexion.add(new JLabel("ESTADO:"));
        txtEstado = new JLabel("Desconectado");
        txtEstado.setForeground(Color.RED);
        panelConexion.add(txtEstado);

        btnIniciar = new JButton("Conectar");
        btnIniciar.setForeground(Color.GREEN);
        btnIniciar.addActionListener(e -> btnIniciarActionPerformed());
        panelConexion.add(btnIniciar);

        tabbedPane.addTab("CONEXION", panelConexion);

        JPanel panelCalculo = new JPanel(new GridLayout(5,2,10,10));
        panelCalculo.setBorder(BorderFactory.createTitledBorder("CALCULAR IMC"));

        panelCalculo.add(new JLabel("PESO (KG):"));
        campoPeso = new JTextField();
        panelCalculo.add(campoPeso);

        panelCalculo.add(new JLabel("ALTURA (m):"));
        campoAltura = new JTextField();
        panelCalculo.add(campoAltura);

        btnCalcular = new JButton("CALCULAR");
        btnCalcular.setForeground(Color.GREEN);
        btnCalcular.addActionListener(e -> btnCalcularActionPerformed());
        panelCalculo.add(btnCalcular);

        panelCalculo.add(new JLabel("IMC:"));
        txtResultado = new JLabel("0.0");
        txtResultado.setForeground(Color.RED);
        txtResultado.setFont(new Font("Arial", Font.BOLD, 14));
        panelCalculo.add(txtResultado);

        panelCalculo.add(new JLabel("MENSAJE:"));
        txtMensaje = new JLabel("");
        panelCalculo.add(txtMensaje);

        tabbedPane.addTab("CALCULAR IMC", panelCalculo);

        add(tabbedPane);
    }

    private void btnIniciarActionPerformed(){
        String ip = campoIPServidor.getText();
        int puerto = Integer.parseInt(campoPuertoServidor.getText());

        try{
            if(btnIniciar.getText().equalsIgnoreCase("Conectar")){
                servidor = new Socket(ip, puerto);
                out = new DataOutputStream(servidor.getOutputStream());
                in = new DataInputStream(servidor.getInputStream());

                btnIniciar.setText("Desconectar");
                btnIniciar.setForeground(Color.RED);
                txtEstado.setText("Conectado");
                txtEstado.setForeground(Color.GREEN);

                JOptionPane.showMessageDialog(this, "Conectado al servidor");

            } else if(btnIniciar.getText().equalsIgnoreCase("Desconectar")){
                if(servidor.isConnected()){
                    servidor.close();
                }
                btnIniciar.setText("Conectar");
                btnIniciar.setForeground(Color.GREEN);
                txtEstado.setText("Desconectado");
                txtEstado.setForeground(Color.RED);

                JOptionPane.showMessageDialog(this, "Desconectado del servidor");

            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al conectar: " + ex.getMessage());
            System.out.println("ERROR AL CONECTAR");
            ex.printStackTrace();
        }
    }

    private void btnCalcularActionPerformed(){
        if(!servidor.isConnected()){
            JOptionPane.showMessageDialog(this, "Client Offline, Conecte con el servidor");
            return;
        }

        try {
            float peso = Float.parseFloat(campoPeso.getText());
            float altura = Float.parseFloat(campoAltura.getText());

            Thread hilo = new Thread(){
                @Override
                public void run(){
                    try {
                        System.out.println("Peso: "+ peso);
                        System.out.println("Altura: " + altura);

                        out.writeFloat(peso);
                        out.writeFloat(altura);
                        out.flush();

                        System.out.println("Enviando los datos\nEsperando respuesta");

                        float imc = in.readFloat();
                        String msj = in.readUTF();

                        System.out.println("IMC: " + imc + "\nMensaje: " + msj);

                        txtResultado.setText(String.format("%.2f", imc));
                        txtMensaje.setText(msj);


                    }catch (IOException ex) {
                        JOptionPane.showMessageDialog(VentanaPrincipal.this,
                                "ERROR con el cliente " + ex.getMessage());
                        System.out.println("ERROR con el cliente " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            };
            hilo.start();
        }catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Ingresa valores validos");
        }

    }
}
