/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.kazvoeten.tagwall.client;

import com.kazvoeten.tagwall.client.rfid.RFIDReader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.PacketDecoder;
import net.PacketEncoder;
import org.pmw.tinylog.Configurator;

/**
 *
 * @author Kaz Voeten
 */
public class Client extends Thread {

    private Channel channel;
    private EventLoopGroup workerGroup;

    @Override
    public void run() {
        connect();
    }

    private void connect() {
        workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bs = new Bootstrap();
            bs.group(workerGroup);
            bs.channel(NioSocketChannel.class);
            bs.option(ChannelOption.SO_KEEPALIVE, true);
            bs.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new PacketDecoder(), new SocketHandler(), new PacketEncoder());
                }
            });

            ChannelFuture cf = bs.connect("217.182.71.24", 2626);
            channel = cf.channel();
            cf.sync();
            channel.closeFuture().sync();
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Unable to connect to the server.", ex);
        } finally {
            workerGroup.shutdownGracefully();
            try {
                Thread.sleep(30 * 1000);
                connect();
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Failed to connect to the server.", ex);
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        Client client = new Client();
        client.start();

        RFIDReader rfidReader = new RFIDReader();
        rfidReader.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                RFIDReader.shutDown();
            }
        });
    }
}
