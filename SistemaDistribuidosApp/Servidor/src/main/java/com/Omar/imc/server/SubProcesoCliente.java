package com.Omar.imc.server;
import com.Omar.imc.model.CalculoImc;
import com.Omar.imc.views.VentanaPrincipal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubProcesoCliente extends Thread{

    private Socket client;
    private String ip;
    private VentanaPrincipal ventana;

    public SubProcesoCliente(Socket client, VentanaPrincipal v){
        this.client = client;
        ip = client.getInetAddress().getHostAddress();
        ventana = v;
    }
    @Override
    public void run(){
        try{
            CalculoImc.Imc imc = calcularImc();
            enviarRespuesta(imc);
        }catch (Exception ex){
            System.out.println(log()+ex.getMessage());
            ventana.getCajaLog().append(log()+ex.getMessage() + "\n");
            try {
                client.close();
            }catch (IOException ex1){
                ServidorTcp.listaDeClientes.remove(ip);
            }finally{
                ServidorTcp.listaDeClientes.remove(ip);
            }
        }
    }

    public CalculoImc.Imc calcularImc() throws Exception {
        DataInputStream input = null;
        try {
            input = new DataInputStream(client.getInputStream());
            String msg = "Esperando el PESO: ";
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n" + "\n");
            float peso = input.readFloat();
            msg = "PESO: " + peso;
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n");
            msg = "Esperando La Altura: ";
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n");
            float altura = input.readFloat();
            msg = "ALTURA: " + altura;
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n");
            CalculoImc datosImc = new CalculoImc(peso, altura);
            System.out.println(log()+"IMC: " + datosImc.getImc().resultado);
            msg = "IMC: " + datosImc.getImc().resultado;
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n");
            System.out.println(log()+"MENSAJE: " + datosImc.getImc().mensaje);
            msg = "MENSAJE: " + datosImc.getImc().mensaje;
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n");
            return datosImc.getImc();
        }catch (IOException ex){
            String msg = "Error al capturar datos del cliente" + ip;
            System.out.println(log()+msg);
            ventana.getCajaLog().append(log()+msg + "\n");
            throw new Exception("Error al capturar datos del  cliente " + ip);
        }
    }

    public void enviarRespuesta(CalculoImc.Imc imc){
        Thread hiloResponde = new Thread(){
            @Override
            public void run(){
                DataOutputStream output = null;
                try{
                    output = new DataOutputStream(client.getOutputStream());
                    output.writeFloat(imc.resultado);
                    output.writeUTF(imc.mensaje);
                    String msg = "IMC: " + imc.resultado;
                    System.out.println(log()+msg);
                    ventana.getCajaLog().append(log()+msg + "\n");
                    msg = "MENSAJE: " + imc.mensaje;
                    System.out.println(log()+msg);
                    ventana.getCajaLog().append(log()+msg + "\n");
                    output.flush();
                    enviarRespuesta(calcularImc());
                } catch (IOException ex) {
                    String msg = "Error al enviar datos al cliente " + ip;
                    System.out.println(log()+msg);
                    ventana.getCajaLog().append(log()+msg + "\n");
                    ServidorTcp.listaDeClientes.remove(ip);
                } catch (Exception e){
                    String msg = "Error al leer datos del cliente " + ip;
                    System.out.println(log()+msg);
                    ventana.getCajaLog().append(log()+msg + "\n");

                    try{
                        client.close();
                    } catch (IOException ex1) {
                        ServidorTcp.listaDeClientes.remove(ip);
                    } finally {
                        ServidorTcp.listaDeClientes.remove(ip);
                    }
                }
            }
        };
        hiloResponde.start();
    }

    public Socket getClient(){
        return client;
    }

    public void setClient(Socket client){
        this.client = client;
    }

    public String log(){
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        return ip + "->" + f.format(new Date()) + "-";
    }

}
