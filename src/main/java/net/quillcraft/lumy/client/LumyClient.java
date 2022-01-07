package net.quillcraft.lumy.client;

import net.quillcraft.lumy.Lumy;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LumyClient{

    private final String ip;
    private final int port;

    public LumyClient(Lumy.Configuration lumyConfiguration){
        this.ip = lumyConfiguration.ip();
        this.port = lumyConfiguration.port();
        start();
    }

    public void start(){
        try{
            // getting localhost ip -> InetAddress.getByName(this.ip)
            // establish the connection with server port
            final Socket socket = new Socket(InetAddress.getByName(this.ip), port);

            try{
                // obtaining input and out streams
                final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeUTF("update");

                socket.close();
                dataOutputStream.close();
            }catch(IOException e){
                e.printStackTrace();
            }

        }catch(UnknownHostException e){
            System.err.println("Unknown Host");
        }catch(IOException e){
            System.err.println("Connexion Refused");
        }
    }

}