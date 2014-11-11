package com.hankcs.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;


public class Peer
{
    public static void main(String[] args) throws Throwable
    {
        try
        {
            IceClient client = new IceClient(8888, "text");
            client.init();
            client.exchangeSdpWithPeer();
            client.startConnect();
            final DatagramSocket socket = client.getDatagramSocket();
            final SocketAddress remoteAddress = client
                    .getRemotePeerSocketAddress();
            System.out.println(socket.toString());
            new Thread(new Runnable()
            {

                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            byte[] buf = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(buf,
                                                                       buf.length);
                            socket.receive(packet);
                            System.out.println(packet.getAddress() + ":" + packet.getPort() + " says: " + new String(packet.getData(), 0, packet.getLength()));
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            new Thread(new Runnable()
            {

                public void run()
                {
                    try
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String line;
                        // 从键盘读取
                        while ((line = reader.readLine()) != null)
                        {
                            line = line.trim();
                            if (line.length() == 0)
                            {
                                break;
                            }
                            byte[] buf = (line).getBytes();
                            DatagramPacket packet = new DatagramPacket(buf, buf.length);
                            packet.setSocketAddress(remoteAddress);
                            socket.send(packet);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}