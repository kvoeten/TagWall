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
package com.kazvoeten.tagwall.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.PacketDecoder;
import net.PacketEncoder;

/**
 *
 * @author Kaz Voeten
 */
public class Server extends Thread {

    private ServerBootstrap sb;
    private Channel serverChannel;
    private EventLoopGroup bossGroup, workerGroup;

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        sb = new ServerBootstrap();

        sb.group(bossGroup, workerGroup);
        sb.channel(NioServerSocketChannel.class);
        sb.childHandler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel c) throws Exception {
                c.pipeline().addLast(new PacketDecoder(), new SocketHandler(), new PacketEncoder());
            }
        });

        sb.option(ChannelOption.SO_BACKLOG, 5); //Change this number if we expect more units.

        sb.childOption(ChannelOption.TCP_NODELAY, true);
        sb.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture f = sb.bind(2626).sync();
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Server succesfully initialized.");
            serverChannel = f.channel();
            serverChannel.closeFuture().sync();
        } catch (InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Server failed to initialize.", ex);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
